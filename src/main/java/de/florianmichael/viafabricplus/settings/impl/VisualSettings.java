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
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.base.VersionedBooleanSetting;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionRange;

public class VisualSettings extends SettingGroup {
    private static final VisualSettings instance = new VisualSettings();

    // 1.19.2 -> 1.19
    public final VersionedBooleanSetting disableSecureChatWarning = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.disable_secure_chat_warning"), VersionRange.andOlder(VersionEnum.r1_19));

    // 1.19 -> 1.18.2
    public final VersionedBooleanSetting hideSignatureIndicator = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_signature_indicator"), VersionRange.andOlder(VersionEnum.r1_18_2));

    // 1.16 -> 1.15.2
    public final VersionedBooleanSetting removeNewerFeaturesFromJigsawScreen = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.remove_newer_features_from_jigsaw_screen"), VersionRange.andOlder(VersionEnum.r1_15_2));

    // 1.13 -> 1.12.2
    public final VersionedBooleanSetting replacePetrifiedOakSlab = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.replace_petrified_oak_slab"), VersionRange.of(VersionEnum.r1_3_1tor1_3_2, VersionEnum.r1_12_2));
    public final VersionedBooleanSetting changeFontRendererBehaviour = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.change_font_renderer_behaviour"), VersionRange.andOlder(VersionEnum.r1_12_2));
    
    // 1.9 -> 1.8.x
    public final VersionedBooleanSetting emulateArmorHud = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.emulate_armor_hud"), VersionRange.andOlder(VersionEnum.r1_8));
    public final VersionedBooleanSetting removeNewerFeaturesFromCommandBlockScreen = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.remove_newer_features_from_command_block_screen"), VersionRange.andOlder(VersionEnum.r1_8));
    public final VersionedBooleanSetting showSuperSecretSettings = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.show_super_secret_settings"), VersionRange.andOlder(VersionEnum.r1_8));

    // 1.8.x -> 1.7.6 - 1.7.10
    public final VersionedBooleanSetting enableSwordBlocking = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.enable_sword_blocking"), VersionRange.andOlder(VersionEnum.r1_7_6tor1_7_10));
    public final VersionedBooleanSetting enableBlockHitAnimation = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.enable_block_hit_animation"), VersionRange.andOlder(VersionEnum.r1_7_6tor1_7_10));

    // 1.0.0-1.0.1 -> b1.8-b1.8.1
    public final VersionedBooleanSetting replaceHurtSoundWithOOFSound = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.replace_hurt_sound_with_oof_sound"), VersionRange.andOlder(VersionEnum.b1_8tob1_8_1));

    // b1.8/b1.8.1 -> b1_7/b1.7.3
    public final VersionedBooleanSetting removeNewerHudElements = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.remove_newer_hud_elements"), VersionRange.andOlder(VersionEnum.b1_7tob1_7_3));

    // a1.0.15 -> c0_28/c0_30
    public final VersionedBooleanSetting replaceCreativeInventory = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.replace_creative_inventory_with_classic_inventory"), VersionRange.andOlder(VersionEnum.c0_28toc0_30));
    public final VersionedBooleanSetting oldWalkingAnimation = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.old_walking_animation"), VersionRange.andOlder(VersionEnum.c0_28toc0_30));

    public VisualSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.visual"));
    }

    public static VisualSettings global() {
        return instance;
    }

}
