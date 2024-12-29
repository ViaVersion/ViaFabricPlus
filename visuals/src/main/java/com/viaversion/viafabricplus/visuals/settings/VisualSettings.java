/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.type.ModeSetting;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.api.settings.type.VersionedBooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialoader.util.VersionRange;

public final class VisualSettings extends SettingGroup {

    public static final VisualSettings INSTANCE = new VisualSettings();

    public final ModeSetting changeGameMenuScreenLayout = new ModeSetting(this, Text.translatable("visual_settings.viafabricplus.change_game_menu_screen_layout"),
            Text.translatable("change_game_menu_screen_layout.viafabricplus.authentic"),
            Text.translatable("change_game_menu_screen_layout.viafabricplus.adjusted"),
            Text.translatable("base.viafabricplus.off")
    );
    public final BooleanSetting filterNonExistingGlyphs = new BooleanSetting(this, Text.translatable("visual_settings.viafabricplus.filter_non_existing_glyphs"), true) {
        @Override
        public void onValueChanged() {
            final MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                for (FontStorage storage : client.fontManager.fontStorages.values()) {
                    storage.bakedGlyphCache.clear();
                    storage.glyphCache.clear();
                }
            }
        }
    };
    public final BooleanSetting hideModernJigsawScreenFeatures = new BooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_modern_jigsaw_screen_features"), true);
    public final BooleanSetting removeBubblePopSound = new BooleanSetting(this, Text.translatable("visual_settings.viafabricplus.remove_bubble_pop_sound"), false);
    public final BooleanSetting hideEmptyBubbleIcons = new BooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_empty_bubble_icons"), false);
    public final BooleanSetting hideVillagerProfession = new BooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_villager_profession"), false);

    // 1.21 -> 1.20.5
    public final VersionedBooleanSetting hideDownloadTerrainScreenTransitionEffects = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_download_terrain_screen_transition_effects"), VersionRange.andOlder(ProtocolVersion.v1_20_5));

    // 1.20.3 -> 1.20.2
    public final VersionedBooleanSetting lockBlockingArmRotation = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.lock_blocking_arm_rotation"), VersionRange.andOlder(ProtocolVersion.v1_20_2));

    // 1.19.4 -> 1.19.3
    public final VersionedBooleanSetting changeBodyRotationInterpolation = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.change_body_rotation_interpolation"), VersionRange.andOlder(ProtocolVersion.v1_19_3));

    // 1.19.2 -> 1.19
    public final VersionedBooleanSetting disableSecureChatWarning = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.disable_secure_chat_warning"), VersionRange.andOlder(ProtocolVersion.v1_19));

    // 1.19 -> 1.18.2
    public final VersionedBooleanSetting hideSignatureIndicator = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_signature_indicator"), VersionRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.13 -> 1.12.2
    public final VersionedBooleanSetting replacePetrifiedOakSlab = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.replace_petrified_oak_slab"), VersionRange.of(LegacyProtocolVersion.r1_3_1tor1_3_2, ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting hideFurnaceRecipeBook = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_furnace_recipe_book"), VersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting forceUnicodeFontForNonAsciiLanguages = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.force_unicode_font_for_non_ascii_languages"), VersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting sneakInstantly = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.sneak_instantly"), VersionRange.andOlder(ProtocolVersion.v1_12_2));

    // 1.12 -> 1.11.1
    public final VersionedBooleanSetting sidewaysBackwardsRunning = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.sideways_backwards_walking"), VersionRange.andOlder(ProtocolVersion.v1_11_1));
    public final VersionedBooleanSetting hideCraftingRecipeBook = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_crafting_recipe_book"), VersionRange.andOlder(ProtocolVersion.v1_11_1));

    // 1.9 -> 1.8.x
    public final VersionedBooleanSetting alwaysRenderCrosshair = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.always_render_crosshair"), VersionRange.andOlder(ProtocolVersion.v1_8));
    public final VersionedBooleanSetting emulateArmorHud = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.emulate_armor_hud"), VersionRange.andOlder(ProtocolVersion.v1_8));
    public final VersionedBooleanSetting hideModernCommandBlockScreenFeatures = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_modern_command_block_screen_features"), VersionRange.andOlder(ProtocolVersion.v1_8));

    // 1.8.x -> 1.7.6 - 1.7.10
    public final VersionedBooleanSetting swingHandOnItemUse = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.swing_hand_on_item_use"), VersionRange.andOlder(ProtocolVersion.v1_7_6));
    public final VersionedBooleanSetting tiltItemPositions = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.tilt_item_positions"), VersionRange.andOlder(ProtocolVersion.v1_7_6));
    public final VersionedBooleanSetting enableLegacyTablist = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.enable_legacy_tablist"), VersionRange.andOlder(ProtocolVersion.v1_7_6));

    // 1.0.0-1.0.1 -> b1.8-b1.8.1
    public final VersionedBooleanSetting replaceHurtSoundWithOOFSound = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.replace_hurt_sound_with_oof_sound"), VersionRange.andOlder(LegacyProtocolVersion.b1_8tob1_8_1));

    // b1.8/b1.8.1 -> b1_7/b1.7.3
    public final VersionedBooleanSetting hideModernHUDElements = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.hide_modern_hud_elements"), VersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3));
    public final VersionedBooleanSetting disableServerPinging = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.disable_server_pinging"), VersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3));

    // a1.0.15 -> c0_28/c0_30
    public final VersionedBooleanSetting replaceCreativeInventory = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.replace_creative_inventory_with_classic_inventory"), VersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));
    public final VersionedBooleanSetting oldWalkingAnimation = new VersionedBooleanSetting(this, Text.translatable("visual_settings.viafabricplus.old_walking_animation"), VersionRange.andOlder(LegacyProtocolVersion.c0_28toc0_30));

    public VisualSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.visual"));
        changeGameMenuScreenLayout.setTooltip(() -> switch (changeGameMenuScreenLayout.getIndex()) {
            case 0 -> Text.translatable("change_game_menu_screen_layout.viafabricplus.authentic.tooltip");
            case 1 -> Text.translatable("change_game_menu_screen_layout.viafabricplus.adjusted.tooltip");
            default -> Text.translatable("change_game_menu_screen_layout.viafabricplus.off.tooltip");
        });
        changeGameMenuScreenLayout.setValue(1);

        hideDownloadTerrainScreenTransitionEffects.setValue(VersionedBooleanSetting.DISABLED_INDEX);
        forceUnicodeFontForNonAsciiLanguages.setValue(VersionedBooleanSetting.DISABLED_INDEX);
    }

}
