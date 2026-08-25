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

import com.viaversion.viafabricplus.api.settings.base.Setting;
import net.minecraft.network.chat.Component;

public abstract class SettingImpl<T> implements Setting<T> {

    private final String key;
    private final Component component;
    private final T defaultValue;
    private T value;

    public SettingImpl(final String key, final Component component, final T defaultValue) {
        this.key = key;
        this.component = component;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    protected String key() {
        return this.key;
    }

    @Override
    public Component name() {
        return this.component;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

}
