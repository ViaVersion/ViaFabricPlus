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

package de.florianmichael.viafabricplus.settings.impl;

import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.type.ProtocolSyncBooleanSetting;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionRange;

public class DebugSettings extends SettingGroup {
    public final static DebugSettings INSTANCE = new DebugSettings();

    // 1.19 -> 1.18.2
    public final ProtocolSyncBooleanSetting disableSequencing = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.sequence"), VersionRange.andOlder(VersionEnum.r1_18_2));

    // 1.14 -> 1.13.2
    public final ProtocolSyncBooleanSetting smoothOutMerchantScreens = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.merchant"), VersionRange.andOlder(VersionEnum.r1_13_2));

    // 1.13 -> 1.12.2
    public final ProtocolSyncBooleanSetting executeInputsInSync = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.postfix"), VersionRange.andOlder(VersionEnum.r1_12_2));
    public final ProtocolSyncBooleanSetting sneakInstant = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.sneakinstant"), VersionRange.of(VersionEnum.r1_8, VersionEnum.r1_12_2));

    // 1.12 -> 1.11.1-1.11.2
    public final ProtocolSyncBooleanSetting sendOpenInventoryPacket = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.inventory"), VersionRange.andOlder(VersionEnum.r1_11_1to1_11_2));

    // 1.9 -> 1.8.x
    public final ProtocolSyncBooleanSetting removeCooldowns = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.cooldown"), VersionRange.andOlder(VersionEnum.r1_8));
    public final ProtocolSyncBooleanSetting sendIdlePacket = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.idle"), VersionRange.of(VersionEnum.r1_4_2, VersionEnum.r1_8).add(VersionRange.andOlder(VersionEnum.r1_2_4tor1_2_5)));
    public final ProtocolSyncBooleanSetting replaceAttributeModifiers = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.attribute"), VersionRange.andOlder(VersionEnum.r1_8));

    // 1.8.x -> 1.7.6
    public final ProtocolSyncBooleanSetting replaceSneaking = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.replacesneak"), VersionRange.andOlder(VersionEnum.r1_7_6tor1_7_10));
    public final ProtocolSyncBooleanSetting longSneaking = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.longsneak"), VersionRange.andOlder(VersionEnum.r1_7_6tor1_7_10));

    // r1_5tor1_5_1 -> r1_4_6tor1_4_7
    public final ProtocolSyncBooleanSetting legacyMiningSpeeds = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.legacypseeds"), VersionRange.andOlder(VersionEnum.r1_4_6tor1_4_7));

    public DebugSettings() {
        super(Text.translatable("settings.viafabricplus.debug"));
    }
}
