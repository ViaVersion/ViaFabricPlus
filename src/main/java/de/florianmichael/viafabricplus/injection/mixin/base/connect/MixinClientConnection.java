/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.base.connect;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.event.DisconnectCallback;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.injection.access.IPerformanceLog;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.netty.ViaFabricPlusVLLegacyPipeline;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.PerformanceLog;
import net.raphimc.vialoader.netty.CompressionReorderEvent;
import net.raphimc.vialoader.netty.VLLegacyPipeline;
import net.raphimc.vialoader.netty.VLPipeline;
import net.raphimc.vialoader.netty.viabedrock.PingEncapsulationCodec;
import net.raphimc.vialoader.util.VersionEnum;
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

import javax.crypto.Cipher;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection extends SimpleChannelInboundHandler<Packet<?>> implements IClientConnection {

    @Shadow
    public Channel channel;

    @Shadow
    private boolean encrypted;

    @Shadow
    public abstract void channelActive(@NotNull ChannelHandlerContext context) throws Exception;

    @Unique
    private UserConnection viaFabricPlus$userConnection;

    @Unique
    private VersionEnum viaFabricPlus$serverVersion;

    @Unique
    private Cipher viaFabricPlus$decryptionCipher;


    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(CompressionReorderEvent.INSTANCE);
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    private void storeDecryptionCipher(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (this.viaFabricPlus$serverVersion.isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            ci.cancel();
            this.viaFabricPlus$decryptionCipher = decryptionCipher;
            this.viaFabricPlus$setupPreNettyEncryption(encryptionCipher);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (VersionEnum.bedrockLatest.equals(this.viaFabricPlus$serverVersion)) {
            this.channelActive(ctx);
        }
    }

    @Redirect(method = "channelActive", at = @At(value = "INVOKE", target = "Lio/netty/channel/SimpleChannelInboundHandler;channelActive(Lio/netty/channel/ChannelHandlerContext;)V"), remap = false)
    private void dontCallChannelActive(SimpleChannelInboundHandler<Packet<?>> instance, ChannelHandlerContext ctx) throws Exception {
        if (!VersionEnum.bedrockLatest.equals(this.viaFabricPlus$serverVersion)) {
            super.channelActive(ctx);
        }
    }

    @Inject(method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/profiler/PerformanceLog;)Lnet/minecraft/network/ClientConnection;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;", shift = At.Shift.BEFORE))
    private static void setTargetVersion(InetSocketAddress address, boolean useEpoll, PerformanceLog packetSizeLog, CallbackInfoReturnable<ClientConnection> cir, @Local ClientConnection clientConnection) {
        if (packetSizeLog instanceof IPerformanceLog mixinPerformanceLog && mixinPerformanceLog.viaFabricPlus$getForcedVersion() != null) {
            ((IClientConnection) clientConnection).viaFabricPlus$setTargetVersion(mixinPerformanceLog.viaFabricPlus$getForcedVersion());
        }
    }

    @Redirect(method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/profiler/PerformanceLog;)Lnet/minecraft/network/ClientConnection;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;resetPacketSizeLog(Lnet/minecraft/util/profiler/PerformanceLog;)V"))
    private static void dontSetPerformanceLog(ClientConnection instance, PerformanceLog performanceLog) {
        if (performanceLog instanceof IPerformanceLog mixinPerformanceLog && mixinPerformanceLog.viaFabricPlus$getForcedVersion() != null) {
            return;
        }

        instance.resetPacketSizeLog(performanceLog);
    }

    @Inject(method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;", at = @At("HEAD"))
    private static void setTargetVersion(InetSocketAddress address, boolean useEpoll, ClientConnection connection, CallbackInfoReturnable<ChannelFuture> cir) {
        if (((IClientConnection) connection).viaFabricPlus$getTargetVersion() == null) {
            ((IClientConnection) connection).viaFabricPlus$setTargetVersion(ProtocolHack.getTargetVersion());
        }
    }

    @Redirect(method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;channel(Ljava/lang/Class;)Lio/netty/bootstrap/AbstractBootstrap;", remap = false))
    private static AbstractBootstrap<?, ?> useRakNetChannelFactory(Bootstrap instance, Class<? extends Channel> channelTypeClass, @Local(argsOnly = true) ClientConnection clientConnection) {
        if (VersionEnum.bedrockLatest.equals(((IClientConnection) clientConnection).viaFabricPlus$getTargetVersion())) {
            return instance.channelFactory(channelTypeClass == EpollSocketChannel.class ?
                            RakChannelFactory.client(EpollDatagramChannel.class) :
                            RakChannelFactory.client(NioDatagramChannel.class)
            );
        }

        return instance.channel(channelTypeClass);
    }

    @Redirect(method = "connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;connect(Ljava/net/InetAddress;I)Lio/netty/channel/ChannelFuture;"))
    private static ChannelFuture useRakNetPingHandlers(Bootstrap instance, InetAddress inetHost, int inetPort, @Local(argsOnly = true) ClientConnection clientConnection, @Local(argsOnly = true) boolean isConnecting) {
        if (VersionEnum.bedrockLatest.equals(((IClientConnection) clientConnection).viaFabricPlus$getTargetVersion()) && !isConnecting) {
            return instance.register().syncUninterruptibly().channel().bind(new InetSocketAddress(0)).
                    addListeners(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE, (ChannelFutureListener) f -> {
                        if (f.isSuccess()) {
                            f.channel().pipeline().replace(
                                    VLPipeline.VIABEDROCK_FRAME_ENCAPSULATION_HANDLER_NAME,
                                    ViaFabricPlusVLLegacyPipeline.VIABEDROCK_PING_ENCAPSULATION_HANDLER_NAME,
                                    new PingEncapsulationCodec(new InetSocketAddress(inetHost, inetPort))
                            );
                            f.channel().pipeline().remove(VLPipeline.VIABEDROCK_PACKET_ENCAPSULATION_HANDLER_NAME);
                            f.channel().pipeline().remove("splitter");
                        }
                    });
        }

        return instance.connect(inetHost, inetPort);
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    private void callDisconnectCallback(Text disconnectReason, CallbackInfo ci) {
        DisconnectCallback.EVENT.invoker().onDisconnect();
    }

    @Unique
    public void viaFabricPlus$setupPreNettyEncryption(final Cipher encryptionCipher) {
        if (encryptionCipher == null) {
            throw new IllegalStateException("Encryption cipher is null");
        }

        this.encrypted = true;
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_REMOVER_NAME, "encrypt", new PacketEncryptor(encryptionCipher));
    }

    @Override
    public void viaFabricPlus$setupPreNettyDecryption() {
        if (this.viaFabricPlus$decryptionCipher == null) {
            throw new IllegalStateException("Decryption cipher is null");
        }

        this.encrypted = true;
        this.channel.pipeline().addBefore(VLLegacyPipeline.VIALEGACY_PRE_NETTY_LENGTH_PREPENDER_NAME, "decrypt", new PacketDecryptor(this.viaFabricPlus$decryptionCipher));
    }

    @Override
    public UserConnection viaFabricPlus$getUserConnection() {
        return this.viaFabricPlus$userConnection;
    }

    @Override
    public void viaFabricPlus$setUserConnection(UserConnection userConnection) {
        this.viaFabricPlus$userConnection = userConnection;
    }

    @Override
    public VersionEnum viaFabricPlus$getTargetVersion() {
        return this.viaFabricPlus$serverVersion;
    }

    @Override
    public void viaFabricPlus$setTargetVersion(final VersionEnum serverVersion) {
        this.viaFabricPlus$serverVersion = serverVersion;
    }

}
