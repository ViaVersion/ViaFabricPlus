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
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.api.events.LoadingCycleEvent;
import com.viaversion.viafabricplus.api.settings.Settings;
import com.viaversion.viafabricplus.api.settings.base.SettingGroup;
import com.viaversion.viafabricplus.api.settings.impl.AdvancedSettings;
import com.viaversion.viafabricplus.api.settings.impl.GeneralSettings;
import com.viaversion.viafabricplus.api.settings.impl.VisualSettings;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.base.SettingGroupImpl;
import com.viaversion.viafabricplus.settings.impl.AdvancedSettingsImpl;
import com.viaversion.viafabricplus.settings.impl.GeneralSettingsImpl;
import com.viaversion.viafabricplus.settings.impl.VisualSettingsImpl;
import com.viaversion.viafabricplus.util.JsonSave;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    public void init(final ViaFabricPlusImpl impl) {
        impl.runLoadingCycleEvents(LoadingCycleEvent.LoadingCycle.PRE_SETTINGS_LOAD);
        final Path path = impl.path().resolve("settings.json");
        JsonSave.read(path, jsonObject -> {
            for (final SettingGroup group : this.groups) {
                group.read(jsonObject);
            }
            this.selectedProtocolVersion = jsonObject.get("selected_protocol_version").getAsString();
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> JsonSave.write(path, () -> {
            final JsonObject object = new JsonObject();
            for (final SettingGroup group : this.groups) {
                group.write(object);
            }
            object.addProperty("selected_protocol_version", ProtocolTranslator.getTargetVersion().getName());
            return object;
        })));
        impl.runLoadingCycleEvents(LoadingCycleEvent.LoadingCycle.POST_SETTINGS_LOAD);
    }

    public void postInit() {
        // Set target version AFTER protocol loading, so we can reach all versions
        if (this.selectedProtocolVersion != null) {
            if (this.general.saveSelectedProtocolVersion().value()) {
                final ProtocolVersion protocolVersion = ProtocolVersion.getClosest(this.selectedProtocolVersion);
                if (protocolVersion != null) {
                    ProtocolTranslator.setTargetVersion(protocolVersion);
                }
            } else {
                ProtocolTranslator.setTargetVersion(ProtocolTranslator.NATIVE_VERSION);
            }
        }
    }

    @Override
    public GeneralSettings general() {
        return this.general;
    }

    @Override
    public VisualSettings visual() {
        return this.visual;
    }

    @Override
    public AdvancedSettings advanced() {
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
