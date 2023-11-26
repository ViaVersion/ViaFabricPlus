/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.settings.base;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.screen.base.VFPListEntry;
import de.florianmichael.viafabricplus.screen.settings.VersionedBooleanSettingRenderer;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.text.MutableText;
import net.raphimc.vialoader.util.VersionRange;

public class VersionedBooleanSetting extends AbstractSetting<Integer> {
    private static final int AUTO_INDEX = 2;
    private static final int DISABLED_INDEX = 1;
    private static final int ENABLED_INDEX = 0;

    private final VersionRange protocolRange;

    public VersionedBooleanSetting(SettingGroup parent, MutableText name, VersionRange protocolRange) {
        super(parent, name, AUTO_INDEX);

        this.protocolRange = protocolRange;
    }

    @Override
    public VFPListEntry makeSettingRenderer() {
        return new VersionedBooleanSettingRenderer(this);
    }

    @Override
    public void write(JsonObject object) {
        object.addProperty(getTranslationKey(), getValue() == AUTO_INDEX ? "auto" : getValue() == ENABLED_INDEX ? "enabled" : "disabled");
    }

    @Override
    public void read(JsonObject object) {
        final String selected = object.get(getTranslationKey()).getAsString();

        setValue(selected.equals("auto") ? AUTO_INDEX : selected.equals("enabled") ? ENABLED_INDEX : DISABLED_INDEX);
    }

    public boolean isAuto() {
        return getValue() == AUTO_INDEX;
    }

    public boolean isEnabled() {
        if (isAuto()) {
            return protocolRange.contains(ProtocolHack.getTargetVersion());
        }

        return getValue() == ENABLED_INDEX;
    }

    public VersionRange getProtocolRange() {
        return protocolRange;
    }

}
