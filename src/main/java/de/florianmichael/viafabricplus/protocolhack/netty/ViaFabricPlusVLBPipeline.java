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
package de.florianmichael.viafabricplus.protocolhack.netty;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.DisconnectHandle;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.codec.PingEncapsulationCodec;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.codec.RakMessageEncapsulationCodec;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.codec.library_fix.FixedUnconnectedPingEncoder;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.codec.library_fix.FixedUnconnectedPongDecoder;
import de.florianmichael.vialoadingbase.model.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.netty.VLBPipeline;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.BatchLengthCodec;
import net.raphimc.viabedrock.netty.PacketEncapsulationCodec;
import net.raphimc.viabedrock.protocol.BedrockBaseProtocol;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.netty.PreNettyLengthPrepender;
import net.raphimc.vialegacy.netty.PreNettyLengthRemover;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPingEncoder;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPongDecoder;

import java.net.InetSocketAddress;

public class ViaFabricPlusVLBPipeline extends VLBPipeline {
    // ViaBedrock (RakNet and Codec based)
    public final static String VIA_BEDROCK_DISCONNECT_HANDLER_NAME = "via-bedrock-disconnect-handler";
    public final static String VIA_BEDROCK_FRAME_ENCAPSULATION_HANDLER_NAME = "via-bedrock-frame-encapsulation";
    public final static String VIA_BEDROCK_PING_ENCAPSULATION_HANDLER_NAME = "via-bedrock-ping-encapsulation";
    public final static String VIA_BEDROCK_PACKET_ENCAPSULATION_HANDLER_NAME = "via-bedrock-packet-encapsulation";
    public final static String VIA_BEDROCK_BATCH_LENGTH_HANDLER_NAME = "via-bedrock-batch_length";
    public final static String VIA_BEDROCK_COMPRESSION_HANDLER_NAME = "via-bedrock-compression";
    public final static String VIA_BEDROCK_ENCRYPTION_HANDLER_NAME = "via-bedrock-encryption";

    // ViaLegacy (pre Netty)
    public static final String VIA_LEGACY_DECODER_HANDLER_NAME = "via-legacy-decoder";
    public static final String VIA_LEGACY_ENCODER_HANDLER_NAME = "via-legacy-encoder";

    private final InetSocketAddress address;
    private final ComparableProtocolVersion version;

    public ViaFabricPlusVLBPipeline(UserConnection info, final InetSocketAddress address, final ComparableProtocolVersion version) {
        super(info);
        
        this.address = address;
        this.version = version;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // ViaLoadingBase
        super.handlerAdded(ctx);

        final ChannelPipeline pipeline = ctx.channel().pipeline();

        // ViaLegacy
        if (this.version.isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            getInfo().getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

            pipeline.addBefore("splitter", VIA_LEGACY_DECODER_HANDLER_NAME, new PreNettyLengthPrepender(getInfo()));
            pipeline.addBefore("prepender", VIA_LEGACY_ENCODER_HANDLER_NAME, new PreNettyLengthRemover(getInfo()));
        }

        // ViaBedrock
        if (this.version.isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            getInfo().getProtocolInfo().getPipeline().add(BedrockBaseProtocol.INSTANCE);

            pipeline.replace("splitter", VIA_BEDROCK_BATCH_LENGTH_HANDLER_NAME, new BatchLengthCodec());

            pipeline.addBefore(VIA_BEDROCK_BATCH_LENGTH_HANDLER_NAME, VIA_BEDROCK_DISCONNECT_HANDLER_NAME, new DisconnectHandle());
            pipeline.addAfter(VIA_BEDROCK_DISCONNECT_HANDLER_NAME, VIA_BEDROCK_FRAME_ENCAPSULATION_HANDLER_NAME, new RakMessageEncapsulationCodec());
            pipeline.addAfter(VIA_BEDROCK_BATCH_LENGTH_HANDLER_NAME, VIA_BEDROCK_PACKET_ENCAPSULATION_HANDLER_NAME, new PacketEncapsulationCodec());

            // Replaced by the codecs
            pipeline.remove("prepender");
            pipeline.remove("timeout");

            // Pinging in RakNet is something different
            if (ProtocolHack.getRakNetPingSessions().contains(address)) {
                { // Temporary fix for the ping encoder
                    final RakClientChannel rakChannel = (RakClientChannel) ctx.channel();

                    rakChannel.parent().pipeline().replace(UnconnectedPingEncoder.NAME, UnconnectedPingEncoder.NAME, new FixedUnconnectedPingEncoder(rakChannel));
                    rakChannel.parent().pipeline().replace(UnconnectedPongDecoder.NAME, UnconnectedPongDecoder.NAME, new FixedUnconnectedPongDecoder(rakChannel));
                }

                pipeline.replace(VIA_BEDROCK_FRAME_ENCAPSULATION_HANDLER_NAME, VIA_BEDROCK_PING_ENCAPSULATION_HANDLER_NAME, new PingEncapsulationCodec(address));

                pipeline.remove(VIA_BEDROCK_PACKET_ENCAPSULATION_HANDLER_NAME);
                pipeline.remove(VIA_BEDROCK_BATCH_LENGTH_HANDLER_NAME);
            }
        }
    }

    @Override
    public String getDecoderHandlerName() {
        return "decoder";
    }

    @Override
    public String getEncoderHandlerName() {
        return "encoder";
    }

    @Override
    public String getDecompressionHandlerName() {
        return "decompress";
    }

    @Override
    public String getCompressionHandlerName() {
        return "compress";
    }
}
