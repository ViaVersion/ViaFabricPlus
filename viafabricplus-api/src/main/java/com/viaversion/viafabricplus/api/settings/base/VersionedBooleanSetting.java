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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;

/**
 * Boolean setting that can be enabled or disabled for specific protocol versions.
 *
 * @see BooleanSetting
 * @see Setting
 */
public interface VersionedBooleanSetting extends BooleanSetting {

    /**
     * The version range the setting is valid for.
     *
     * @return the version range
     */
    ProtocolVersionRange versionRange();

    /**
     * Checks if the setting is active for the given protocol version.
     *
     * @param version the protocol version
     * @return true if the setting is active, false otherwise
     */
    boolean isActive(final ProtocolVersion version);

    /**
     * The value of the setting.
     *
     * @return the value of the setting
     */
    boolean value();

}
