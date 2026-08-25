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

package com.viaversion.viafabricplus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class JsonSave {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void read(final Path path, final Consumer<JsonObject> consumer) {
        if (Files.exists(path)) {
            try (final BufferedReader reader = Files.newBufferedReader(path)) {
                final JsonObject object = GSON.fromJson(reader, JsonObject.class);
                if (object != null) {
                    consumer.accept(object);
                } else {
                    ViaFabricPlusImpl.impl().logger().error("The file {} is empty!", path.getFileName());
                }
            } catch (Exception e) {
                ViaFabricPlusImpl.impl().logger().error("Failed to read file: {}!", path.getFileName(), e);
            }
        }
    }

    public static void write(final Path path, final Supplier<JsonObject> supplier) {
        try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(supplier.get(), writer);
        } catch (Exception e) {
            ViaFabricPlusImpl.impl().logger().error("Failed to write file: {}!", path.getFileName(), e);
        }
    }

}
