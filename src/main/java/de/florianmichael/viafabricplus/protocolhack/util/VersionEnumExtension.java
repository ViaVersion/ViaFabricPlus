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
package de.florianmichael.viafabricplus.protocolhack.util;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.reflect.Enums;
import net.lenni0451.reflect.stream.RStream;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Map;

public class VersionEnumExtension {

    private static final ProtocolVersion autoDetect = ProtocolVersion.register(-2, "Auto Detect (1.7+ servers)");
    public static final VersionEnum AUTO_DETECT = Enums.newInstance(VersionEnum.class, "AUTO_DETECT", VersionEnum.UNKNOWN.ordinal(), new Class<?>[]{ProtocolVersion.class}, new Object[]{autoDetect});

    static {
        Enums.addEnumInstance(VersionEnum.class, AUTO_DETECT);
        RStream.of(VersionEnum.class).fields().by("VERSION_REGISTRY").<Map<ProtocolVersion, VersionEnum>>get().put(autoDetect, AUTO_DETECT);
        VersionEnum.SORTED_VERSIONS.add(AUTO_DETECT);
    }

    public static void init() {
        // calls static initializer
    }

}
