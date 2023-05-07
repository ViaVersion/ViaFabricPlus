/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.base;

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.ViaFabricPlus;

import java.io.*;

public abstract class FileSaver {
    private final File file;

    public FileSaver(final String name) {
        file = new File(ViaFabricPlus.RUN_DIRECTORY, name);
    }

    public void init() {
        if (file.exists()) {
            JsonObject parentNode = null;
            try {
                parentNode = ViaFabricPlus.GSON.fromJson(new FileReader(file), JsonObject.class).getAsJsonObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (parentNode != null) {
                read(parentNode);
            }
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (final FileWriter fw = new FileWriter(file)) {
                final JsonObject parentNode = new JsonObject();
                write(parentNode);
                fw.write(ViaFabricPlus.GSON.toJson(parentNode));
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public abstract void write(final JsonObject object);
    public abstract void read(final JsonObject object);

    public File getFile() {
        return file;
    }
}
