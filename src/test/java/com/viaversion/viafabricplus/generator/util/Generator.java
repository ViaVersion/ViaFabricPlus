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

package com.viaversion.viafabricplus.generator.util;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@FunctionalInterface
public interface Generator {

    StringBuilder generate(final ProtocolVersion nativeVersion);

    default void writeToFile(final ProtocolVersion nativeVersion, final File directory, final String name) {
        final Path path = directory.toPath().resolve(name);
        try {
            Files.write(path, generate(nativeVersion).toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void printToConsole(final ProtocolVersion nativeVersion) {
        System.out.println(generate(nativeVersion).toString());
    }

}
