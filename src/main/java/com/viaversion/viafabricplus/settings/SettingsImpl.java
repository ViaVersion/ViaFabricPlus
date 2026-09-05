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

package com.viaversion.viafabricplus.settings;

import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusEntrypoint;
import com.viaversion.viafabricplus.api.settings.Settings;
import com.viaversion.viafabricplus.api.settings.base.SettingGroup;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl;
import com.viaversion.viafabricplus.settings.base.SettingGroupImpl;
import com.viaversion.viafabricplus.settings.impl.AdvancedSettingsImpl;
import com.viaversion.viafabricplus.settings.impl.GeneralSettingsImpl;
import com.viaversion.viafabricplus.settings.impl.VisualSettingsImpl;
import com.viaversion.viafabricplus.util.JsonSave;
import com.viaversion.viafabricplus.util.LegacySaveMigrator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

public final class SettingsImpl implements Settings {

    private final List<SettingGroup> groups = new ArrayList<>();

    private final GeneralSettingsImpl general = new GeneralSettingsImpl();
    private final VisualSettingsImpl visual = new VisualSettingsImpl();
    private final AdvancedSettingsImpl advanced = new AdvancedSettingsImpl();

    public SettingsImpl() {
        this.groups.add(this.general);
        this.groups.add(this.visual);
        this.groups.add(this.advanced);
    }

    private String selectedProtocolVersion;

    public void init() {
        JsonSave.load(ViaFabricPlus.api().path().resolve("settings.json"), jsonObject -> {
            // Migrating into the current defaults leaves settings the old format didn't know at their default
            final JsonObject settings = LegacySaveMigrator.isLegacySettings(jsonObject)
                ? LegacySaveMigrator.migrateSettings(jsonObject, this.snapshot())
                : jsonObject;

            for (final SettingGroup group : this.groups) {
                group.read(settings);
            }
            this.selectedProtocolVersion = settings.get("selected_protocol_version").getAsString();
        }, this::snapshot);

        FabricLoader.getInstance().invokeEntrypoints("viafabricplus", ViaFabricPlusEntrypoint.class, ViaFabricPlusEntrypoint::onPostSettingsLoading);
    }

    private JsonObject snapshot() {
        final JsonObject object = new JsonObject();
        for (final SettingGroup group : this.groups) {
            group.write(object);
        }
        object.addProperty("selected_protocol_version", ViaFabricPlus.api().targetVersion().getName());
        return object;
    }

    public void postInit() {
        // Set target version AFTER protocol loading, so we can reach all versions
        if (this.selectedProtocolVersion != null) {
            if (this.general.saveSelectedProtocolVersion().isActive()) {
                final ProtocolVersion protocolVersion = ProtocolVersion.getClosest(this.selectedProtocolVersion);
                if (protocolVersion != null) {
                    ViaFabricPlus.api().setTargetVersion(protocolVersion);
                }
            } else {
                ViaFabricPlus.api().setTargetVersion(ProtocolTranslationImpl.NATIVE_VERSION);
            }
        }
    }

    @Override
    public GeneralSettingsImpl general() {
        return this.general;
    }

    @Override
    public VisualSettingsImpl visual() {
        return this.visual;
    }

    @Override
    public AdvancedSettingsImpl advanced() {
        return this.advanced;
    }

    @Override
    public SettingGroup register(final String key) {
        final SettingGroup group = new SettingGroupImpl(key);
        this.groups.add(group);
        return group;
    }

    @Override
    public List<SettingGroup> groups() {
        return new ArrayList<>(this.groups);
    }

}
