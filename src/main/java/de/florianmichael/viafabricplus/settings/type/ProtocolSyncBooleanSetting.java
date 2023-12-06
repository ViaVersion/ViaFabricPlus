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
package de.florianmichael.viafabricplus.settings.type;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.settings.AbstractSetting;
import de.florianmichael.viafabricplus.screen.MappedSlotEntry;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.ProtocolSyncBooleanSettingRenderer;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.text.MutableText;
import net.raphimc.vialoader.util.VersionRange;

public class ProtocolSyncBooleanSetting extends AbstractSetting<Integer> {
    public final static int AUTO = 2;
    public final static int ENABLED = 0;

    private final VersionRange protocolRange;

    public ProtocolSyncBooleanSetting(SettingGroup parent, MutableText name, VersionRange protocolRange) {
        super(parent, name, 2);

        this.protocolRange = protocolRange;
    }

    @Override
    public MappedSlotEntry makeSettingRenderer() {
        return new ProtocolSyncBooleanSettingRenderer(this);
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty(getTranslationKey(), getValue());
    }

    @Override
    public void read(JsonObject object) {
        if (!object.has(getTranslationKey())) return;

        if (object.get(getTranslationKey()).isJsonPrimitive() && object.get(getTranslationKey()).getAsJsonPrimitive().isBoolean()) { // Migrate configs, will be removed in the future
            setValue(AUTO);
            return;
        }

        setValue(object.get(getTranslationKey()).getAsInt());
    }

    public boolean isAuto() {
        return getValue() == AUTO;
    }

    public boolean isEnabled() {
        if (isAuto()) {
            return protocolRange.contains(ProtocolHack.getTargetVersion());
        }

        return getValue() == ENABLED;
    }

    public VersionRange getProtocolRange() {
        return protocolRange;
    }
}
