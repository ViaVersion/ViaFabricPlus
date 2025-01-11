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

package de.florianmichael.viafabricplus.event;

import de.florianmichael.viafabricplus.LegacyCompatBridge;
import net.fabricmc.fabric.api.event.Event;

/**
 * Please migrate to the general {@link com.viaversion.viafabricplus.ViaFabricPlus} API point.
 */
@Deprecated
public interface PostGameLoadCallback {

    @Deprecated
    Event<PostGameLoadCallback> EVENT = LegacyCompatBridge.createArrayBacked(PostGameLoadCallback.class, listeners -> () -> {
        for (PostGameLoadCallback listener : listeners) {
            listener.postGameLoad();
        }
    });

    @Deprecated
    void postGameLoad();

}