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

package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.viaversion.viafabricplus.base.bedrock.NetherNetInetSocketAddress;
import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IEventLoopGroupHolder;
import com.viaversion.viafabricplus.protocoltranslator.netty.ViaFabricPlusVLLegacyPipeline;
import com.viaversion.viafabricplus.save.SaveManager;
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
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.RakNetStatusProtocol;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Mixin(value = Connection.class, priority = 1001) // Apply after connection/MixinConnection
public abstract class MixinConnection extends SimpleChannelInboundHandler<Packet<?>> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (BedrockProtocolVersion.bedrockLatest.equals(((IConnection) this).viaFabricPlus$getTargetVersion())) { // Call channelActive manually when the channel is registered
            this.channelActive(ctx);
        }
    }

    @WrapWithCondition(method = "channelActive", at = @At(value = "INVOKE", target = "Lio/netty/channel/SimpleChannelInboundHandler;channelActive(Lio/netty/channel/ChannelHandlerContext;)V", remap = false))
    private boolean dontCallChannelActiveTwice(SimpleChannelInboundHandler<Packet<?>> instance, ChannelHandlerContext channelHandlerContext) {
        return !BedrockProtocolVersion.bedrockLatest.equals(((IConnection) this).viaFabricPlus$getTargetVersion());
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void setTargetVersion(InetSocketAddress inetSocketAddress, EventLoopGroupHolder eventLoopGroupHolder, Connection connection, CallbackInfoReturnable<ChannelFuture> cir, @Local(argsOnly = true) LocalRef<EventLoopGroupHolder> eventLoopGroupHolderRef) {
        final ProtocolVersion targetVersion = ((IConnection) connection).viaFabricPlus$getTargetVersion();
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
                final String authorizationHeader = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount().getMinecraftSession().getUpToDateUnchecked().getAuthorizationHeader();
                return instance.channelFactory(NetherNetChannelFactory.client(new PeerConnectionFactory(), new NetherNetXboxSignaling(authorizationHeader)));
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

}
