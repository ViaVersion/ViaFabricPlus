/*
 * This file is part of ViaBedrock - https://github.com/RaphiMC/ViaBedrock
 * Copyright (C) 2023 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.platform.raknet.library_fix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.RakPong;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.handler.codec.raknet.AdvancedChannelInboundHandler;

import static org.cloudburstmc.netty.channel.raknet.RakConstants.ID_UNCONNECTED_PONG;

// Temporary fix until the library fixes the issue
public class FixedUnconnectedPongDecoder extends AdvancedChannelInboundHandler<DatagramPacket> {

    private final RakClientChannel rakClientChannel;

    public FixedUnconnectedPongDecoder(final RakClientChannel rakClientChannel) {
        this.rakClientChannel = rakClientChannel;
    }

    @Override
    protected boolean acceptInboundMessage(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!super.acceptInboundMessage(ctx, msg)) {
            return false;
        }

        DatagramPacket packet = (DatagramPacket) msg;
        ByteBuf buf = packet.content();
        return buf.isReadable() && buf.getUnsignedByte(buf.readerIndex()) == ID_UNCONNECTED_PONG;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ByteBuf buf = packet.content();
        buf.readUnsignedByte(); // Packet ID

        long pingTime = buf.readLong();
        long guid = buf.readLong();

        ByteBuf magicBuf = this.rakClientChannel.config().getOption(RakChannelOption.RAK_UNCONNECTED_MAGIC);
        if (!buf.isReadable(magicBuf.readableBytes()) || !ByteBufUtil.equals(buf.readSlice(magicBuf.readableBytes()), magicBuf)) {
            // Magic does not match
            return;
        }

        ByteBuf pongData = Unpooled.EMPTY_BUFFER;
        if (buf.isReadable(2)) { // Length
            pongData = buf.readRetainedSlice(buf.readUnsignedShort());
        }
        ctx.fireChannelRead(new RakPong(pingTime, guid, pongData, packet.sender()));
    }

}
