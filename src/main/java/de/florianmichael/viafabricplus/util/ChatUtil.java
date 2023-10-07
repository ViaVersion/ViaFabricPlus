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
package de.florianmichael.viafabricplus.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtil {
    public final static String PREFIX = Formatting.WHITE + "[" + Formatting.GOLD + "ViaFabricPlus" + Formatting.WHITE + "]";
    public final static Text PREFIX_TEXT = Text.literal("[").formatted(Formatting.WHITE).append(Text.literal("ViaFabricPlus").formatted(Formatting.GOLD)).append("]");

    public static Text prefixText(final String message) {
        return prefixText(Text.literal(message));
    }

    public static Text prefixText(final Text message) {
        return Text.empty().append(PREFIX_TEXT).append(" ").append(message);
    }

    public static void sendPrefixedMessage(final Text message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(prefixText(message));
    }
}
