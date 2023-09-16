/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.protocolhack.provider.viaversion;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.base.settings.groups.GeneralSettings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.handler.PacketSizeLogger;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.PerformanceLog;
import net.raphimc.vialoader.util.VersionEnum;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ViaFabricPlusBaseVersionProvider extends BaseVersionProvider {

    // Based on https://github.com/ViaVersion/ViaFabric/blob/main/viafabric-mc119/src/main/java/com/viaversion/fabric/mc119/service/ProtocolAutoDetector.java
    private final static LoadingCache<InetSocketAddress, CompletableFuture<VersionEnum>> AUTO_DETECTION_CACHE = CacheBuilder.newBuilder().
            expireAfterWrite(10, TimeUnit.MINUTES).
            build(CacheLoader.from(address -> {
                CompletableFuture<VersionEnum> future = new CompletableFuture<>();

                try {
                    final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
                    final boolean useEpoll = Epoll.isAvailable() && MinecraftClient.getInstance().options.shouldUseNativeTransport();

                    final ChannelFuture channelFuture = new Bootstrap().group((useEpoll ? ClientConnection.EPOLL_CLIENT_IO_GROUP : ClientConnection.CLIENT_IO_GROUP).get()).handler(new ChannelInitializer<>() {
                        protected void initChannel(@NotNull Channel channel) {
                            try {
                                channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                                channel.config().setOption(ChannelOption.IP_TOS, 0x18);
                            } catch (ChannelException ignored) {
                            }

                            ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
                            ClientConnection.addHandlers(channelPipeline, NetworkSide.CLIENTBOUND, new PacketSizeLogger(new PerformanceLog()));
                            channelPipeline.addLast("packet_handler", clientConnection);
                        }
                    }).channel(useEpoll ? EpollSocketChannel.class : NioSocketChannel.class).connect(address);

                    channelFuture.addListener(future1 -> {
                        if (!future1.isSuccess()) {
                            future.completeExceptionally(future1.cause());
                        } else {
                            clientConnection.connect(address.getHostString(), address.getPort(), new ClientQueryPacketListener() {
                                @Override
                                public void onResponse(QueryResponseS2CPacket packet) {
                                    if (packet.metadata() != null && packet.metadata().version().isPresent()) {
                                        final VersionEnum version = VersionEnum.fromProtocolId(packet.metadata().version().get().protocolVersion());
                                        future.complete(version);

                                        ViaFabricPlus.LOGGER.info("Auto-detected " + version + " for " + address);
                                    } else {
                                        future.completeExceptionally(new IllegalArgumentException("Null version in query response"));
                                    }
                                    clientConnection.disconnect(Text.empty());
                                }

                                @Override
                                public void onPingResult(PingResultS2CPacket packet) {
                                    clientConnection.disconnect(Text.literal("Ping not requested!"));
                                }

                                @Override
                                public void onDisconnected(Text reason) {
                                    future.completeExceptionally(new IllegalStateException(reason.getString()));
                                }

                                @Override
                                public boolean isConnectionOpen() {
                                    return channelFuture.channel().isOpen();
                                }
                            });
                            clientConnection.send(new QueryRequestC2SPacket());
                        }
                    });
                } catch (Throwable throwable) { // You never know...
                    future.completeExceptionally(throwable);
                }

                return future;
            }));

    @Override
    public int getClosestServerProtocol(UserConnection connection) throws Exception {
        if (connection.isClientSide() && !MinecraftClient.getInstance().isInSingleplayer()) {
            if (GeneralSettings.INSTANCE.autoDetectVersion.getValue()) {
                final SocketAddress target = connection.getChannel().remoteAddress();
                if (target instanceof final InetSocketAddress socketAddress) {
                    AUTO_DETECTION_CACHE.get(socketAddress).whenComplete((version, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                            return;
                        }
                        if (version != null) {
                            final VersionEnum remapped = VersionEnum.fromProtocolId(version.getVersion());
                            if (remapped != null) {
                                ProtocolHack.getForcedVersions().put(socketAddress, remapped);
                            }
                        }
                    });
                }
            }
            return ProtocolHack.getTargetVersion(connection.getChannel()).getVersion();
        }
        return super.getClosestServerProtocol(connection);
    }
}
