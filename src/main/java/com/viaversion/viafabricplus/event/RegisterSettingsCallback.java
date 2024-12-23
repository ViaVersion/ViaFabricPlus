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

package com.viaversion.viafabricplus.event;

import com.viaversion.viafabricplus.settings.SettingsManager;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is fired when ViaFabricPlus has initialized its settings, and before it starts reading the values from the settings file.
 */
public interface RegisterSettingsCallback {

    Event<RegisterSettingsCallback> EVENT = EventFactory.createArrayBacked(RegisterSettingsCallback.class, listeners -> (settingsManager, state) -> {
        for (RegisterSettingsCallback listener : listeners) {
            listener.onRegisterSettings(settingsManager, state);
        }
    });

    void onRegisterSettings(final SettingsManager settingsManager, final State state);

    enum State {
        PRE, POST
    }

}