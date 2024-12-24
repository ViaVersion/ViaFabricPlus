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

package com.viaversion.viafabricplus.settings.base;

import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.base.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.settings.ButtonSettingRenderer;
import net.minecraft.text.MutableText;

public class ButtonSetting extends AbstractSetting<Runnable> {

    public ButtonSetting(SettingGroup parent, MutableText name, Runnable onClick) {
        super(parent, name, onClick);
    }

    @Override
    public VFPListEntry makeSettingRenderer() {
        return new ButtonSettingRenderer(this);
    }

    public MutableText displayValue() {
        return getName();
    }

    @Override
    public void write(JsonObject object) {}

    @Override
    public void read(JsonObject object) {}

}
