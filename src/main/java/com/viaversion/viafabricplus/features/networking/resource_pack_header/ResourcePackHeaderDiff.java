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

package com.viaversion.viafabricplus.features.networking.resource_pack_header;

import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourceType;

/**
 * This class file contains the {@link GameVersion} for each protocol version.
 */
public final class ResourcePackHeaderDiff {

    private final static Map<ProtocolVersion, GameVersion> GAME_VERSION_DIFF = new HashMap<>();

    public static void init() {
        final JsonObject diff = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("resource-pack-headers.json");
        for (final String string : diff.keySet()) {
            fill(string, diff.getAsJsonObject(string));
        }
    }

    private static void fill(final String name, final JsonObject data) {
        final ProtocolVersion version = ProtocolVersion.getProtocol(data.get("version").getAsInt());
        if (!version.isKnown()) {
            throw new IllegalStateException("Unknown protocol version: " + version.getOriginalVersion());
        }

        final JsonElement packFormat = data.get("pack_format");
        if (packFormat.isJsonObject()) {
            final int major = packFormat.getAsJsonObject().get("major").getAsInt();
            final int minor = packFormat.getAsJsonObject().get("minor").getAsInt();
            registerVersion(version, major, minor, name, name);
        } else {
            registerVersion(version, packFormat.getAsInt(), -1, name, name);
        }
    }

    /**
     * @param version The {@link ProtocolVersion} to get the {@link GameVersion} for.
     * @return The {@link GameVersion} for the given {@link ProtocolVersion}.
     */
    public static GameVersion get(final ProtocolVersion version) {
        if (!GAME_VERSION_DIFF.containsKey(version)) {
            return SharedConstants.getGameVersion();
        } else {
            return GAME_VERSION_DIFF.get(version);
        }
    }

    private static void registerVersion(final ProtocolVersion version, final int majorVersion, final int minorVersion, final String name, final String id) {
        GAME_VERSION_DIFF.put(version, new GameVersion() {

            @Override
            public SaveVersion dataVersion() {
                return null;
            }

            @Override
            public String id() {
                return id;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public int protocolVersion() {
                return version.getOriginalVersion();
            }

            @Override
            public PackVersion packVersion(final ResourceType type) {
                if (type == ResourceType.CLIENT_RESOURCES) {
                    return PackVersion.of(majorVersion, minorVersion);
                }
                throw new UnsupportedOperationException();
            }

            @Override
            public Date buildTime() {
                return null;
            }

            @Override
            public boolean stable() {
                return true;
            }
        });
    }

}
