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
import com.viaversion.nbt.tag.FloatTag;
import com.viaversion.nbt.tag.ListTag;
import com.viaversion.nbt.tag.NumberTag;
import com.viaversion.nbt.tag.Tag;
import com.viaversion.viafabricplus.injection.access.raphi.IResourcePackStorage;
import com.viaversion.viaversion.libs.gson.JsonObject;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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

    public static VoxelShape tagToVoxelShape(Tag boxTag) {
        if (boxTag == null) {
            return Shapes.block(); // This is correct yes.
        }

        if (boxTag instanceof NumberTag tag) {
            return tag.asBoolean() ? Shapes.block() : Shapes.empty();
        }
        if (!(boxTag instanceof CompoundTag box)) {
            return Shapes.empty();
        }

        if (!box.getBoolean("enabled")) {
            return Shapes.empty();
        }

        if (box.contains("boxes")) {
            VoxelShape finalShape = Shapes.empty();
            for (CompoundTag tag : box.getListTag("boxes", CompoundTag.class)) {
                finalShape = Shapes.join(finalShape, newBoxToComponent(tag), BooleanOp.OR);
            }

            return finalShape;
        }

        return box.contains("origin") && box.contains("size") ? oldBoxToComponent(box) : Shapes.empty();
    }

    private static VoxelShape newBoxToComponent(CompoundTag box) {
        float minX = box.getFloat("minX") / 16.0F;
        float minY = box.getFloat("minY") / 16.0F;
        float minZ = box.getFloat("minZ") / 16.0F;
        float maxX = box.getFloat("maxX") / 16.0F;
        float maxY = box.getFloat("maxY") / 16.0F;
        float maxZ = box.getFloat("maxZ") / 16.0F;
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static VoxelShape oldBoxToComponent(CompoundTag box) {
        ListTag<FloatTag> origin = (ListTag<FloatTag>) box.getNumberListTag("origin");
        ListTag<FloatTag> size = (ListTag<FloatTag>) box.getNumberListTag("size");
        float minX = (origin.get(0).getValue() + 8.0F) / 16.0F;
        float minY = origin.get(1).getValue() / 16.0F;
        float minZ = (origin.get(2).getValue() + 8.0F) / 16.0F;
        float maxX = minX + size.get(0).getValue() / 16.0F;
        float maxY = minY + size.get(1).getValue() / 16.0F;
        float maxZ = minZ + size.get(2).getValue() / 16.0F;
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
