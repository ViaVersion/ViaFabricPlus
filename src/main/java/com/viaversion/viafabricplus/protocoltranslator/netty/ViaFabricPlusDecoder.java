/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.platform.ViaDecodeHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class ViaFabricPlusDecoder extends ViaDecodeHandler {

    public ViaFabricPlusDecoder(UserConnection connection) {
        super(connection);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause instanceof CancelCodecException) {
            return;
        }

        final int mode = GeneralSettings.INSTANCE.ignorePacketTranslationErrors.getIndex();
        if (mode > 0) {
            ViaFabricPlusImpl.INSTANCE.getLogger().error("Error occurred while decoding packet in ViaFabricPlus decoder", cause);
            if (mode == 1) {
                ChatUtil.sendPrefixedMessage(Component.translatable("translation.viafabricplus.packet_error").withStyle(ChatFormatting.RED));
            }
            return;
        }

        super.exceptionCaught(ctx, cause);
    }

}
