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
package de.florianmichael.viafabricplus.base.settings.groups;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.base.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.base.settings.type_impl.ProtocolSyncBooleanSetting;
import de.florianmichael.vialoadingbase.model.ProtocolRange;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class VisualSettings extends SettingGroup {
    public final static VisualSettings INSTANCE = new VisualSettings();

    // 1.19.2 -> 1.19
    public final ProtocolSyncBooleanSetting disableSecureChatWarning = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.secure"), ProtocolRange.andOlder(ProtocolVersion.v1_19));

    // 1.19 -> 1.18.2
    public final ProtocolSyncBooleanSetting hideSignatureIndicator = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.indicator"), ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.16 -> 1.15.2
    public final ProtocolSyncBooleanSetting removeNewerFeaturesFromJigsawScreen = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.jigsaw"), ProtocolRange.andOlder(ProtocolVersion.v1_15_2));

    // 1.13 -> 1.12.2
    public final ProtocolSyncBooleanSetting replacePetrifiedOakSlab = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.stoneslab"), new ProtocolRange(ProtocolVersion.v1_12_2, LegacyProtocolVersion.r1_3_1tor1_3_2));

    // 1.9 -> 1.8.x
    public final ProtocolSyncBooleanSetting emulateArmorHud = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.armor"), ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public final ProtocolSyncBooleanSetting removeNewerFeaturesFromCommandBlockScreen = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.command"), ProtocolRange.andOlder(ProtocolVersion.v1_8));

    // r1_0_0tor1_0_1 -> b1_8tob1_8_1
    public final ProtocolSyncBooleanSetting replaceHurtSoundWithOOFSound = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.oof"), ProtocolRange.andOlder(LegacyProtocolVersion.b1_8tob1_8_1));

    // b1_8tob1_8_1 -> b1_7tob1_7_3
    public final ProtocolSyncBooleanSetting removeNewerHudElements = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.betahud"), ProtocolRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3));

    // a1_0_15 -> c0_28toc0_30
    public final ProtocolSyncBooleanSetting replaceCreativeInventory = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.classic"), ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));
    public final ProtocolSyncBooleanSetting oldWalkingAnimation = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.walkanimation"), ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));
    public final ProtocolSyncBooleanSetting fixSodiumChunkRendering = new ProtocolSyncBooleanSetting(this, Text.translatable("visual.viafabricplus.sodium"), ProtocolRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    public VisualSettings() {
        super("Visual");
    }
}
