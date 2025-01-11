/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.type.ModeSetting;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class GeneralSettings extends SettingGroup {

    public static final GeneralSettings INSTANCE = new GeneralSettings();

    private final MutableText[] ORIENTATION_OPTIONS = new MutableText[]{
            Text.translatable("base.viafabricplus.none"),
            Text.translatable("base.viafabricplus.left_top"),
            Text.translatable("base.viafabricplus.right_top"),
            Text.translatable("base.viafabricplus.left_bottom"),
            Text.translatable("base.viafabricplus.right_bottom")
    };

    public final ModeSetting multiplayerScreenButtonOrientation = new ModeSetting(this, Text.translatable("general_settings.viafabricplus.multiplayer_screen_button_orientation"), 2, ORIENTATION_OPTIONS);
    public final ModeSetting addServerScreenButtonOrientation = new ModeSetting(this, Text.translatable("general_settings.viafabricplus.add_server_screen_button_orientation"), 2, ORIENTATION_OPTIONS);
    public final ModeSetting directConnectScreenButtonOrientation = new ModeSetting(this, Text.translatable("general_settings.viafabricplus.direct_connect_screen_button_orientation"), 2, ORIENTATION_OPTIONS);
    public final ModeSetting removeNotAvailableItemsFromCreativeTab = new ModeSetting(this, Text.translatable("general_settings.viafabricplus.filter_creative_tabs"),
            Text.translatable("base.viafabricplus.vanilla_and_modded"),
            Text.translatable("base.viafabricplus.vanilla_only"),
            Text.translatable("base.viafabricplus.off")
    );
    public final BooleanSetting saveSelectedProtocolVersion = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.save_selected_protocol_version"), true);
    public final BooleanSetting showExtraInformationInDebugHud = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.extra_information_in_debug_hud"), true);
    public final BooleanSetting showClassicLoadingProgressInConnectScreen = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.show_classic_loading_progress"), true);
    public final BooleanSetting showAdvertisedServerVersion = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.show_advertised_server_version"), true);
    public final ModeSetting ignorePacketTranslationErrors = new ModeSetting(this, Text.translatable("general_settings.viafabricplus.ignore_packet_translation_errors"),
            Text.translatable("base.viafabricplus.kick"),
            Text.translatable("base.viafabricplus.cancel_and_notify"),
            Text.translatable("base.viafabricplus.cancel")
    );
    public final BooleanSetting loadSkinsAndSkullsInLegacyVersions = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.load_skins_and_skulls_in_legacy_versions"), true);
    public final BooleanSetting emulateInventoryActionsInAlphaVersions = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.emulate_inventory_actions_in_alpha_versions"), true);
    public final BooleanSetting saveScrollPositionInSlotScreens = new BooleanSetting(this, Text.translatable("general_settings.viafabricplus.save_scroll_position_in_slot_screens"), true);

    public GeneralSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.general"));
        emulateInventoryActionsInAlphaVersions.setTooltip(Text.translatable("base.viafabricplus.this_will_require_a_restart"));
    }

    public static ButtonWidget.Builder withOrientation(final ButtonWidget.Builder builder, final int orientationIndex, final int width, final int height) {
        return switch (orientationIndex) {
            case 1 -> builder.position(5, 5);
            case 2 -> builder.position(width - 98 - 5, 5);
            case 3 -> builder.position(5, height - 20 - 5);
            case 4 -> builder.position(width - 98 - 5, height - 20 - 5);
            default -> builder;
        };
    }

}
