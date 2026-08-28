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

package com.viaversion.viafabricplus.api.settings.base;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;

/**
 * Base setting class.
 *
 * @see SettingGroup
 * @see BooleanSetting
 * @see EnumSetting
 */
public interface Setting {

    /**
     * The name of the setting. This is the name displayed in the settings menu.
     *
     * @return The name of the setting
     */
    Component name();

    /**
     * Writes the setting to the given JsonObject.
     *
     * @param object The JsonObject to write to
     */
    void write(final JsonObject object);

    /**
     * Reads the setting from the given JsonObject.
     *
     * @param object The JsonObject to read from
     */
    void read(final JsonObject object);

}
