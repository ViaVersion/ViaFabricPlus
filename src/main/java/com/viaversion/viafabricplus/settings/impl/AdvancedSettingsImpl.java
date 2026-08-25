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

import com.viaversion.viafabricplus.api.settings.base.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting;
import com.viaversion.viafabricplus.api.settings.impl.AdvancedSettings;
import com.viaversion.viafabricplus.settings.base.SettingGroupImpl;

import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_18_2;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_3;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_21_11;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange.andOlder;

public final class AdvancedSettingsImpl extends SettingGroupImpl implements AdvancedSettings {

    private final BooleanSetting queueConfigPackets = registerBoolean("queue_config_packets", true);
    private final BooleanSetting printNetworkingErrorsToLogs = registerBoolean("print_networking_errors_to_logs", true);
    private final BooleanSetting ignoreFabricSyncErrors = registerBoolean("ignore_fabric_sync_errors", false);
    private final BooleanSetting filterNonExistingGlyphs = registerBoolean("filter_non_existing_glyphs", true);
    private final VersionedBooleanSetting removeServerDescriptionSanitize = registerVersionedBoolean("remove_server_description_sanitize", andOlder(v1_21_11), false);
    private final VersionedBooleanSetting dontCreatePacketErrorCrashReports = registerVersionedBoolean("dont_create_packet_error_crash_reports", andOlder(v1_20_3), false);
    private final VersionedBooleanSetting disableSequencing = registerVersionedBoolean("disable_sequencing", andOlder(v1_18_2), false);

    public AdvancedSettingsImpl() {
        super("advanced");
    }

    @Override
    public BooleanSetting queueConfigPackets() {
        return this.queueConfigPackets;
    }

    @Override
    public BooleanSetting printNetworkingErrorsToLogs() {
        return this.printNetworkingErrorsToLogs;
    }

    @Override
    public BooleanSetting ignoreFabricSyncErrors() {
        return this.ignoreFabricSyncErrors;
    }

    @Override
    public BooleanSetting filterNonExistingGlyphs() {
        return this.filterNonExistingGlyphs;
    }

    @Override
    public VersionedBooleanSetting removeServerDescriptionSanitize() {
        return this.removeServerDescriptionSanitize;
    }

    @Override
    public VersionedBooleanSetting dontCreatePacketErrorCrashReports() {
        return this.dontCreatePacketErrorCrashReports;
    }

    @Override
    public VersionedBooleanSetting disableSequencing() {
        return this.disableSequencing;
    }

}
