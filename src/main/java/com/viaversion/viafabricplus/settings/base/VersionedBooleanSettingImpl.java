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
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import net.minecraft.network.chat.Component;

public final class VersionedBooleanSettingImpl extends BooleanSettingImpl implements VersionedBooleanSetting {

    private final ProtocolVersionRange versionRange;

    public VersionedBooleanSettingImpl(final String key, final Component component, final ProtocolVersionRange versionRange, final boolean defaultValue) {
        super(key, component, defaultValue);
        this.versionRange = versionRange;
    }

    @Override
    public ProtocolVersionRange versionRange() {
        return this.versionRange;
    }

    @Override
    public boolean value() {
        return super.isActive();
    }

    @Override
    public void write(final JsonObject object) {
        object.addProperty(this.key(), this.isActive());
    }

    @Override
    public void read(final JsonObject object) {
        this.setActive(object.get(this.key()).getAsBoolean());
    }

    @Override
    public boolean isActive() {
        return this.versionRange.contains(ViaFabricPlusImpl.impl().targetVersion()) && super.isActive();
    }

    @Override
    public boolean isActive(final ProtocolVersion version) {
        return this.versionRange.contains(version) && super.isActive();
    }

}
