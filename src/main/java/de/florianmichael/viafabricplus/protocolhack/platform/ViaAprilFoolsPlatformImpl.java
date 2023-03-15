/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.protocolhack.platform;

import com.viaversion.viaversion.api.Via;
import de.florianmichael.vialoadingbase.util.JLoggerToLog4j;
import net.raphimc.viaaprilfools.platform.ViaAprilFoolsPlatform;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.logging.Logger;

public class ViaAprilFoolsPlatformImpl implements ViaAprilFoolsPlatform {
    private static final Logger LOGGER = new JLoggerToLog4j(LogManager.getLogger("ViaAprilFools"));

    public ViaAprilFoolsPlatformImpl() {
        this.init(this.getDataFolder());
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public File getDataFolder() {
        return Via.getPlatform().getDataFolder();
    }
}
