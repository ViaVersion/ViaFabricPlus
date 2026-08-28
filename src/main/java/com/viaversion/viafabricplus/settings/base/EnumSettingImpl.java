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

package com.viaversion.viafabricplus.settings.base;

import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.api.settings.base.EnumSetting;
import net.minecraft.network.chat.Component;

public final class EnumSettingImpl<T extends Enum<T> & EnumSetting.EnumValue> extends SettingImpl implements EnumSetting<T> {

    private final T defaultValue;
    private T value;

    public EnumSettingImpl(final String key, final Component component, final T defaultValue) {
        super(key, component);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public T[] values() {
        return this.defaultValue().getDeclaringClass().getEnumConstants();
    }

    @Override
    public void write(final JsonObject object) {
        object.addProperty(this.key(), this.value().name());
    }

    @Override
    public void read(final JsonObject object) {
        for (final T constant : this.defaultValue().getDeclaringClass().getEnumConstants()) {
            if (constant.name().equals(object.get(this.key()).getAsString())) {
                this.setValue(constant);
                return;
            }
        }
    }

}
