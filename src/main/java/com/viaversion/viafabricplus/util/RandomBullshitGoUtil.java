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

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.viafabricplus.injection.access.raphi.IResourcePackStorage;
import com.viaversion.viaversion.libs.gson.JsonObject;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.cube.converter.util.element.Direction;
import java.util.Map;

public class RandomBullshitGoUtil {
    public static ResourcePackStorage STORAGE;

    public static void putIfExist(final Direction direction, final JsonObject object, final Map<Direction, String> map) {
        String name = direction.name().toLowerCase();
        if (object.has(name)) {
            map.put(direction, object.getAsJsonPrimitive(name).getAsString());
        } else {
            map.put(direction, "empty");
        }
    }

    public static void putIfExist(final Direction direction, final CompoundTag tag, final Map<Direction, String> map) {
        String name = direction.name().toLowerCase();
        if (tag.contains(name)) {
            try {
                String result = ((IResourcePackStorage)STORAGE).viaFabricPlus$texturesPathFromId(tag.getCompoundTag(name).getString("texture"));
                map.put(direction, result);
            } catch (Exception ignored) {
                map.put(direction, "empty");
            }
        } else {
            map.put(direction, "empty");
        }
    }
}
