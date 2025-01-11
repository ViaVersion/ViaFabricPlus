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
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;


/**
 * Please migrate to the general {@link com.viaversion.viafabricplus.ViaFabricPlus} API point.
 */
@Deprecated
public interface LoadClassicProtocolExtensionCallback {

    @Deprecated
    Event<LoadClassicProtocolExtensionCallback> EVENT = LegacyCompatBridge.createArrayBacked(LoadClassicProtocolExtensionCallback.class, listeners -> classicProtocolExtension -> {
        for (LoadClassicProtocolExtensionCallback listener : listeners) {
            listener.onLoadClassicProtocolExtension(classicProtocolExtension);
        }
    });

    @Deprecated
    void onLoadClassicProtocolExtension(final ClassicProtocolExtension classicProtocolExtension);

}