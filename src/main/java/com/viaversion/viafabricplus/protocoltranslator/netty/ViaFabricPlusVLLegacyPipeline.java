/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.protocoltranslator.netty;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol;
import com.viaversion.vialoader.netty.CompressionReorderEvent;
import com.viaversion.vialoader.netty.VLLegacyPipeline;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.handler.HandlerNames;

public final class ViaFabricPlusVLLegacyPipeline extends VLLegacyPipeline {

    public static final String VIA_FLOW_CONTROL = "via-flow-control";

    public static final String VIABEDROCK_COMPRESSION_HANDLER_NAME = "viabedrock-compression";
    public static final String VIABEDROCK_ENCRYPTION_HANDLER_NAME = "viabedrock-encryption";
    public static final String VIABEDROCK_PING_ENCAPSULATION_HANDLER_NAME = "viabedrock-ping-encapsulation";

    public ViaFabricPlusVLLegacyPipeline(UserConnection connection, ProtocolVersion version) {
        super(connection, version);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        super.handlerAdded(ctx);

        ctx.pipeline().addAfter(VIA_DECODER_NAME, VIA_FLOW_CONTROL, new NoReadFlowControlHandler());

        this.connection.getProtocolInfo().getPipeline().add(ViaFabricPlusProtocol.INSTANCE);
    }

    @Override
    protected ChannelHandler createViaDecoder() {
        return new ViaFabricPlusViaDecoder(this.connection);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // Bypass, because Krypton overwrites the entire compression instead of modifying the handlers.
        if (evt.getClass().getName().equals("me.steinborn.krypton.mod.shared.misc.KryptonPipelineEvent")) {
            if (evt.toString().equals("COMPRESSION_ENABLED")) {
                super.userEventTriggered(ctx, CompressionReorderEvent.INSTANCE);
                ViaFabricPlusImpl.INSTANCE.logger().info("Compression has been re-ordered after \"Krypton\"");
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected String decompressName() {
        return HandlerNames.DECOMPRESS;
    }

    @Override
    protected String compressName() {
        return HandlerNames.COMPRESS;
    }

    @Override
    protected String packetDecoderName() {
        return HandlerNames.INBOUND_CONFIG;
    }

    @Override
    protected String packetEncoderName() {
        return HandlerNames.ENCODER;
    }

    @Override
    protected String lengthSplitterName() {
        return HandlerNames.SPLITTER;
    }

    @Override
    protected String lengthPrependerName() {
        return HandlerNames.PREPENDER;
    }

}
