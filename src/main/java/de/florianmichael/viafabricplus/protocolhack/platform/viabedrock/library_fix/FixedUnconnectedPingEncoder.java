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

package de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.RakPing;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;

import static org.cloudburstmc.netty.channel.raknet.RakConstants.ID_UNCONNECTED_PING;

// Temporary fix until the library fixes the issue
public class FixedUnconnectedPingEncoder extends ChannelOutboundHandlerAdapter {

    private final RakClientChannel rakClientChannel;

    public FixedUnconnectedPingEncoder(final RakClientChannel rakClientChannel) {
        this.rakClientChannel = rakClientChannel;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof RakPing)) {
            ctx.write(msg, promise);
            return;
        }

        RakPing ping = (RakPing) msg;
        ByteBuf magicBuf = this.rakClientChannel.config().getOption(RakChannelOption.RAK_UNCONNECTED_MAGIC);
        long guid = this.rakClientChannel.config().getOption(RakChannelOption.RAK_GUID);

        ByteBuf pingBuffer = ctx.alloc().ioBuffer(magicBuf.readableBytes() + 17);
        pingBuffer.writeByte(ID_UNCONNECTED_PING);
        pingBuffer.writeLong(ping.getPingTime());
        pingBuffer.writeBytes(magicBuf, magicBuf.readerIndex(), magicBuf.readableBytes());
        pingBuffer.writeLong(guid);
        ctx.write(new DatagramPacket(pingBuffer, ping.getSender()));
    }

}
