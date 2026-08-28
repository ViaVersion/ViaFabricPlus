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

package com.viaversion.viafabricplus.api;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.protocoltranslator.Conversions;
import com.viaversion.viafabricplus.api.protocoltranslator.Limitations;
import com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation;
import com.viaversion.viafabricplus.api.screen.Screens;
import com.viaversion.viafabricplus.api.settings.Settings;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * General API point for mods. Get an instance via {@link ViaFabricPlus#api()}.
 */
public interface ViaFabricPlusAPI {

    /**
     * Returns an <b>internally based API version</b> incremented with meaningful API changes.
     * This includes breaking changes to the existing API and larger additions.
     *
     * @return API version incremented with meaningful API changes
     */
    default int apiVersion() {
        return 7;
    }

    /**
     * The version of the mod displayed in ModMenu and other places (e.g., 5.0.0)
     *
     * @return the version of the mod
     */
    String version();

    /**
     * The implementation version of the mod formatted as "git-ViaFabricPlus-{@link #version()}:{commit hash}".
     *
     * @return the implementation version of the mod
     */
    String implVersion();

    /**
     * The root path inside the config folder where configuration files are stored.
     *
     * @return The root path of the mod
     */
    Path path();

    Settings settings();

    ProtocolTranslation protocolTranslation();

    default ProtocolVersion targetVersion() {
        return this.protocolTranslation().targetVersion();
    }

    default void setTargetVersion(final ProtocolVersion targetVersion) {
        this.protocolTranslation().setTargetVersion(targetVersion);
    }

    default void addChangeProtocolVersionListener(final BiConsumer<ProtocolVersion, ProtocolVersion> listener) {
        this.protocolTranslation().addChangeProtocolVersionListener(listener);
    }

    default UserConnection userConnection() {
        return this.protocolTranslation().userConnection();
    }

    Conversions conversions();

    Limitations limitations();

    Screens screens();

}
