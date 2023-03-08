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
package de.florianmichael.viafabricplus.settings.groups;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.ProtocolSyncBooleanSetting;
import de.florianmichael.vialoadingbase.platform.ProtocolRange;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class DebugSettings extends SettingGroup {
    public static final DebugSettings self = new DebugSettings();

    // 1.19 -> 1.18.2
    public final ProtocolSyncBooleanSetting disableSequencing = new ProtocolSyncBooleanSetting(this, "Disable sequencing", ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.14 -> 1.13.2
    public final ProtocolSyncBooleanSetting smoothOutMerchantScreens = new ProtocolSyncBooleanSetting(this, "Smooth out merchant screens", ProtocolRange.andOlder(ProtocolVersion.v1_13_2));

    // 1.13 -> 1.12.2
    public final ProtocolSyncBooleanSetting executeInputsInSync = new ProtocolSyncBooleanSetting(this, "Execute inputs in sync", ProtocolRange.andOlder(ProtocolVersion.v1_12_2));
    public final ProtocolSyncBooleanSetting sneakInstant = new ProtocolSyncBooleanSetting(this, "Sneak instant", new ProtocolRange(ProtocolVersion.v1_12_2, ProtocolVersion.v1_8));
    public final ProtocolSyncBooleanSetting cancelEqualBlockChangeUpdates = new ProtocolSyncBooleanSetting(this, "Cancel equal block change updates", ProtocolRange.andOlder(ProtocolVersion.v1_12_2));

    // 1.12 -> 1.11.1-1.11.2
    public final ProtocolSyncBooleanSetting sendOpenInventoryPacket = new ProtocolSyncBooleanSetting(this, "Send open inventory packet", ProtocolRange.andOlder(ProtocolVersion.v1_11_1));

    // 1.9 -> 1.8.x
    public final ProtocolSyncBooleanSetting removeCooldowns = new ProtocolSyncBooleanSetting(this, "Remove cooldowns", ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public final ProtocolSyncBooleanSetting sendIdlePacket = new ProtocolSyncBooleanSetting(this, "Send idle packet", new ProtocolRange(ProtocolVersion.v1_8, LegacyProtocolVersion.r1_3_1tor1_3_2));
    public final ProtocolSyncBooleanSetting replaceAttributeModifiers = new ProtocolSyncBooleanSetting(this, "Replace attribute modifiers", ProtocolRange.andOlder(ProtocolVersion.v1_8));

    // 1.8.x -> 1.7.6
    public final ProtocolSyncBooleanSetting replaceSneaking = new ProtocolSyncBooleanSetting(this, "Replace sneaking", ProtocolRange.andOlder(ProtocolVersion.v1_7_6));
    public final ProtocolSyncBooleanSetting longSneaking = new ProtocolSyncBooleanSetting(this, "Long sneaking", ProtocolRange.andOlder(ProtocolVersion.v1_7_6));

    public DebugSettings() {
        super("Debug");
    }

    public static DebugSettings getClassWrapper() {
        return DebugSettings.self;
    }
}
