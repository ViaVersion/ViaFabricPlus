/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.util;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.lenni0451.reflect.ClassLoaders;
import net.lenni0451.reflect.stream.RStream;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;

/**
 * Allows the user to override the Via* jar files with custom ones using the "jars" folder in the run directory
 */
@ApiStatus.Internal
public class ClassLoaderPriorityUtil {

    /**
     * Loads all overriding jars
     */
    public static void loadOverridingJars(final File directory) {
        try {
            final File jarsDirectory = new File(directory, "jars");
            if (!jarsDirectory.exists()) {
                jarsDirectory.mkdir();
                return;
            }

            final File[] files = jarsDirectory.listFiles();
            if (files != null && files.length > 0) {
                final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
                try {
                    final ClassLoader actualLoader = RStream.of(oldLoader).fields().by("urlLoader").get();
                    Thread.currentThread().setContextClassLoader(actualLoader);
                    for (File file : files) {
                        if (file.getName().endsWith(".jar")) {
                            ClassLoaders.loadToFront(file.toURI().toURL());
                            ViaFabricPlus.global().getLogger().info("Loaded overriding jar " + file.getName());
                        }
                    }
                } finally {
                    Thread.currentThread().setContextClassLoader(oldLoader);
                }
            }
        } catch (Throwable e) {
            ViaFabricPlus.global().getLogger().error("Failed to load overriding jars", e);
        }
    }

}
