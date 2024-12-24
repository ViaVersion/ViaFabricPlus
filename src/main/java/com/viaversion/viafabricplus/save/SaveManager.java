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

package com.viaversion.viafabricplus.save;

import com.viaversion.viafabricplus.api.events.LoadingCycleCallback;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.save.impl.AccountsSave;
import com.viaversion.viafabricplus.save.impl.SettingsSave;
import com.viaversion.viafabricplus.settings.SettingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SaveManager {

    public static final SaveManager INSTANCE = new SaveManager();

    private final List<AbstractSave> saves = new ArrayList<>();

    private SettingsSave settingsSave;
    private AccountsSave accountsSave;

    public void init() {
        Events.LOADING_CYCLE.invoker().onLoadCycle(LoadingCycleCallback.LoadingCycle.PRE_FILES_LOAD);

        // Register saves
        add(
                settingsSave = new SettingsSave(),
                accountsSave = new AccountsSave()
        );

        // Load save files
        for (AbstractSave save : saves) {
            save.init();
        }

        // Save the save files on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (AbstractSave save : saves) {
                save.save();
            }
        }));
    }

    public void postInit() {
        for (AbstractSave save : saves) {
            save.postInit();
        }
        Events.LOADING_CYCLE.invoker().onLoadCycle(LoadingCycleCallback.LoadingCycle.POST_FILES_LOAD);
    }

    public void add(final AbstractSave... saves) {
        this.saves.addAll(Arrays.asList(saves));
    }

    public SettingsSave getSettingsSave() {
        return settingsSave;
    }

    public AccountsSave getAccountsSave() {
        return accountsSave;
    }

}
