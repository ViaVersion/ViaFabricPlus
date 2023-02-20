/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.definition;

import com.mojang.bridge.game.PackType;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PackFormatsDefinition {
    private final static Map<Integer, GameVersion> protocolMap = new HashMap<>();

    public static void load() {
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
    }

    public static void checkOutdated(final int nativeVersion) {
        if (!protocolMap.containsKey(nativeVersion))
            throw new RuntimeException("The current version has no pack format registered");

        final GameVersion gameVersion = protocolMap.get(nativeVersion);
        if (!gameVersion.getName().equals(SharedConstants.getGameVersion().getName()) ||
                !gameVersion.getId().equals(SharedConstants.getGameVersion().getId()) ||
                gameVersion.getPackVersion(PackType.RESOURCE) != SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE))
            throw new RuntimeException("The current version has no pack format registered");
    }

    public static GameVersion current() {
        final int targetVersion = ViaLoadingBase.getTargetVersion().getOriginalVersion();
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
            public int getPackVersion(PackType packType) {
                if (packType == PackType.RESOURCE) {
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
