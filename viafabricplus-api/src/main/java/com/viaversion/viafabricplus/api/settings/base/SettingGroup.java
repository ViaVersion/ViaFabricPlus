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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import java.util.List;
import net.minecraft.network.chat.Component;

/**
 * A group of settings.
 *
 * @see Setting
 */
public interface SettingGroup {

    /**
     * The name of the group. Used for displaying the group in the settings menu.
     *
     * @return The name of the group
     */
    Component name();

    /**
     * All settings in the group.
     *
     * @return All settings in the group
     */
    List<Setting> settings();

    /**
     * Registers a new setting.
     *
     * @param key          The translation key of the setting. Requires all settings to be prefixed with this key as well.
     * @param defaultValue The default value of the setting.
     * @return The newly created setting
     */
    BooleanSetting registerBoolean(final String key, final boolean defaultValue);

    /**
     * Registers a new enum setting.
     *
     * @param key          The translation key of the setting. Requires all settings to be prefixed with this key as well.
     * @param defaultValue The default value of the setting.
     * @param <T>          The type of the enum
     * @return The newly created enum setting
     */
    <T extends Enum<T> & EnumSetting.EnumValue> EnumSetting<T> registerEnum(final String key, final T defaultValue);

    /**
     * Registers a new versioned boolean setting.
     *
     * @param key          The translation key of the setting. Requires all settings to be prefixed with this key as well.
     * @param versionRange The version range the setting is valid for.
     * @param defaultValue The default value of the setting.
     * @return The newly created versioned boolean setting
     */
    VersionedBooleanSetting registerVersionedBoolean(final String key, final ProtocolVersionRange versionRange, final boolean defaultValue);

    /**
     * Writes the settings to the given JsonObject.
     *
     * @param object The JsonObject to write to
     */
    void write(final JsonObject object);

    /**
     * Reads the settings from the given JsonObject.
     *
     * @param object The JsonObject to read from
     */
    void read(final JsonObject object);

}
