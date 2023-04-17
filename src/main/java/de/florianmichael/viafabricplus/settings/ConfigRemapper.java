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
package de.florianmichael.viafabricplus.settings;

@Deprecated
public class ConfigRemapper {

    /**
     * Remaps the old config format to the new one, will be removed in the future
     */
    public static String remapp(String key) {
        if (key.equals("bedrock.viafabricplus.set")) key = "authentication.viafabricplus.bedrock";
        if (key.startsWith("mppass")) key = key.replace("mppass", "authentication");
        if (key.equals("visual.viafabricplus.chunkborderfix")) key = "experimental.viafabricplus.chunkborderfix";
        if (key.startsWith("bridge")) key = key.replace("bridge", "general");

        return key;
    }
}
