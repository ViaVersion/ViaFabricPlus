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

package de.florianmichael.viafabricplus.event;

import de.florianmichael.viafabricplus.save.SaveManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is fired when ViaFabricPlus has loaded its save files, and before it starts reading the values from the save files.
 */
public interface LoadSaveFilesCallback {

    Event<LoadSaveFilesCallback> EVENT = EventFactory.createArrayBacked(LoadSaveFilesCallback.class, listeners -> (saveManager, state) -> {
        for (LoadSaveFilesCallback listener : listeners) {
            listener.onLoadSaveFiles(saveManager, state);
        }
    });

    void onLoadSaveFiles(final SaveManager saveManager, final State state);

    enum State {
        PRE, POST
    }

}
