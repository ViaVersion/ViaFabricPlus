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

public interface SettingGroup {

    Component name();

    List<Setting<?>> settings();

    BooleanSetting registerBoolean(final String key, final Boolean defaultValue);

    <T extends Enum<T> & EnumSetting.EnumValue> EnumSetting<T> registerEnum(final String key, final T defaultValue);

    VersionedBooleanSetting registerVersionedBoolean(final String key, final ProtocolVersionRange versionRange, final Boolean defaultValue);

    void write(final JsonObject object);

    void read(final JsonObject object);

}
