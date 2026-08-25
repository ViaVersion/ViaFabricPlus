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

package com.viaversion.viafabricplus.settings.impl;

import com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting;
import com.viaversion.viafabricplus.api.settings.impl.VisualSettings;
import com.viaversion.viafabricplus.settings.base.SettingGroupImpl;

import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_12_2;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_6;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange.andOlder;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.b1_7tob1_7_3;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.b1_8tob1_8_1;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.c0_28toc0_30;

public final class VisualSettingsImpl extends SettingGroupImpl implements VisualSettings {

    private final VersionedBooleanSetting hideDownloadTerrainScreenTransitionEffects = registerVersionedBoolean("hide_download_terrain_screen_transition_effects", andOlder(v1_20_5), false);
    private final VersionedBooleanSetting forceUnicodeFontForNonAsciiLanguages = registerVersionedBoolean("force_unicode_font_for_non_ascii_languages", andOlder(v1_12_2), false);
    private final VersionedBooleanSetting sneakInstantly = registerVersionedBoolean("sneak_instantly", andOlder(v1_12_2), true);
    private final VersionedBooleanSetting enableLegacyTablist = registerVersionedBoolean("enable_legacy_tablist", andOlder(v1_7_6), false);
    private final VersionedBooleanSetting replaceHurtSoundWithOOFSound = registerVersionedBoolean("replace_hurt_sound_with_oof_sound", andOlder(b1_8tob1_8_1), true);
    private final VersionedBooleanSetting hideModernHUDElements = registerVersionedBoolean("hide_modern_hud_elements", andOlder(b1_7tob1_7_3), false);
    private final VersionedBooleanSetting replaceCreativeInventory = registerVersionedBoolean("replace_creative_inventory_with_classic_inventory", andOlder(c0_28toc0_30), true);
    private final VersionedBooleanSetting oldWalkingAnimation = registerVersionedBoolean("old_walking_animation", andOlder(c0_28toc0_30), true);
    private final VersionedBooleanSetting legacyPaneOutlines = registerVersionedBoolean("legacy_pane_outlines", andOlder(v1_12_2), false);
    private final VersionedBooleanSetting legacyCropOutlines = registerVersionedBoolean("legacy_crop_outlines", andOlder(v1_8), false);
    private final VersionedBooleanSetting disableServerPinging = registerVersionedBoolean("disable_server_pinging", andOlder(b1_7tob1_7_3), false);

    public VisualSettingsImpl() {
        super("visual");
    }

    @Override
    public VersionedBooleanSetting hideDownloadTerrainScreenTransitionEffects() {
        return this.hideDownloadTerrainScreenTransitionEffects;
    }

    @Override
    public VersionedBooleanSetting forceUnicodeFontForNonAsciiLanguages() {
        return this.forceUnicodeFontForNonAsciiLanguages;
    }

    @Override
    public VersionedBooleanSetting sneakInstantly() {
        return this.sneakInstantly;
    }

    @Override
    public VersionedBooleanSetting enableLegacyTablist() {
        return this.enableLegacyTablist;
    }

    @Override
    public VersionedBooleanSetting replaceHurtSoundWithOOFSound() {
        return this.replaceHurtSoundWithOOFSound;
    }

    @Override
    public VersionedBooleanSetting hideModernHUDElements() {
        return this.hideModernHUDElements;
    }

    @Override
    public VersionedBooleanSetting replaceCreativeInventory() {
        return this.replaceCreativeInventory;
    }

    @Override
    public VersionedBooleanSetting oldWalkingAnimation() {
        return this.oldWalkingAnimation;
    }

    @Override
    public VersionedBooleanSetting legacyPaneOutlines() {
        return this.legacyPaneOutlines;
    }

    @Override
    public VersionedBooleanSetting legacyCropOutlines() {
        return this.legacyCropOutlines;
    }

    @Override
    public VersionedBooleanSetting disableServerPinging() {
        return this.disableServerPinging;
    }

}
