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

package de.florianmichael.viafabricplus.fixes.data;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResourcePackHeaderDiff {

    private final static Map<VersionEnum, GameVersion> GAME_VERSION_DIFF = new HashMap<>();

    static {
        registerVersion(VersionEnum.r1_20_5, 22, "23w51b");
        registerVersion(VersionEnum.r1_20_3tor1_20_4, 22, "1.20.4");
        registerVersion(VersionEnum.r1_20_2, 18, "1.20.2");
        registerVersion(VersionEnum.r1_20tor1_20_1, 15, "1.20.1");
        registerVersion(VersionEnum.r1_19_4, 13, "1.19.4");
        registerVersion(VersionEnum.r1_19_3, 12, "1.19.3");
        registerVersion(VersionEnum.r1_19_1tor1_19_2, 9, "1.19.2");
        registerVersion(VersionEnum.r1_19, 9, "1.19");
        registerVersion(VersionEnum.r1_18_2, 8, "1.18.2");
        registerVersion(VersionEnum.r1_18tor1_18_1, 8, "1.18.1");
        registerVersion(VersionEnum.r1_17_1, 7, "1.17.1");
        registerVersion(VersionEnum.r1_17, 7, "1.17");
        registerVersion(VersionEnum.r1_16_4tor1_16_5, 6, "1.16.5");
        registerVersion(VersionEnum.r1_16_3, 6, "1.16.3");
        registerVersion(VersionEnum.r1_16_2, 6, "1.16.2");
        registerVersion(VersionEnum.r1_16_1, 5, "1.16.1");
        registerVersion(VersionEnum.r1_16, 5, "1.16");
        registerVersion(VersionEnum.r1_15_2, 5, "1.15.2");
        registerVersion(VersionEnum.r1_15_1, 5, "1.15.1");
        registerVersion(VersionEnum.r1_15, 5, "1.15");
        registerVersion(VersionEnum.r1_14_4, 4, "1.14.4");
        registerVersion(VersionEnum.r1_14_3, 4, "1.14.3");
        registerVersion(VersionEnum.r1_14_2, 4, "1.14.2");
        registerVersion(VersionEnum.r1_14_1, 4, "1.14.1");
        registerVersion(VersionEnum.r1_14, 4, "1.14");
        registerVersion(VersionEnum.r1_13_2, 4, "1.13.2");
        registerVersion(VersionEnum.r1_13_1, 4, "1.13.1");
        registerVersion(VersionEnum.r1_13, 4, "1.13");
        registerVersion(VersionEnum.r1_12_2, 3, "1.12.2");
        registerVersion(VersionEnum.r1_12_1, 3, "1.12.1");
        registerVersion(VersionEnum.r1_12, 3, "1.12");
        registerVersion(VersionEnum.r1_11_1to1_11_2, 3, "1.11.2");
        registerVersion(VersionEnum.r1_11, 3, "1.11");
        registerVersion(VersionEnum.r1_10, 2, "1.10.2");
        registerVersion(VersionEnum.r1_9_3tor1_9_4, 2, "1.9.4");
        registerVersion(VersionEnum.r1_9_2, 2, "1.9.2");
        registerVersion(VersionEnum.r1_9_1, 2, "1.9.1");
        registerVersion(VersionEnum.r1_9, 2, "1.9");
        registerVersion(VersionEnum.r1_8, 1, "1.8.9");
        registerVersion(VersionEnum.r1_7_6tor1_7_10, 1, "1.7.10");
        registerVersion(VersionEnum.r1_7_2tor1_7_5, 1, "1.7.5");
    }

    public static void checkOutdated() {
        if (!GAME_VERSION_DIFF.containsKey(ProtocolHack.NATIVE_VERSION)) {
            throw new RuntimeException("The current version has no pack format registered");
        }
        for (VersionEnum version : VersionEnum.OFFICIAL_SUPPORTED_PROTOCOLS) {
            if (!GAME_VERSION_DIFF.containsKey(version)) {
                throw new RuntimeException("The version " + version + " has no pack format registered");
            }
        }
    }

    public static GameVersion get(final VersionEnum version) {
        if (!GAME_VERSION_DIFF.containsKey(version)) {
            return SharedConstants.getGameVersion();
        }
        return GAME_VERSION_DIFF.get(version);
    }

    private static void registerVersion(final VersionEnum version, final int packFormat, final String name) {
        registerVersion(version, packFormat, name, name);
    }

    private static void registerVersion(final VersionEnum version, final int packFormat, final String name, final String id) {
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
                return version.getProtocol().getOriginalVersion();
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
