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

package com.viaversion.viafabricplus.settings;

import com.viaversion.viafabricplus.api.events.LoadingCycleCallback;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.settings.impl.AuthenticationSettings;
import com.viaversion.viafabricplus.settings.impl.BedrockSettings;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SettingsManager {

    public static final SettingsManager INSTANCE = new SettingsManager();

    private final List<SettingGroup> groups = new ArrayList<>();

    public void init() {
        Events.LOADING_CYCLE.invoker().onLoadCycle(LoadingCycleCallback.LoadingCycle.PRE_SETTINGS_LOAD);

        addGroup(
            GeneralSettings.INSTANCE,
            BedrockSettings.INSTANCE,
            AuthenticationSettings.INSTANCE,
            DebugSettings.INSTANCE
        );

        Events.LOADING_CYCLE.invoker().onLoadCycle(LoadingCycleCallback.LoadingCycle.POST_SETTINGS_LOAD);
    }

    public void addGroup(final SettingGroup... groups) {
        Collections.addAll(this.groups, groups);
    }

    public List<SettingGroup> getGroups() {
        return groups;
    }

}
