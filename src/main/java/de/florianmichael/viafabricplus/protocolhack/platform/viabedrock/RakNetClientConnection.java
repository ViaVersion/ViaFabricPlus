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
package de.florianmichael.viafabricplus.protocolhack.platform.viabedrock;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.injection.reference.ClientConnectionReference;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.constants.BedrockRakNetConstants;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPingEncoder;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPongDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.util.Lazy;
import net.raphimc.viabedrock.netty.BatchLengthCodec;
import net.raphimc.viabedrock.netty.PacketEncapsulationCodec;
import net.raphimc.viabedrock.protocol.BedrockBaseProtocol;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPingEncoder;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPongDecoder;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

public class RakNetClientConnection {

    public static void connect(final ClientConnection clientConnection, final InetSocketAddress address, final Lazy<? extends MultithreadEventLoopGroup> lazy, final Class<? extends AbstractChannel> channelType) {
        Bootstrap nettyBoostrap = new Bootstrap();
        nettyBoostrap = nettyBoostrap.group(lazy.get());
        nettyBoostrap = nettyBoostrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel channel) throws Exception {
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

                ClientConnectionReference.hackNettyPipeline(clientConnection, channel, address);
                final UserConnection user = channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get();

                user.getProtocolInfo().getPipeline().add(BedrockBaseProtocol.INSTANCE);

                channel.pipeline().replace("splitter", BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, new BatchLengthCodec());

                channel.pipeline().addBefore(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.DISCONNECT_HANDLER_NAME, new DisconnectHandler());
                channel.pipeline().addAfter(BedrockRakNetConstants.DISCONNECT_HANDLER_NAME, BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, new RakMessageEncapsulationCodec());
                channel.pipeline().addAfter(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.PACKET_ENCAPSULATION_HANDLER_NAME, new PacketEncapsulationCodec());

                channel.pipeline().remove("prepender");
                channel.pipeline().remove("timeout");

                // Pinging in RakNet is something different
                if (ProtocolHack.getRakNetPingSessions().contains(address)) {
                    { // Temporary fix for the ping encoder
                        final RakClientChannel rakChannel = (RakClientChannel) channel;

                        rakChannel.parent().pipeline().replace(UnconnectedPingEncoder.NAME, UnconnectedPingEncoder.NAME, new FixedUnconnectedPingEncoder(rakChannel));
                        rakChannel.parent().pipeline().replace(UnconnectedPongDecoder.NAME, UnconnectedPongDecoder.NAME, new FixedUnconnectedPongDecoder(rakChannel));
                    }

                    channel.pipeline().replace(BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, BedrockRakNetConstants.PING_ENCAPSULATION_HANDLER_NAME, new PingEncapsulationCodec(address));

                    channel.pipeline().remove(BedrockRakNetConstants.PACKET_ENCAPSULATION_HANDLER_NAME);
                    channel.pipeline().remove(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME);
                }
            }
        });
        nettyBoostrap = nettyBoostrap.channelFactory(channelType == EpollSocketChannel.class ? RakChannelFactory.client(EpollDatagramChannel.class) : RakChannelFactory.client(NioDatagramChannel.class));

        if (ProtocolHack.getRakNetPingSessions().contains(address)) {
            nettyBoostrap.bind(new InetSocketAddress(0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE).syncUninterruptibly();
        } else {
            nettyBoostrap.connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        }
    }
}
