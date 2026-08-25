/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2019-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - Raphael Koppensteiner
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
import com.viaversion.viafabricplus.api.settings.base.EnumSetting;
import com.viaversion.viafabricplus.api.settings.impl.GeneralSettings;
import com.viaversion.viafabricplus.api.settings.impl.ItemFilter;
import com.viaversion.viafabricplus.api.settings.impl.Orientation;
import com.viaversion.viafabricplus.api.settings.impl.PacketTranslationError;
import com.viaversion.viafabricplus.settings.base.SettingGroupImpl;

public final class GeneralSettingsImpl extends SettingGroupImpl implements GeneralSettings {

    private final EnumSetting<Orientation> multiplayerScreenButtonOrientation = registerEnum("multiplayer_screen_button_orientation", Orientation.RIGHT_TOP);
    private final EnumSetting<Orientation> addServerScreenButtonOrientation = registerEnum("add_server_screen_button_orientation", Orientation.RIGHT_TOP);
    private final EnumSetting<Orientation> directConnectScreenButtonOrientation = registerEnum("direct_connect_screen_button_orientation", Orientation.RIGHT_TOP);
    private final EnumSetting<ItemFilter> removeNotAvailableItemsFromCreativeTab = registerEnum("filter_creative_tabs", ItemFilter.VANILLA_AND_MODDED);
    private final BooleanSetting saveSelectedProtocolVersion = registerBoolean("save_selected_protocol_version", true);
    private final BooleanSetting showAdvertisedServerVersion = registerBoolean("show_advertised_server_version", true);
    private final EnumSetting<PacketTranslationError> ignorePacketTranslationErrors = registerEnum("ignore_packet_translation_errors", PacketTranslationError.KICK);
    private final BooleanSetting loadSkinsAndSkullsInLegacyVersions = registerBoolean("load_skins_and_skulls_in_legacy_versions", true);
    private final BooleanSetting betaCraftAuthentication = registerBoolean("beta_craft_authentication", true);

    public GeneralSettingsImpl() {
        super("general");
    }

    @Override
    public EnumSetting<Orientation> multiplayerScreenButtonOrientation() {
        return this.multiplayerScreenButtonOrientation;
    }

    @Override
    public EnumSetting<Orientation> addServerScreenButtonOrientation() {
        return this.addServerScreenButtonOrientation;
    }

    @Override
    public EnumSetting<Orientation> directConnectScreenButtonOrientation() {
        return this.directConnectScreenButtonOrientation;
    }

    @Override
    public EnumSetting<ItemFilter> removeNotAvailableItemsFromCreativeTab() {
        return this.removeNotAvailableItemsFromCreativeTab;
    }

    @Override
    public BooleanSetting saveSelectedProtocolVersion() {
        return this.saveSelectedProtocolVersion;
    }

    @Override
    public BooleanSetting showAdvertisedServerVersion() {
        return this.showAdvertisedServerVersion;
    }

    @Override
    public EnumSetting<PacketTranslationError> ignorePacketTranslationErrors() {
        return this.ignorePacketTranslationErrors;
    }

    @Override
    public BooleanSetting loadSkinsAndSkullsInLegacyVersions() {
        return this.loadSkinsAndSkullsInLegacyVersions;
    }

    @Override
    public BooleanSetting betaCraftAuthentication() {
        return this.betaCraftAuthentication;
    }

}
