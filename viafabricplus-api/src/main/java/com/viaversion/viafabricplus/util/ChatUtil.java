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

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.ChatFormatting;

/**
 * This class contains methods to send messages to the player with the ViaFabricPlus prefix
 */
public final class ChatUtil {

    public static final String PREFIX = ChatFormatting.WHITE + "[" + ChatFormatting.GOLD + "ViaFabricPlus" + ChatFormatting.WHITE + "]";
    public static final Component PREFIX_TEXT = Component.literal("[").withStyle(ChatFormatting.WHITE).append(Component.literal("ViaFabricPlus").withStyle(ChatFormatting.GOLD)).append("]");

    /**
     * Prefixes the message with the ViaFabricPlus prefix
     *
     * @param message The message to send
     * @return The prefixed message
     */
    public static Component prefixText(final String message) {
        return prefixText(Component.nullToEmpty(message));
    }

    /**
     * Prefixes the message with the ViaFabricPlus prefix
     *
     * @param message The message to send
     * @return The prefixed message
     */
    public static Component prefixText(final Component message) {
        return Component.empty().append(PREFIX_TEXT).append(" ").append(message);
    }

    /**
     * Sends a prefixed message to the player
     *
     * @param message The message to send
     */
    public static void sendPrefixedMessage(final Component message) {
        Minecraft.getInstance().gui.getChat().addMessage(prefixText(message));
    }

    /**
     * @param text The text to uncover
     * @return The translation key of the text
     */
    public static String uncoverTranslationKey(final Component text) {
        return ((TranslatableContents) text.getContents()).getKey();
    }

}
