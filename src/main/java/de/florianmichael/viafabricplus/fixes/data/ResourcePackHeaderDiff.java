/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.fixes.data;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionType;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class file contains the {@link GameVersion} for each protocol version.
 */
public class ResourcePackHeaderDiff {

    private final static Map<ProtocolVersion, GameVersion> GAME_VERSION_DIFF = new HashMap<>();

    static {
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

    /**
     * Checks if the registry is outdated.
     */
    @ApiStatus.Internal
    public static void checkOutdated() {
        for (ProtocolVersion version : ProtocolVersion.getProtocols()) {
            if (version.isSnapshot()) continue;
            if (version.getVersionType() != VersionType.RELEASE) continue;
            if (!GAME_VERSION_DIFF.containsKey(version)) {
                throw new RuntimeException("The version " + version + " has no pack format registered");
            }
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

    private static void registerVersion(final ProtocolVersion version, final int packFormat, final String name) {
        registerVersion(version, packFormat, name, name);
    }

    private static void registerVersion(final ProtocolVersion version, final int packFormat, final String name, final String id) {
        GAME_VERSION_DIFF.put(version, new GameVersion() {

            @Override
            public SaveVersion getSaveVersion() {
                return null;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getProtocolVersion() {
                return version.getOriginalVersion();
            }

            @Override
            public int getResourceVersion(ResourceType type) {
                if (type == ResourceType.CLIENT_RESOURCES) {
                    return packFormat;
                }
                throw new UnsupportedOperationException();
            }

            @Override
            public Date getBuildTime() {
                return null;
            }

            @Override
            public boolean isStable() {
                return true;
            }
        });
    }

}
