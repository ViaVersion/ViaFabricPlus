/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.api.events;

/**
 * This event is fired in various loading stages of ViaFabricPlus. See {@link LoadingCycle} for more information.
 */
public interface LoadingCycleCallback {

    enum LoadingCycle {

        /**
         * Before the settings are loaded.
         */
        PRE_SETTINGS_LOAD,

        /**
         * After the settings are loaded.
         */
        POST_SETTINGS_LOAD,

        /**
         * Before the files are loaded.
         */
        PRE_FILES_LOAD,

        /**
         * After the files are loaded.
         */
        POST_FILES_LOAD,

        /**
         * Before ViaVersion is loaded.
         */
        PRE_VIAVERSION_LOAD,

        /**
         * After ViaVersion is loaded.
         */
        POST_VIAVERSION_LOAD,

        /**
         * Final stage after everything is loaded.
         */
        FINAL_LOAD,

        /**
         * After the game is loaded.
         */
        POST_GAME_LOAD

    }

    void onLoadCycle(final LoadingCycle cycle);

}
