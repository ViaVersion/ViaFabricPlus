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
package de.florianmichael.viafabricplus.definition;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PackFormatsDefinition {
    private final static Map<Integer, GameVersion> protocolMap = new HashMap<>();

    public static void load() {
        registerVersion(ProtocolVersion.v1_19_4, 13, "1.19.4");
        registerVersion(ProtocolVersion.v1_19_3, 12, "1.19.3");
        registerVersion(ProtocolVersion.v1_19_1, 9, "1.19.2");
        registerVersion(ProtocolVersion.v1_19, 9, "1.19");
        registerVersion(ProtocolVersion.v1_18_2, 8, "1.18.2");
        registerVersion(ProtocolVersion.v1_18, 8, "1.18");
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
        registerVersion(ProtocolVersion.v1_14_2, 4, "1.14.2", "1.14.2 / f647ba8dc371474797bee24b2b312ff4");
        registerVersion(ProtocolVersion.v1_14_1, 4, "1.14.1", "1.14.1 / a8f78b0d43c74598a199d6d80cda413f");
        registerVersion(ProtocolVersion.v1_14, 4, "1.14", "1.14 / 5dac5567e13e46bdb0c1d90aa8d8b3f7");
        registerVersion(ProtocolVersion.v1_13_2, 4, "1.13.2"); // ids weren't sent over the http headers back then, why care...
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
        registerVersion(ProtocolVersion.v1_7_1, 1, "1.7.5");

        checkOutdated(SharedConstants.getProtocolVersion());
    }

    public static void checkOutdated(final int nativeVersion) {
        if (!protocolMap.containsKey(nativeVersion)) {
            throw new RuntimeException("The current version has no pack format registered");
        }

        final GameVersion gameVersion = protocolMap.get(nativeVersion);
        if (!gameVersion.getName().equals(SharedConstants.getGameVersion().getName()) || !gameVersion.getId().equals(SharedConstants.getGameVersion().getId()) || gameVersion.getResourceVersion(ResourceType.CLIENT_RESOURCES) != SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES)) {
            throw new RuntimeException("The current version has no pack format registered");
        }
    }

    public static GameVersion current() {
        final int targetVersion = ViaLoadingBase.getClassWrapper().getTargetVersion().getOriginalVersion();
        if (!protocolMap.containsKey(targetVersion)) return SharedConstants.getGameVersion();
        return protocolMap.get(targetVersion);
    }

    private static void registerVersion(final ProtocolVersion version, final int packFormat, final String name) {
        registerVersion(version, packFormat, name, name);
    }

    private static void registerVersion(final ProtocolVersion version, final int packFormat, final String name, final String id) {
        protocolMap.put(version.getVersion(), new GameVersion() {
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
                return version.getVersion();
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
