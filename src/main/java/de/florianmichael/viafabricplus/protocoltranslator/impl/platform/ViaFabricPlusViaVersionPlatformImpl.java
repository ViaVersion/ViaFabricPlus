/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.protocoltranslator.impl.platform;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import de.florianmichael.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusVLViaConfig;
import net.raphimc.vialoader.impl.platform.ViaVersionPlatformImpl;

import java.io.File;

public class ViaFabricPlusViaVersionPlatformImpl extends ViaVersionPlatformImpl {

    public ViaFabricPlusViaVersionPlatformImpl(File rootFolder) {
        super(rootFolder);
    }

    @Override
    protected AbstractViaConfig createConfig() {
        return new ViaFabricPlusVLViaConfig(new File(this.getDataFolder(), "viaversion.yml"));
    }

}
