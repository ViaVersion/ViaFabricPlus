/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.netty.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.netty.ViaDecoder;

public class ViaFabricPlusViaDecoder extends ViaDecoder {

    public ViaFabricPlusViaDecoder(UserConnection user) {
        super(user);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final var mode = GeneralSettings.INSTANCE.ignorePacketTranslationErrors.getIndex();

        if (mode > 0) {
            try {
                super.channelRead(ctx, msg);
            } catch (Throwable t) {
                ViaFabricPlus.LOGGER.error("Error occurred while decoding packet in ViaDecoder", t);
                if (mode == 1) {
                    ChatUtil.sendPrefixedMessage(Text.literal("An error occurred while decoding a packet! See more details in the logs!").formatted(Formatting.RED));
                }
            }
            return;
        }
        super.channelRead(ctx, msg);
    }
}
