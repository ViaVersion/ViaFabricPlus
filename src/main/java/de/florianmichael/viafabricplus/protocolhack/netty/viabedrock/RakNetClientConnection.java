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
package de.florianmichael.viafabricplus.protocolhack.netty.viabedrock;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Lazy;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RakNetClientConnection {
    private final static List<InetSocketAddress> rakNetPingSessions = new ArrayList<>();

    public static void connectRakNet(final ClientConnection clientConnection, final InetSocketAddress address, final Lazy lazy, final Class channelType) {
        final Bootstrap nettyBoostrap = new Bootstrap().group((EventLoopGroup) lazy.get()).handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel channel) {
                try {
                    channel.config().setOption(RakChannelOption.RAK_PROTOCOL_VERSION, 11);
                    channel.config().setOption(RakChannelOption.RAK_CONNECT_TIMEOUT, 4_000L);
                    channel.config().setOption(RakChannelOption.RAK_SESSION_TIMEOUT, 30_000L);
                    channel.config().setOption(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong());
                } catch (Exception ignored) {
                }
                ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
                ClientConnection.addHandlers(channelPipeline, NetworkSide.CLIENTBOUND);

                channelPipeline.addLast("packet_handler", clientConnection);

                ProtocolHack.injectVLBPipeline(clientConnection, channel, address);
            }
        }).channelFactory(channelType == EpollSocketChannel.class ? RakChannelFactory.client(EpollDatagramChannel.class) : RakChannelFactory.client(NioDatagramChannel.class));

        if (getRakNetPingSessions().contains(address)) {
            nettyBoostrap.bind(new InetSocketAddress(0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE).syncUninterruptibly();
        } else {
            nettyBoostrap.connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        }
    }

    public static List<InetSocketAddress> getRakNetPingSessions() {
        return rakNetPingSessions;
    }
}
