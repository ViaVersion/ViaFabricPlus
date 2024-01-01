/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class can be used to save data to a file.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class AbstractSave {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File file;

    /**
     * @param name The name of the file.
     */
    public AbstractSave(final String name) {
        file = new File(ViaFabricPlus.global().getDirectory(), name + ".json");
    }

    /**
     * This method should be called when the file should be initialized.
     * It will read the file and call the {@link #read(JsonObject)} method.
     */
    public void init() {
        if (file.exists()) {
            try (final FileReader fr = new FileReader(file)) {
                read(GSON.fromJson(fr, JsonObject.class));
            } catch (Exception e) {
                ViaFabricPlus.global().getLogger().error("Failed to read file: " + file.getName() + "!");
            }
        }
    }

    /**
     * This method should be called when the file should be saved.
     */
    public void save() {
        try {
            file.delete();
            file.createNewFile();
        } catch (IOException e) {
            ViaFabricPlus.global().getLogger().error("Failed to create file: " + file.getName() + "!");
        }

        try (final FileWriter fw = new FileWriter(file)) {
            final JsonObject parentNode = new JsonObject();
            write(parentNode);
            fw.write(GSON.toJson(parentNode));
            fw.flush();
        } catch (IOException e) {
            ViaFabricPlus.global().getLogger().error("Failed to write file: " + file.getName() + "!");
        }
    }

    public abstract void write(final JsonObject object);
    public abstract void read(final JsonObject object);

    public File getFile() {
        return file;
    }

}
