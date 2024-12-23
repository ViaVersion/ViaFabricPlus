/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.util;

import net.lenni0451.reflect.ClassLoaders;
import net.lenni0451.reflect.stream.RStream;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Allows the user to override the Via* jar files with custom ones using the "jars" folder in the run directory
 */
@ApiStatus.Internal
public class ClassLoaderPriorityUtil {

    /**
     * Loads all overriding jars
     */
    public static void loadOverridingJars(final Path path, final Logger logger) {
        try {
            final Path jars = path.resolve("jars");
            if (!Files.exists(jars)) {
                Files.createDirectory(jars);
                return;
            }

            final File[] files = jars.toFile().listFiles();
            if (files != null && files.length > 0) {
                final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
                try {
                    final ClassLoader actualLoader = RStream.of(oldLoader).fields().by("urlLoader").get();
                    Thread.currentThread().setContextClassLoader(actualLoader);

                    logger.warn("================================");
                    logger.warn("OVERRIDING JARS LOADING! THIS CAN CAUSE UNEXPECTED BEHAVIOR AND ISSUES!");
                    for (File file : files) {
                        if (file.getName().endsWith(".jar")) {
                            ClassLoaders.loadToFront(file.toURI().toURL());
                            logger.warn(" -> {}", file.getName());
                        }
                    }
                    logger.warn("================================");
                } finally {
                    Thread.currentThread().setContextClassLoader(oldLoader);
                }
            }
        } catch (Throwable e) {
            logger.error("Failed to load overriding jars", e);
        }
    }

}
