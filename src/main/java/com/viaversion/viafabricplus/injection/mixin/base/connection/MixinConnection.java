/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.injection.mixin.base.connection;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.viaversion.viafabricplus.base.bedrock.NetherNetInetSocketAddress;
import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.injection.access.base.IEventLoopGroupHolder;
import com.viaversion.viafabricplus.injection.access.base.ILocalSampleLogger;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.netty.ViaFabricPlusVLLegacyPipeline;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.vialoader.netty.CompressionReorderEvent;
import com.viaversion.vialoader.netty.VLLegacyPipeline;
import com.viaversion.vialoader.netty.VLPipeline;
import com.viaversion.vialoader.netty.viabedrock.RakNetPingEncapsulationCodec;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import dev.kastle.netty.channel.nethernet.NetherNetChannelFactory;
import dev.kastle.netty.channel.nethernet.signaling.NetherNetXboxSignaling;
import dev.kastle.webrtc.PeerConnectionFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.crypto.Cipher;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.RakNetStatusProtocol;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Connection.class)
public abstract class MixinConnection extends SimpleChannelInboundHandler<Packet<?>> implements IConnection {

    @Shadow
    public Channel channel;

    @Shadow
    private boolean encrypted;

    @Shadow
    public abstract void channelActive(@NotNull ChannelHandlerContext context) throws Exception;

    @Unique
    private UserConnection viaFabricPlus$userConnection;

    @Unique
    private ProtocolVersion viaFabricPlus$serverVersion;

    @Unique
    private Cipher viaFabricPlus$decryptionCipher;

    @Inject(method = "setupCompression", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        // Compression enabled and elements put into pipeline, move via handlers
        channel.pipeline().fireUserEventTriggered(CompressionReorderEvent.INSTANCE);
    }

    @Inject(method = "setEncryptionKey", at = @At("HEAD"), cancellable = true)
    private void storeDecryptionCipher(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (this.viaFabricPlus$serverVersion != null /* This happens when opening a lan server and people are joining */ && this.viaFabricPlus$serverVersion.olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel();

            // Minecraft 1.6.4 supports split encryption/decryption which means the server can only enable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore, we need to store the cipher instance.
            this.viaFabricPlus$decryptionCipher = decryptionCipher;

            // Enabling the encryption side
            if (encryptionCipher == null) {
                throw new IllegalStateException("Encryption cipher is null");
            }

            this.encrypted = true;
            this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, HandlerNames.ENCRYPT, new CipherEncoder(encryptionCipher));
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (BedrockProtocolVersion.bedrockLatest.equals(this.viaFabricPlus$serverVersion)) { // Call channelActive manually when the channel is registered
            this.channelActive(ctx);
        }
    }

    @WrapWithCondition(method = "channelActive", at = @At(value = "INVOKE", target = "Lio/netty/channel/SimpleChannelInboundHandler;channelActive(Lio/netty/channel/ChannelHandlerContext;)V", remap = false))
    private boolean dontCallChannelActiveTwice(SimpleChannelInboundHandler<Packet<?>> instance, ChannelHandlerContext channelHandlerContext) {
        return !BedrockProtocolVersion.bedrockLatest.equals(this.viaFabricPlus$serverVersion);
    }

