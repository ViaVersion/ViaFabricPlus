/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.settings;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.event.InitializeSettingsCallback;
import de.florianmichael.viafabricplus.settings.base.AbstractSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.groups.*;
import de.florianmichael.viafabricplus.util.FileSaver;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingsSystem extends FileSaver {
    private final List<SettingGroup> groups = new ArrayList<>();

    public SettingsSystem() {
        super("settings.json");
    }

    @Override
    public void init() {
        addGroup(
                GeneralSettings.INSTANCE,
                BridgeSettings.INSTANCE,
                BedrockSettings.INSTANCE,
                MPPassSettings.INSTANCE,
                VisualSettings.INSTANCE,
                DebugSettings.INSTANCE
        );

        InitializeSettingsCallback.EVENT.invoker().onInitializeSettings();

        super.init();
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty("protocol", ViaLoadingBase.getClassWrapper().getTargetVersion().getVersion());
        for (SettingGroup group : groups) {
            for (AbstractSetting<?> setting : group.getSettings()) {
                setting.write(object);
            }
        }
    }

    @Override
    public void read(JsonObject object) {
        if (object.has("protocol")) {
            ViaLoadingBase.getClassWrapper().reload(InternalProtocolList.fromProtocolId(object.get("protocol").getAsInt()));
        }
        for (SettingGroup group : groups) {
            for (AbstractSetting<?> setting : group.getSettings()) {
                setting.read(object);
            }
        }
    }

    public void addGroup(final SettingGroup... groups) {
        Collections.addAll(this.groups, groups);
    }

    public List<SettingGroup> getGroups() {
        return groups;
    }
}
