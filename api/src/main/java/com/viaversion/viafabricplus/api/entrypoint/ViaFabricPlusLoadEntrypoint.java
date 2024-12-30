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

package com.viaversion.viafabricplus.api.entrypoint;

import com.viaversion.viafabricplus.api.ViaFabricPlusBase;

/**
 * Entrypoint called before the ViaFabricPlus loading cycle starts. To be used by addons to register their own callbacks.
 * <p>
 * See {@link com.viaversion.viafabricplus.api.events.LoadingCycleCallback} for more information.
 */
@FunctionalInterface
public interface ViaFabricPlusLoadEntrypoint {

    void onPlatformLoad(final ViaFabricPlusBase platform);

}