    @Inject(method = "connectToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;"))
    private static void setTargetVersion(InetSocketAddress inetSocketAddress, EventLoopGroupHolder eventLoopGroupHolder, LocalSampleLogger localSampleLogger, CallbackInfoReturnable<Connection> cir, @Local Connection clientConnection) {
        // Set the target version stored in the PerformanceLog field to the ClientConnection instance
        if (localSampleLogger instanceof ILocalSampleLogger mixinMultiValueDebugSampleLogImpl && mixinMultiValueDebugSampleLogImpl.viaFabricPlus$getForcedVersion() != null) {
            ((IConnection) clientConnection).viaFabricPlus$setTargetVersion(mixinMultiValueDebugSampleLogImpl.viaFabricPlus$getForcedVersion());
        }
    }

    @WrapWithCondition(method = "connectToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setBandwidthLogger(Lnet/minecraft/util/debugchart/LocalSampleLogger;)V"))
    private static boolean dontSetPerformanceLog(Connection instance, LocalSampleLogger packetSizeLog) {
        // We need to restore vanilla behavior since we use the PerformanceLog as a way to store the target version
        return !(packetSizeLog instanceof ILocalSampleLogger mixinMultiValueDebugSampleLogImpl) || mixinMultiValueDebugSampleLogImpl.viaFabricPlus$getForcedVersion() == null;
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void setTargetVersion(InetSocketAddress inetSocketAddress, EventLoopGroupHolder eventLoopGroupHolder, Connection connection, CallbackInfoReturnable<ChannelFuture> cir, @Local(argsOnly = true) LocalRef<EventLoopGroupHolder> eventLoopGroupHolderRef) {
        ProtocolVersion targetVersion = ((IConnection) connection).viaFabricPlus$getTargetVersion();
        if (targetVersion == null) { // No server specific override
            targetVersion = ProtocolTranslator.getTargetVersion();
        }
        if (targetVersion == ProtocolTranslator.AUTO_DETECT_PROTOCOL) { // Auto-detect enabled (when pinging always use native version). Auto-detect is resolved in ConnectScreen mixin
            targetVersion = ProtocolTranslator.NATIVE_VERSION;
        }
        ((IConnection) connection).viaFabricPlus$setTargetVersion(targetVersion);

        if (BedrockProtocolVersion.bedrockLatest.equals(targetVersion) && (eventLoopGroupHolder.channelCls() == KQueueSocketChannel.class || inetSocketAddress instanceof NetherNetInetSocketAddress)) { // RakNet does not support KQueue, switch to NIO. NetherNet requires NIO
            final EventLoopGroupHolder newEventLoopGroupHolder = EventLoopGroupHolder.remote(false);
            ((IEventLoopGroupHolder) newEventLoopGroupHolder).viaFabricPlus$setConnecting(((IEventLoopGroupHolder) eventLoopGroupHolder).viaFabricPlus$isConnecting());
            eventLoopGroupHolderRef.set(newEventLoopGroupHolder);
        }
    }

    @WrapOperation(method = "connect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;channel(Ljava/lang/Class;)Lio/netty/bootstrap/AbstractBootstrap;", remap = false))
    private static AbstractBootstrap<?, ?> useRakNetChannelFactory(Bootstrap instance, Class<? extends Channel> channelTypeClass, Operation<AbstractBootstrap<Bootstrap, Channel>> original, @Local(argsOnly = true) InetSocketAddress address, @Local(argsOnly = true) Connection clientConnection) {
        if (BedrockProtocolVersion.bedrockLatest.equals(((IConnection) clientConnection).viaFabricPlus$getTargetVersion())) {
            if (address instanceof NetherNetInetSocketAddress) {
                return instance.channelFactory(NetherNetChannelFactory.client(new PeerConnectionFactory(), new NetherNetXboxSignaling(SaveManager.INSTANCE.getAccountsSave().getBedrockAccount().getMinecraftSession().getUpToDateUnchecked().getAuthorizationHeader())));
            } else { // RakNet
                if (channelTypeClass == NioSocketChannel.class) {
                    channelTypeClass = NioDatagramChannel.class;
                } else if (channelTypeClass == EpollSocketChannel.class) {
                    channelTypeClass = EpollDatagramChannel.class;
                } else {
                    throw new IllegalStateException("Unsupported channel type for RakNet: " + channelTypeClass);
                }
                return instance.channelFactory(RakChannelFactory.client((Class<? extends DatagramChannel>) channelTypeClass));
            }
        } else {
            return original.call(instance, channelTypeClass);
        }
    }

    @Redirect(method = "connect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;connect(Ljava/net/InetAddress;I)Lio/netty/channel/ChannelFuture;", remap = false))
    private static ChannelFuture useRakNetPingHandlers(Bootstrap instance, InetAddress inetHost, int inetPort, @Local(argsOnly = true) InetSocketAddress address, @Local(argsOnly = true) Connection clientConnection, @Local(argsOnly = true) EventLoopGroupHolder eventLoopGroupHolder) {
        if (BedrockProtocolVersion.bedrockLatest.equals(((IConnection) clientConnection).viaFabricPlus$getTargetVersion())) {
            if (address instanceof NetherNetInetSocketAddress netherNetAddress) {
                return instance.connect(netherNetAddress.getNetherNetAddress()).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, (ChannelFutureListener) f -> {
                    if (f.isSuccess()) {
                        f.channel().pipeline().remove(VLPipeline.VIABEDROCK_RAKNET_MESSAGE_CODEC_NAME);
                    }
                });
            } else if (!((IEventLoopGroupHolder) eventLoopGroupHolder).viaFabricPlus$isConnecting()) {
                // Bedrock edition / RakNet has different handlers for pinging a server
                return instance.register().syncUninterruptibly().channel().bind(new InetSocketAddress(0)).addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, (ChannelFutureListener) f -> {
                    if (f.isSuccess()) {
                        f.channel().pipeline().replace(
                            VLPipeline.VIABEDROCK_RAKNET_MESSAGE_CODEC_NAME,
                            ViaFabricPlusVLLegacyPipeline.VIABEDROCK_PING_ENCAPSULATION_HANDLER_NAME,
                            new RakNetPingEncapsulationCodec(new InetSocketAddress(inetHost, inetPort))
                        );
                        f.channel().pipeline().remove(VLPipeline.VIABEDROCK_PACKET_CODEC_NAME);
                        f.channel().pipeline().remove(HandlerNames.SPLITTER);

                        UserConnection user = ((IConnection) clientConnection).viaFabricPlus$getUserConnection();
                        user.getProtocolInfo().getPipeline().add(RakNetStatusProtocol.INSTANCE);
                    }
                });
            }
        }
        return instance.connect(inetHost, inetPort);
    }

    @Override
    public void viaFabricPlus$setupPreNettyDecryption() {
        if (this.viaFabricPlus$decryptionCipher == null) {
            throw new IllegalStateException("Decryption cipher is null");
        }

        this.encrypted = true;
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_PREPENDER_NAME, HandlerNames.DECRYPT, new CipherDecoder(this.viaFabricPlus$decryptionCipher));
    }

    @Override
    public UserConnection viaFabricPlus$getUserConnection() {
        return this.viaFabricPlus$userConnection;
    }

    @Override
    public void viaFabricPlus$setUserConnection(UserConnection connection) {
        this.viaFabricPlus$userConnection = connection;
    }

    @Override
    public ProtocolVersion viaFabricPlus$getTargetVersion() {
        return this.viaFabricPlus$serverVersion;
    }

    @Override
    public void viaFabricPlus$setTargetVersion(final ProtocolVersion serverVersion) {
        this.viaFabricPlus$serverVersion = serverVersion;
    }

}
