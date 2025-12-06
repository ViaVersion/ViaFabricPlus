/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.api.settings.type;

import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.settings.AbstractSetting;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.network.chat.MutableComponent;

public class VersionedBooleanSetting extends AbstractSetting<Integer> {

    public static final int AUTO_INDEX = 2;
    public static final int DISABLED_INDEX = 1;
    public static final int ENABLED_INDEX = 0;

    private final VersionRange protocolRange;

    public VersionedBooleanSetting(SettingGroup parent, MutableComponent name, VersionRange protocolRange) {
        super(parent, name, AUTO_INDEX);

        this.protocolRange = protocolRange;
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
        return isEnabled(ViaFabricPlus.getImpl().getTargetVersion());
    }

    public boolean isEnabled(final ProtocolVersion version) {
        if (isAuto()) {
            return protocolRange.contains(version);
        } else {
            return getValue() == ENABLED_INDEX;
        }
    }

    public VersionRange getProtocolRange() {
        return protocolRange;
    }

}
