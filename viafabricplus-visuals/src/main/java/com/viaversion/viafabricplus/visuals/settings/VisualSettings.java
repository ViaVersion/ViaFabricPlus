/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.visuals.settings;

import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.api.settings.type.VersionedBooleanSetting;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import net.minecraft.network.chat.Component;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public final class VisualSettings extends SettingGroup {

    public static final VisualSettings INSTANCE = new VisualSettings();

    public final VersionedBooleanSetting hideDownloadTerrainScreenTransitionEffects = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_download_terrain_screen_transition_effects"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_20_5));
    public final VersionedBooleanSetting forceUnicodeFontForNonAsciiLanguages = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.force_unicode_font_for_non_ascii_languages"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting sneakInstantly = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.sneak_instantly"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting enableLegacyTablist = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.enable_legacy_tablist"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_7_6));
    public final VersionedBooleanSetting replaceHurtSoundWithOOFSound = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.replace_hurt_sound_with_oof_sound"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_8tob1_8_1));
    public final VersionedBooleanSetting hideModernHUDElements = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_modern_hud_elements"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3));
    public final VersionedBooleanSetting replaceCreativeInventory = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.replace_creative_inventory_with_classic_inventory"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));
    public final VersionedBooleanSetting oldWalkingAnimation = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.old_walking_animation"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    public VisualSettings() {
        super(Component.translatable("setting_group_name.viafabricplus.visual"));

        hideDownloadTerrainScreenTransitionEffects.setValue(VersionedBooleanSetting.DISABLED_INDEX);
        forceUnicodeFontForNonAsciiLanguages.setValue(VersionedBooleanSetting.DISABLED_INDEX);
    }

}
