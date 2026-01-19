/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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
import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.type.ModeSetting;
import com.viaversion.viafabricplus.api.settings.type.VersionedBooleanSetting;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import net.minecraft.network.chat.Component;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public final class VisualSettings extends SettingGroup {

    public static final VisualSettings INSTANCE = new VisualSettings();

    public final ModeSetting changeGameMenuScreenLayout = new ModeSetting(this, Component.translatable("visual_settings.viafabricplus.change_game_menu_screen_layout"),
            Component.translatable("change_game_menu_screen_layout.viafabricplus.authentic"),
            Component.translatable("change_game_menu_screen_layout.viafabricplus.adjusted"),
            Component.translatable("base.viafabricplus.off")
    );
    public final BooleanSetting removeBubblePopSound = new BooleanSetting(this, Component.translatable("visual_settings.viafabricplus.remove_bubble_pop_sound"), false);
    public final BooleanSetting hideEmptyBubbleIcons = new BooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_empty_bubble_icons"), false);
    public final BooleanSetting hideVillagerProfession = new BooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_villager_profession"), false);

    // 1.21 -> 1.20.5
    public final VersionedBooleanSetting hideDownloadTerrainScreenTransitionEffects = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_download_terrain_screen_transition_effects"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_20_5));

    // 1.20.3 -> 1.20.2
    public final VersionedBooleanSetting lockBlockingArmRotation = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.lock_blocking_arm_rotation"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_20_2));

    // 1.19.4 -> 1.19.3
    public final VersionedBooleanSetting changeBodyRotationInterpolation = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.change_body_rotation_interpolation"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_19_3));
    public final VersionedBooleanSetting potionEnchantmentGlint = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.potion_enchantment_glint"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_19_3));

    // 1.19.2 -> 1.19
    public final VersionedBooleanSetting disableSecureChatWarning = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.disable_secure_chat_warning"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_19));

    // 1.19 -> 1.18.2
    public final VersionedBooleanSetting hideSignatureIndicator = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_signature_indicator"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.13 -> 1.12.2
    public final VersionedBooleanSetting replacePetrifiedOakSlab = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.replace_petrified_oak_slab"), ProtocolVersionRange.of(LegacyProtocolVersion.r1_3_1tor1_3_2, ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting hideFurnaceRecipeBook = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_furnace_recipe_book"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting forceUnicodeFontForNonAsciiLanguages = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.force_unicode_font_for_non_ascii_languages"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting sneakInstantly = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.sneak_instantly"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_12_2));

    // 1.12 -> 1.11.1
    public final VersionedBooleanSetting sidewaysBackwardsRunning = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.sideways_backwards_walking"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_11_1));
    public final VersionedBooleanSetting hideCraftingRecipeBook = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_crafting_recipe_book"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_11_1));

    // 1.9 -> 1.8.x
    public final VersionedBooleanSetting alwaysRenderCrosshair = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.always_render_crosshair"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_8));

    // 1.8.x -> 1.7.6 - 1.7.10
    public final VersionedBooleanSetting swingHandOnItemUse = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.swing_hand_on_item_use"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_7_6));
    public final VersionedBooleanSetting tiltItemPositions = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.tilt_item_positions"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_7_6));
    public final VersionedBooleanSetting enableLegacyTablist = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.enable_legacy_tablist"), ProtocolVersionRange.andOlder(ProtocolVersion.v1_7_6));

    // 1.0.0-1.0.1 -> b1.8-b1.8.1
    public final VersionedBooleanSetting replaceHurtSoundWithOOFSound = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.replace_hurt_sound_with_oof_sound"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_8tob1_8_1));

    // b1.8/b1.8.1 -> b1_7/b1.7.3
    public final VersionedBooleanSetting hideModernHUDElements = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.hide_modern_hud_elements"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3));

    // a1.0.15 -> c0_28/c0_30
    public final VersionedBooleanSetting replaceCreativeInventory = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.replace_creative_inventory_with_classic_inventory"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));
    public final VersionedBooleanSetting oldWalkingAnimation = new VersionedBooleanSetting(this, Component.translatable("visual_settings.viafabricplus.old_walking_animation"), ProtocolVersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    public VisualSettings() {
        super(Component.translatable("setting_group_name.viafabricplus.visual"));
        changeGameMenuScreenLayout.setTooltip(() -> switch (changeGameMenuScreenLayout.getIndex()) {
            case 0 -> Component.translatable("change_game_menu_screen_layout.viafabricplus.authentic.tooltip");
            case 1 -> Component.translatable("change_game_menu_screen_layout.viafabricplus.adjusted.tooltip");
            default -> Component.translatable("change_game_menu_screen_layout.viafabricplus.off.tooltip");
        });
        changeGameMenuScreenLayout.setValue(1);

        hideDownloadTerrainScreenTransitionEffects.setValue(VersionedBooleanSetting.DISABLED_INDEX);
        forceUnicodeFontForNonAsciiLanguages.setValue(VersionedBooleanSetting.DISABLED_INDEX);
    }

}
