/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.definition.v1_19_0;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class JsonHelper {

    public static String toSortedString(JsonElement json) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        try {
            writeSorted(jsonWriter, json, Comparator.naturalOrder());
        } catch (IOException var4) {
            throw new AssertionError(var4);
        }

        return stringWriter.toString();
    }

    public static void writeSorted(JsonWriter writer, @Nullable JsonElement json, @Nullable Comparator<String> comparator) throws IOException {
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
                if (jsonPrimitive.isNumber()) {
                    writer.value(jsonPrimitive.getAsNumber());
                } else if (jsonPrimitive.isBoolean()) {
                    writer.value(jsonPrimitive.getAsBoolean());
                } else {
                    writer.value(jsonPrimitive.getAsString());
                }
            } else {
                Iterator var5;
                if (json.isJsonArray()) {
                    writer.beginArray();
                    var5 = json.getAsJsonArray().iterator();

                    while(var5.hasNext()) {
                        JsonElement jsonElement = (JsonElement)var5.next();
                        writeSorted(writer, jsonElement, comparator);
                    }

                    writer.endArray();
                } else {
                    if (!json.isJsonObject()) {
                        throw new IllegalArgumentException("Couldn't write " + json.getClass());
                    }

                    writer.beginObject();
                    var5 = sort(json.getAsJsonObject().entrySet(), comparator).iterator();

                    while(var5.hasNext()) {
                        Map.Entry<String, JsonElement> entry = (Map.Entry)var5.next();
                        writer.name((String)entry.getKey());
                        writeSorted(writer, (JsonElement)entry.getValue(), comparator);
                    }

                    writer.endObject();
                }
            }
        } else {
            writer.nullValue();
        }
    }

    private static Collection<Map.Entry<String, JsonElement>> sort(Collection<Map.Entry<String, JsonElement>> entries, @Nullable Comparator<String> comparator) {
        if (comparator == null) {
            return entries;
        } else {
            List<Map.Entry<String, JsonElement>> list = new ArrayList(entries);
            list.sort(Map.Entry.comparingByKey(comparator));
            return list;
        }
    }
}
