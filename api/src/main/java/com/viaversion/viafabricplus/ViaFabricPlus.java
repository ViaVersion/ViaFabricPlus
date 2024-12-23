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

package com.viaversion.viafabricplus;

import com.viaversion.viafabricplus.api.ViaFabricPlusBase;
import org.jetbrains.annotations.ApiStatus;

/**
 * Holder class for the {@link ViaFabricPlusBase} implementation
 */
public class ViaFabricPlus {

    private static ViaFabricPlusBase impl;

    /**
     * Invoked by the internals to set the ViaFabricPlusBase implementation, DO NOT CALL THIS METHOD
     */
    @ApiStatus.Internal
    public static void init(ViaFabricPlusBase impl) {
        if (ViaFabricPlus.impl != null) {
            throw new IllegalStateException("ViaFabricPlus has already been initialized!");
        }
        ViaFabricPlus.impl = impl;
    }

    /**
     * @return the ViaFabricPlusBase implementation which is set by the internals
     */
    public static ViaFabricPlusBase getImpl() {
        if (impl == null) {
            throw new IllegalStateException("ViaFabricPlus has not been initialized yet!");
        }
        return impl;
    }

}
