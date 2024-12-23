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

package com.viaversion.viafabricplus.base.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This class can be used to save data to a file.
 */
public abstract class AbstractSave {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path path;

    /**
     * @param name The name of the file.
     */
    public AbstractSave(final String name) {
        path = ViaFabricPlusImpl.INSTANCE.rootPath().resolve(name + ".json");
    }

    /**
     * This method should be called when the file should be initialized.
     * It will read the file and call the {@link #read(JsonObject)} method.
     */
    public void init() {
        if (Files.exists(path)) {
            try {
                read(GSON.fromJson(Files.readString(path), JsonObject.class));
            } catch (IOException e) {
                ViaFabricPlusImpl.INSTANCE.logger().error("Failed to read file: {}!", path.getFileName(), e);
            }
        }
    }

    /**
     * This method should be called when the file should be saved.
     */
    public void save() {
        try {
            final JsonObject object = new JsonObject();
            write(object);

            Files.writeString(path, GSON.toJson(object), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            ViaFabricPlusImpl.INSTANCE.logger().error("Failed to write file: {}!", path.getFileName(), e);
        }
    }

    public abstract void write(final JsonObject object);
    public abstract void read(final JsonObject object);

    public void postInit() {
    }

    public Path getPath() {
        return path;
    }

}
