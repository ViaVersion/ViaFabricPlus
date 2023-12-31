/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.raphimc.vialoader.util.VersionEnum;

/**
 * This event is fired when the user changes the target version in the screen, or if the user joins a server with a different version.
 * If the user disconnects, the event will also be fired with the current version.
 */
public interface ChangeProtocolVersionCallback {

    Event<ChangeProtocolVersionCallback> EVENT = EventFactory.createArrayBacked(ChangeProtocolVersionCallback.class, listeners -> (oldVersion, newVersion) -> {
        for (ChangeProtocolVersionCallback listener : listeners) {
            listener.onChangeProtocolVersion(oldVersion, newVersion);
        }
    });

    void onChangeProtocolVersion(final VersionEnum oldVersion, final VersionEnum newVersion);
}
