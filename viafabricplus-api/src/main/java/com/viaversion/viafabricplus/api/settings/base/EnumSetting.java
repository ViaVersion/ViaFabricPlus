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

import net.minecraft.network.chat.Component;

/**
 * Base enum setting class.
 *
 * @param <T> The enum type.
 * @see Setting
 */
public interface EnumSetting<T extends EnumSetting.EnumValue> extends Setting {

    /**
     * The current value of the setting.
     *
     * @return The current value of the setting.
     */
    T value();

    /**
     * Sets the value of the setting.
     *
     * @param value The new value of the setting.
     */
    void setValue(T value);

    /**
     * The default value of the setting.
     *
     * @return The default value of the setting.
     */
    T defaultValue();

    /**
     * All possible values of the setting.
     *
     * @return All possible values of the setting.
     */
    T[] values();

    /**
     * Represents a single value of an enum setting. Required to be implemented by the enum.
     */
    interface EnumValue {

        /**
         * The component of the enum value. Used for displaying the value in the settings menu.
         *
         * @return The component of the enum value.
         */
        Component component();

    }

}
