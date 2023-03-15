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
package de.florianmichael.viafabricplus.protocolhack.replacement;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.util.PipelineUtil;
import de.florianmichael.vialoadingbase.netty.VLBViaDecodeHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.logging.Level;

public class VFPVLBViaDecodeHandler extends VLBViaDecodeHandler {

    public VFPVLBViaDecodeHandler(UserConnection info) {
        super(info);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out) throws Exception {
        try {
            super.decode(ctx, bytebuf, out);
        } catch (Throwable e) {
            if (PipelineUtil.containsCause(e, CancelCodecException.class)) throw e;
            Via.getPlatform().getLogger().log(Level.SEVERE, "ViaLoadingBase Packet Error occurred", e);
            e.printStackTrace();
        }
    }
}
