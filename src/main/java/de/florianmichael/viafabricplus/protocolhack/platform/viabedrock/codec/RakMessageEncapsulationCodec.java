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
package de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.cloudburstmc.netty.channel.raknet.RakReliability;
import org.cloudburstmc.netty.channel.raknet.packet.RakMessage;

import java.util.List;

public class RakMessageEncapsulationCodec extends MessageToMessageCodec<RakMessage, ByteBuf> {

    private static final int FRAME_ID = 0xFE;

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        final CompositeByteBuf buf = ctx.alloc().compositeBuffer(2);
        try {
            buf.addComponent(true, ctx.alloc().ioBuffer(1).writeByte(FRAME_ID));
            buf.addComponent(true, msg.retainedSlice());
            out.add(buf.retain());
        } finally {
            buf.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, RakMessage msg, List<Object> out) {
        if (msg.channel() != 0 && msg.reliability() != RakReliability.RELIABLE_ORDERED) {
            return;
        }
        final ByteBuf in = msg.content();
        if (!in.isReadable()) {
            return;
        }
        final int id = in.readUnsignedByte();
        if (id != FRAME_ID) {
            throw new IllegalStateException("Invalid frame ID: " + id);
        }
        out.add(in.readRetainedSlice(in.readableBytes()));
    }

}
