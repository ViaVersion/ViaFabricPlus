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

package com.viaversion.viafabricplus;

import com.google.common.base.Preconditions;
import com.viaversion.viafabricplus.api.ViaFabricPlusAPI;
import com.viaversion.viafabricplus.api.ViaFabricPlusBase;
import org.jetbrains.annotations.ApiStatus;

public final class ViaFabricPlus {

    private static ViaFabricPlusAPI api;

    @ApiStatus.Internal
    public static void init(final ViaFabricPlusAPI api) {
        Preconditions.checkArgument(ViaFabricPlus.api == null, "ViaFabricPlus has already been initialized!");
        ViaFabricPlus.api = api;
    }

    /**
     * Returns the API instance associated with this implementation.
     *
     * @return The API instance
     */
    public static ViaFabricPlusAPI api() {
        Preconditions.checkArgument(api != null, "ViaFabricPlus has not been initialized!");
        return api;
    }

    // -----

    private static final ViaFabricPlusBase legacy = new ViaFabricPlusBase() {};
    @Deprecated
    public static ViaFabricPlusBase getImpl() {
        return legacy;
    }

}
