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
package de.florianmichael.viafabricplus.settings.type_impl;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.screen.settings.AbstractSettingRenderer;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.ProtocolSyncBooleanSettingRenderer;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.groups.GeneralSettings;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.ProtocolRange;
import net.minecraft.text.MutableText;

public class ProtocolSyncBooleanSetting extends BooleanSetting {
    private final ProtocolRange protocolRange;

    public ProtocolSyncBooleanSetting(SettingGroup parent, MutableText name, ProtocolRange protocolRange) {
        super(parent, name, true);

        this.protocolRange = protocolRange;
    }

    @Override
    public AbstractSettingRenderer makeSettingRenderer() {
        return new ProtocolSyncBooleanSettingRenderer(this);
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

    @Override
    public Boolean getValue() {
        if (GeneralSettings.INSTANCE.automaticallyChangeValuesBasedOnTheCurrentVersion.getValue()) return this.getProtocolRange().contains(ViaLoadingBase.getClassWrapper().getTargetVersion());

        return super.getValue();
    }

    public ProtocolRange getProtocolRange() {
        return protocolRange;
    }
}
