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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;

/**
 * This class file contains the {@link GameVersion} for each protocol version.
 */
public final class ResourcePackHeaderDiff {

    private final static Map<ProtocolVersion, GameVersion> GAME_VERSION_DIFF = new HashMap<>();

    static {
        registerVersion(ProtocolVersion.v1_21_7, 64, "1.21.8");
        registerVersion(ProtocolVersion.v1_21_6, 63, "1.21.6");
        registerVersion(ProtocolVersion.v1_21_5, 55, "1.21.5");
        registerVersion(ProtocolVersion.v1_21_4, 46, "1.21.4");
        registerVersion(ProtocolVersion.v1_21_2, 42, "1.21.3");
        registerVersion(ProtocolVersion.v1_21, 34, "1.21.1");
        registerVersion(ProtocolVersion.v1_20_5, 32, "1.20.6");
        registerVersion(ProtocolVersion.v1_20_3, 22, "1.20.4");
        registerVersion(ProtocolVersion.v1_20_2, 18, "1.20.2");
        registerVersion(ProtocolVersion.v1_20, 15, "1.20.1");
        registerVersion(ProtocolVersion.v1_19_4, 13, "1.19.4");
        registerVersion(ProtocolVersion.v1_19_3, 12, "1.19.3");
        registerVersion(ProtocolVersion.v1_19_1, 9, "1.19.2");
        registerVersion(ProtocolVersion.v1_19, 9, "1.19");
        registerVersion(ProtocolVersion.v1_18_2, 8, "1.18.2");
        registerVersion(ProtocolVersion.v1_18, 8, "1.18.1");
        registerVersion(ProtocolVersion.v1_17_1, 7, "1.17.1");
        registerVersion(ProtocolVersion.v1_17, 7, "1.17");
        registerVersion(ProtocolVersion.v1_16_4, 6, "1.16.5");
        registerVersion(ProtocolVersion.v1_16_3, 6, "1.16.3");
        registerVersion(ProtocolVersion.v1_16_2, 6, "1.16.2");
        registerVersion(ProtocolVersion.v1_16_1, 5, "1.16.1");
        registerVersion(ProtocolVersion.v1_16, 5, "1.16");
        registerVersion(ProtocolVersion.v1_15_2, 5, "1.15.2");
        registerVersion(ProtocolVersion.v1_15_1, 5, "1.15.1");
        registerVersion(ProtocolVersion.v1_15, 5, "1.15");
        registerVersion(ProtocolVersion.v1_14_4, 4, "1.14.4");
        registerVersion(ProtocolVersion.v1_14_3, 4, "1.14.3");
        registerVersion(ProtocolVersion.v1_14_2, 4, "1.14.2");
        registerVersion(ProtocolVersion.v1_14_1, 4, "1.14.1");
        registerVersion(ProtocolVersion.v1_14, 4, "1.14");
        registerVersion(ProtocolVersion.v1_13_2, 4, "1.13.2");
        registerVersion(ProtocolVersion.v1_13_1, 4, "1.13.1");
        registerVersion(ProtocolVersion.v1_13, 4, "1.13");
        registerVersion(ProtocolVersion.v1_12_2, 3, "1.12.2");
        registerVersion(ProtocolVersion.v1_12_1, 3, "1.12.1");
        registerVersion(ProtocolVersion.v1_12, 3, "1.12");
        registerVersion(ProtocolVersion.v1_11_1, 3, "1.11.2");
        registerVersion(ProtocolVersion.v1_11, 3, "1.11");
        registerVersion(ProtocolVersion.v1_10, 2, "1.10.2");
        registerVersion(ProtocolVersion.v1_9_3, 2, "1.9.4");
        registerVersion(ProtocolVersion.v1_9_2, 2, "1.9.2");
        registerVersion(ProtocolVersion.v1_9_1, 2, "1.9.1");
        registerVersion(ProtocolVersion.v1_9, 2, "1.9");
        registerVersion(ProtocolVersion.v1_8, 1, "1.8.9");
        registerVersion(ProtocolVersion.v1_7_6, 1, "1.7.10");
        registerVersion(ProtocolVersion.v1_7_2, 1, "1.7.5");
    }

    public static void init() {
        // Also calls the static block
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

    private static void registerVersion(final ProtocolVersion version, final int packFormat, final String name) {
        registerVersion(version, packFormat, name, name);
    }

    private static void registerVersion(final ProtocolVersion version, final int packFormat, final String name, final String id) {
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
            public int packVersion(final ResourceType type) {
                if (type == ResourceType.CLIENT_RESOURCES) {
                    return packFormat;
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
