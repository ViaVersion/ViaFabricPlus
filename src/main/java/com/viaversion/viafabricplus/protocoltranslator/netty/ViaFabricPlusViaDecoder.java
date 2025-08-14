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
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import com.viaversion.viafabricplus.util.ChatUtil;
import com.viaversion.vialoader.netty.ViaDecoder;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ViaFabricPlusViaDecoder extends ViaDecoder {

    public ViaFabricPlusViaDecoder(UserConnection connection) {
        super(connection);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final int mode = GeneralSettings.INSTANCE.ignorePacketTranslationErrors.getIndex();
        if (mode == 0) {
            // Mode 0: Just pass the exception to the next handler
            super.channelRead(ctx, msg);
        } else {
            try {
                super.channelRead(ctx, msg);
            } catch (Throwable t) {
                // Mode 2: Just log the error
                ViaFabricPlusImpl.INSTANCE.getLogger().error("Error occurred while decoding packet in ViaFabricPlus decoder", t);
                if (mode == 1) {
                    // Mode 1: Send a message to the player that an error occurred and log the error
                    ChatUtil.sendPrefixedMessage(Text.translatable("translation.viafabricplus.packet_error").formatted(Formatting.RED));
                }
            }
        }
    }

}
