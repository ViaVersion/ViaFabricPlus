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

package de.florianmichael.viafabricplus;

import de.florianmichael.viafabricplus.save.SaveManager;
import de.florianmichael.viafabricplus.settings.SettingsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Please migrate to the general {@link com.viaversion.viafabricplus.ViaFabricPlus} API point.
 */
@Deprecated
public class ViaFabricPlus {

    static final ViaFabricPlus INSTANCE = new ViaFabricPlus();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus-Legacy");

    private final SettingsManager settingsManager = new SettingsManager();
    private final SaveManager saveManager = new SaveManager();

    @Deprecated
    public static ViaFabricPlus global() {
        LegacyCompatBridge.warn();
        return INSTANCE;
    }

    @Deprecated
    public Logger getLogger() {
        return logger;
    }

    @Deprecated
    public File getDirectory() {
        return com.viaversion.viafabricplus.ViaFabricPlus.getImpl().rootPath().toFile();
    }

    @Deprecated
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Deprecated
    public SaveManager getSaveManager() {
        return saveManager;
    }

}