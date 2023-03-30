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
package de.florianmichael.viafabricplus.settings.groups;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.type_impl.ProtocolSyncBooleanSetting;
import de.florianmichael.vialoadingbase.model.ProtocolRange;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class DebugSettings extends SettingGroup {
    public final static DebugSettings INSTANCE = new DebugSettings();

    // 1.19 -> 1.18.2
    public final ProtocolSyncBooleanSetting disableSequencing = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.sequence"), ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.14 -> 1.13.2
    public final ProtocolSyncBooleanSetting smoothOutMerchantScreens = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.merchant"), ProtocolRange.andOlder(ProtocolVersion.v1_13_2));

    // 1.13 -> 1.12.2
    public final ProtocolSyncBooleanSetting executeInputsInSync = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.postfix"), ProtocolRange.andOlder(ProtocolVersion.v1_12_2));
    public final ProtocolSyncBooleanSetting sneakInstant = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.sneakinstant"), new ProtocolRange(ProtocolVersion.v1_12_2, ProtocolVersion.v1_8));

    // 1.12 -> 1.11.1-1.11.2
    public final ProtocolSyncBooleanSetting sendOpenInventoryPacket = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.inventory"), ProtocolRange.andOlder(ProtocolVersion.v1_11_1));

    // 1.9 -> 1.8.x
    public final ProtocolSyncBooleanSetting removeCooldowns = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.cooldown"), ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public final ProtocolSyncBooleanSetting sendIdlePacket = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.idle"), new ProtocolRange(ProtocolVersion.v1_8, LegacyProtocolVersion.r1_3_1tor1_3_2));
    public final ProtocolSyncBooleanSetting replaceAttributeModifiers = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.attribute"), ProtocolRange.andOlder(ProtocolVersion.v1_8));

    // 1.8.x -> 1.7.6
    public final ProtocolSyncBooleanSetting replaceSneaking = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.replacesneak"), ProtocolRange.andOlder(ProtocolVersion.v1_7_6));
    public final ProtocolSyncBooleanSetting longSneaking = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.longsneak"), ProtocolRange.andOlder(ProtocolVersion.v1_7_6));

    // r1_5tor1_5_1 -> r1_4_6tor1_4_7
    public final ProtocolSyncBooleanSetting legacyMiningSpeeds = new ProtocolSyncBooleanSetting(this, Text.translatable("debug.viafabricplus.legacypseeds"), ProtocolRange.andOlder(LegacyProtocolVersion.r1_4_6tor1_4_7));

    public DebugSettings() {
        super("Debug");
    }
}
