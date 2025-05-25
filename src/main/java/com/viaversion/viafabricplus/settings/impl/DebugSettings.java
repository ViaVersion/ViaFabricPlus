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
import com.viaversion.viafabricplus.api.settings.type.VersionedBooleanSetting;
import com.viaversion.viafabricplus.features.font.FontCacheReload;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public final class DebugSettings extends SettingGroup {

    public static final DebugSettings INSTANCE = new DebugSettings();

    public final BooleanSetting queueConfigPackets = new BooleanSetting(this, Text.translatable("debug_settings.viafabricplus.queue_config_packets"), true);
    public final BooleanSetting printNetworkingErrorsToLogs = new BooleanSetting(this, Text.translatable("debug_settings.viafabricplus.print_networking_errors_to_logs"), true);
    public final BooleanSetting ignoreFabricSyncErrors = new BooleanSetting(this, Text.translatable("debug_settings.viafabricplus.ignore_fabric_sync_errors"), false);
    public final BooleanSetting hideModernJigsawScreenFeatures = new BooleanSetting(this, Text.translatable("debug_settings.viafabricplus.hide_modern_jigsaw_screen_features"), true);
    public final BooleanSetting filterNonExistingGlyphs = new BooleanSetting(this, Text.translatable("debug_settings.viafabricplus.filter_non_existing_glyphs"), true) {
        @Override
        public void onValueChanged() {
            FontCacheReload.reload();
        }
    };

    // 1.20.5 -> 1.20.4
    public final VersionedBooleanSetting dontCreatePacketErrorCrashReports = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.dont_create_packet_error_crash_reports"), VersionRange.andOlder(ProtocolVersion.v1_20_3));

    // 1.19 -> 1.18.2
    public final VersionedBooleanSetting disableSequencing = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.disable_sequencing"), VersionRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.17 -> 1.16.5
    public final VersionedBooleanSetting alwaysTickClientPlayer = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.always_tick_client_player"), VersionRange.andOlder(ProtocolVersion.v1_8).add(VersionRange.andNewer(ProtocolVersion.v1_17)));

    // 1.13 -> 1.12.2
    public final VersionedBooleanSetting executeInputsSynchronously = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.execute_inputs_synchronously"), VersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting legacyTabCompletions = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.legacy_tab_completions"), VersionRange.andOlder(ProtocolVersion.v1_12_2));
    public final VersionedBooleanSetting legacyPaneOutlines = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.legacy_pane_outlines"), VersionRange.andOlder(ProtocolVersion.v1_12_2));

    // 1.9 -> 1.8.x
    public final VersionedBooleanSetting emulateArmorHud = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.emulate_armor_hud"), VersionRange.andOlder(ProtocolVersion.v1_8));
    public final VersionedBooleanSetting hideModernCommandBlockScreenFeatures = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.hide_modern_command_block_screen_features"), VersionRange.andOlder(ProtocolVersion.v1_8));

    // b1.8/b1.8.1 -> b1_7/b1.7.3
    public final VersionedBooleanSetting disableServerPinging = new VersionedBooleanSetting(this, Text.translatable("debug_settings.viafabricplus.disable_server_pinging"), VersionRange.andOlder(LegacyProtocolVersion.b1_7tob1_7_3));

    public DebugSettings() {
        super(Text.translatable("setting_group_name.viafabricplus.debug"));
    }

}
