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
package de.florianmichael.viafabricplus.mappings;

import com.google.gson.*;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterMappings {
    public static Map<String, Map<String, List<Integer>>> forbiddenCharacters1_19_4To1_20 = new HashMap<>();

    public static void load() {
        final JsonObject file = ViaFabricPlus.GSON.fromJson(new InputStreamReader(CharacterMappings.class.getResourceAsStream("/assets/viafabricplus/characters1_19_4To1_20.json")), JsonObject.class);
        for (Map.Entry<String, JsonElement> layer1 : file.entrySet()) { // missing / uniform / bitmap
            final Map<String, List<Integer>> typeStorage = new HashMap<>();
            for (Map.Entry<String, JsonElement> layer2 : layer1.getValue().getAsJsonObject().entrySet()) { // blank / space / unicode
                final List<Integer> type2Storage = new ArrayList<>();
                for (JsonElement element : layer2.getValue().getAsJsonArray()) { // data
                    type2Storage.add(element.getAsInt());
                }
                typeStorage.put(layer2.getKey(), type2Storage);
            }
            forbiddenCharacters1_19_4To1_20.put(layer1.getKey(), typeStorage);
        }
    }

    public static Map<String, List<Integer>> getForbiddenCharactersForID(final Identifier id) {
        if (!forbiddenCharacters1_19_4To1_20.containsKey(id.toString())) return new HashMap<>();
        return forbiddenCharacters1_19_4To1_20.get(id.toString());
    }
}
