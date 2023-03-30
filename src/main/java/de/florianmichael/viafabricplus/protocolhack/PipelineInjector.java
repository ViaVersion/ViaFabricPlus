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
package de.florianmichael.viafabricplus.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viafabricplus.protocolhack.constants.BedrockRakNetConstants;
import de.florianmichael.viafabricplus.protocolhack.constants.PreNettyConstants;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.DisconnectHandler;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.PingEncapsulationCodec;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.RakMessageEncapsulationCodec;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPingEncoder;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPongDecoder;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyDecoder;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyEncoder;
import de.florianmichael.viafabricplus.protocolhack.replacement.ViaFabricPlusVLBViaDecodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import de.florianmichael.vialoadingbase.netty.handler.VLBViaEncodeHandler;
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
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPingEncoder;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPongDecoder;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

public class PipelineInjector {

    /**
     * This method adds the channel handlers required for ViaVersion, ViaBackwards, ViaAprilFools and ViaLegacy, it also tracks the Via and Minecraft Connection
     */
    public static void hookProtocolHack(final ClientConnection connection, final Channel channel, final InetSocketAddress address) {
        if (ProtocolHack.getForcedVersions().containsKey(address)) {
            channel.attr(ProtocolHack.FORCED_VERSION).set(ProtocolHack.getForcedVersions().get(address));
            ProtocolHack.getForcedVersions().remove(address);
        }
        final UserConnection user = new UserConnectionImpl(channel, true);
        channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).set(user);
        channel.attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).set(connection);

        new ProtocolPipelineImpl(user);

        channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new VLBViaEncodeHandler(user));
        channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new ViaFabricPlusVLBViaDecodeHandler(user));

        if (ProtocolHack.getTargetVersion(channel).isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

            channel.pipeline().addBefore("prepender", PreNettyConstants.HANDLER_ENCODER_NAME, new VFPPreNettyEncoder(user));
            channel.pipeline().addBefore("splitter", PreNettyConstants.HANDLER_DECODER_NAME, new VFPPreNettyDecoder(user));
        }
    }

    /**
     * This method represents the rak net connection for bedrock edition, it's a replacement of the ClientConnection#connect method
     */
    public static void connectRakNet(final ClientConnection clientConnection, final InetSocketAddress address, final Lazy<? extends MultithreadEventLoopGroup> lazy, final Class<? extends AbstractChannel> channelType) {
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

                PipelineInjector.hookProtocolHack(clientConnection, channel, address);
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
