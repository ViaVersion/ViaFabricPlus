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
package de.florianmichael.viafabricplus.definition;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class ChatLengthDefinition {
    private static int maxLength = 256;

    public static void reload(final ComparableProtocolVersion protocolVersion) {
        maxLength = 256;
        if (protocolVersion.isOlderThanOrEqualTo(ProtocolVersion.v1_10)) {
            maxLength = 100;

            if (protocolVersion.isOlderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                maxLength = 64 - MinecraftClient.getInstance().getSession().getUsername().length() - 2;
            }
        }
    }

    public static void expand() {
        maxLength = Short.MAX_VALUE * 2;
    }

    public static int getMaxLength() {
        return maxLength;
    }
}
