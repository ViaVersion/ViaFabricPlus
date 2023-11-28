/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.save;

import de.florianmichael.viafabricplus.event.LoadSaveFilesCallback;
import de.florianmichael.viafabricplus.save.impl.AccountsSave;
import de.florianmichael.viafabricplus.save.impl.SettingsSave;
import de.florianmichael.viafabricplus.settings.SettingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveManager {
    private final List<AbstractSave> saves = new ArrayList<>();

    private final SettingsSave settingsSave;
    private final AccountsSave accountsSave;

    public SaveManager(final SettingsManager settingsManager) {
        LoadSaveFilesCallback.EVENT.invoker().onLoadSaveFiles(this, LoadSaveFilesCallback.State.PRE);

        // Register saves
        add(
                settingsSave = new SettingsSave(settingsManager),
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

        LoadSaveFilesCallback.EVENT.invoker().onLoadSaveFiles(this, LoadSaveFilesCallback.State.POST);
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
