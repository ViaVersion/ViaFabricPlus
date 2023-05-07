/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.base.settings.type_impl;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.base.screen.MappedSlotEntry;
import de.florianmichael.viafabricplus.base.screen.impl.settings.settingrenderer.BooleanSettingRenderer;
import de.florianmichael.viafabricplus.base.settings.base.AbstractSetting;
import de.florianmichael.viafabricplus.base.settings.base.SettingGroup;
import net.minecraft.text.MutableText;

public class BooleanSetting extends AbstractSetting<Boolean> {

    public BooleanSetting(SettingGroup parent, MutableText name, Boolean defaultValue) {
        super(parent, name, defaultValue);
    }

    @Override
    public MappedSlotEntry makeSettingRenderer() {
        return new BooleanSettingRenderer(this);
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty(getTranslationKey(), getValue());
    }

    @Override
    public void read(JsonObject object) {
        if (!object.has(getTranslationKey())) return;

        setValue(object.get(getTranslationKey()).getAsBoolean());
    }
}
