/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for maps.
 */
public class MapUtil {

    /**
     * Creates a new linked hash map with the given objects as key-value pairs.
     *
     * @param objects The objects to put into the map. Must be a multiple of 2.
     * @param <K>     The key type.
     * @param <V>     The value type.
     * @return The created map.
     */
    public static <K, V> Map<K, V> linkedHashMap(final Object... objects) {
        if (objects.length % 2 != 0) {
            throw new IllegalArgumentException("Uneven object count");
        }

        final Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < objects.length; i += 2) {
            map.put((K) objects[i], (V) objects[i + 1]);
        }
        return map;
    }

}
