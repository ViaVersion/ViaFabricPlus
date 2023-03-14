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
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import net.minecraft.client.MinecraftClient;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class ChatLengthDefinition {
    public static ChatLengthDefinition INSTANCE;

    public static void create() {
        INSTANCE = new ChatLengthDefinition();

        ChangeProtocolVersionCallback.EVENT.register(protocolVersion -> {
            INSTANCE.maxLength = 256;
            if (protocolVersion.isOlderThanOrEqualTo(ProtocolVersion.v1_10)) {
                INSTANCE.maxLength = 100;

                if (protocolVersion.isOlderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
                    INSTANCE.maxLength = 64 - MinecraftClient.getInstance().getSession().getUsername().length() - 2;
                }
            }
        });
    }

    private int maxLength = 256;

    public void expand() {
        maxLength = Short.MAX_VALUE * 2;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
