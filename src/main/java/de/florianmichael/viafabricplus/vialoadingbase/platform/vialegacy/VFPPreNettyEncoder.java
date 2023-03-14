/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.vialoadingbase.platform.vialegacy;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.raphimc.vialegacy.netty.PreNettyEncoder;

public class VFPPreNettyEncoder extends PreNettyEncoder {

    public VFPPreNettyEncoder(UserConnection user) {
        super(user);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        if (Via.getManager().isDebug()) {
            final ByteBuf myBuf = in.copy();
            Type.VAR_INT.readPrimitive(myBuf); // length
            Via.getPlatform().getLogger().info("Encoding pre netty packet: " + (Type.VAR_INT.readPrimitive(myBuf) & 255));
        }
        super.encode(ctx, in, out);
    }
}
