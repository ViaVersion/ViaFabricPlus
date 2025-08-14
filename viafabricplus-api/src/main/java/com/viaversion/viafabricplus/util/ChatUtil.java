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

package com.viaversion.viafabricplus.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

/**
 * This class contains methods to send messages to the player with the ViaFabricPlus prefix
 */
public final class ChatUtil {

    public static final String PREFIX = Formatting.WHITE + "[" + Formatting.GOLD + "ViaFabricPlus" + Formatting.WHITE + "]";
    public static final Text PREFIX_TEXT = Text.literal("[").formatted(Formatting.WHITE).append(Text.literal("ViaFabricPlus").formatted(Formatting.GOLD)).append("]");

    /**
     * Prefixes the message with the ViaFabricPlus prefix
     *
     * @param message The message to send
     * @return The prefixed message
     */
    public static Text prefixText(final String message) {
        return prefixText(Text.of(message));
    }

    /**
     * Prefixes the message with the ViaFabricPlus prefix
     *
     * @param message The message to send
     * @return The prefixed message
     */
    public static Text prefixText(final Text message) {
        return Text.empty().append(PREFIX_TEXT).append(" ").append(message);
    }

    /**
     * Sends a prefixed message to the player
     *
     * @param message The message to send
     */
    public static void sendPrefixedMessage(final Text message) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(prefixText(message));
    }

    /**
     * @param text The text to uncover
     * @return The translation key of the text
     */
    public static String uncoverTranslationKey(final Text text) {
        return ((TranslatableTextContent) text.getContent()).getKey();
    }

}
