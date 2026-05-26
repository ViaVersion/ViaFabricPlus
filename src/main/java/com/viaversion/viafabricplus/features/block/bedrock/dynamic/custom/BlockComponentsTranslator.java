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

package com.viaversion.viafabricplus.features.block.bedrock.dynamic.custom;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.FloatTag;
import com.viaversion.nbt.tag.ListTag;
import com.viaversion.nbt.tag.NumberTag;
import com.viaversion.nbt.tag.Tag;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.DynamicBlockCache;
import com.viaversion.viafabricplus.injection.access.bedrock.pack.IModelDefinitions;
import com.viaversion.viafabricplus.injection.access.bedrock.pack.IResourcePackStorage;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.cube.converter.util.element.Direction;
import org.cube.converter.util.element.Position3V;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for translating bedrock block components to java Block.
 */
public class BlockComponentsTranslator {
    public static Result parseBlockComponents(Result.Builder builder, String identifier, CompoundTag components) {
        if (!components.contains("minecraft:collision_box") && builder.collision == null || components.contains("minecraft:collision_box")) {
            builder.collision(parseTagToVoxelShape(components.get("minecraft:collision_box")));
        }
        if (!components.contains("minecraft:selection_box") && builder.selection == null || components.contains("minecraft:selection_box")) {
            builder.selection(parseTagToVoxelShape(components.get("minecraft:selection_box")));
        }

        if (components.contains("minecraft:friction")) {
            Tag tag = components.get("minecraft:friction");
            if (tag instanceof CompoundTag frictionTag && frictionTag.contains("value")) {
                builder.friction(Math.max(0, 1 - frictionTag.getFloat("value")));
            }
        } else {
            builder.friction(builder.friction == 0 ? 0.6f : 0f);
        }

        if (components.contains("minecraft:destructible_by_mining")) {
            Tag tag = components.get("minecraft:destructible_by_mining");
            if (tag instanceof NumberTag numberTag) {
                builder.destroyTime(numberTag.asBoolean() ? 0 : -1);
            } else if (tag instanceof CompoundTag compoundTag && compoundTag.contains("value")) {
                builder.destroyTime(compoundTag.getFloat("value"));
            }
        }

        if (components.contains("minecraft:destructible_by_mining")) {
            Tag tag = components.get("minecraft:destructible_by_mining");
            if (tag instanceof NumberTag numberTag) {
                builder.destroyTime(numberTag.asBoolean() ? 0 : -1);
            } else if (tag instanceof CompoundTag compoundTag && compoundTag.contains("value")) {
                builder.destroyTime(compoundTag.getFloat("value"));
            }
        }

        if (components.contains("minecraft:light_emission")) {
            Tag tag = components.get("minecraft:light_emission");
            if (tag instanceof CompoundTag lightTag && lightTag.contains("emission")) {
                builder.lightEmission(lightTag.getByte("emission"));
            }
        }

        // There's 2 way for a block to define its texture, first is through blocks.json in the texture pack which is what we're pulling here.
        Map<Direction, String> textures = ((IResourcePackStorage) DynamicBlockCache.STORAGE_INSTANCE).viaFabricPlus$textures(identifier);

        // Second is by sending the client a component called minecraft:material_instances with the texture path.
        // From debugging, it seems like minecraft:material_instances has to be priority over blocks.json if they overlap each other.
        CompoundTag materialInstance = components.getCompoundTag("minecraft:material_instances");
        if (materialInstance != null) {
            CompoundTag materials = materialInstance.getCompoundTag("materials");

            // Each faces can have different texture, so we have to map them like this (https://wiki.bedrock.dev/blocks/block-visuals-intro#material-instances)
            if (materials != null) {
                textures = new HashMap<>();
                if (materials.contains("*")) {
                    CompoundTag tag = materials.getCompoundTag("*");
                    if (tag == null) {
                        tag = materials.getCompoundTag(materials.getString("*"));
                    }

                    if (tag != null) {
                        final String textureId = ((IResourcePackStorage)DynamicBlockCache.STORAGE_INSTANCE).viaFabricPlus$texturesPathFromId(tag.getString("texture"));
                        for (Direction direction : Direction.values()) {
                            textures.put(direction, textureId);
                        }
                    }
                }

                for (Direction direction : Direction.values()) {
                    putIfExist(direction, materials, textures);
                }
            }
        }
        builder.textures(textures);

        if (components.contains("minecraft:geometry")) {
            final String geometryId = components.getCompoundTag("minecraft:geometry").getString("identifier");
            builder.model(((IModelDefinitions)DynamicBlockCache.STORAGE_INSTANCE.getModels()).viaFabricPlus$getBlockModel(geometryId));
        } else {
            // If there's no geometry then fall back to the default model.
            if (builder.model == null) {
                builder.model(((IModelDefinitions)DynamicBlockCache.STORAGE_INSTANCE.getModels()).viaFabricPlus$getBlockModel("minecraft:geometry.full_block"));
            }
        }

        // https://wiki.bedrock.dev/blocks/block-components#transformation, TODO: for now scale_pivot is ignored cuz im not sure how it should be implemented.
        if (components.contains("minecraft:transformation")) {
            final CompoundTag tag = components.getCompoundTag("minecraft:transformation");
            Position3V translation = readTransformTag(tag.getListTag("translation", FloatTag.class), builder.transformation.translation());
            Position3V rotation = readTransformTag(tag.getListTag("rotation", FloatTag.class), builder.transformation.rotation());
            Position3V pivot = readTransformTag(tag.getListTag("rotation_pivot", FloatTag.class), builder.transformation.pivot());
            Position3V scale = readTransformTag(tag.getListTag("scale", FloatTag.class), builder.transformation.scale());

            builder.transformation(new DynamicBlockCache.Transformation(translation, rotation, pivot, scale));
        }

        return builder.build();
    }

    private static Position3V readTransformTag(ListTag<FloatTag> tag, Position3V defaultValue) {
        return tag == null ? defaultValue : new Position3V(tag.get(0).asFloat(), tag.get(1).asFloat(), tag.get(2).asFloat());
    }

    private static void putIfExist(final Direction direction, CompoundTag tag, final Map<Direction, String> map) {
        final String name = direction.name().toLowerCase();
        if (tag.contains(name)) {
            CompoundTag compoundTag = tag.getCompoundTag(name);
            try {
                if (compoundTag == null) {
                    compoundTag = tag.getCompoundTag(tag.getString(name));
                }

                String result = ((IResourcePackStorage)DynamicBlockCache.STORAGE_INSTANCE).viaFabricPlus$texturesPathFromId(compoundTag.getString("texture"));
                map.put(direction, result);
            } catch (Exception ignored){
                map.putIfAbsent(direction, "empty");
            }
        } else {
            map.putIfAbsent(direction, "empty");
        }
    }

    private static VoxelShape parseTagToVoxelShape(Tag tag) {
        // If there are no component (or it's a weird one), then it will default to the default value (full block shape).
        if (!(tag instanceof NumberTag) && !(tag instanceof CompoundTag)) {
            return Shapes.block();
        }

        if (tag instanceof NumberTag numberTag) {
            return numberTag.asBoolean() ? Shapes.block() : Shapes.empty();
        }

        final CompoundTag compoundTag = (CompoundTag) tag;
        if (!compoundTag.getBoolean("enabled")) {
            return Shapes.empty();
        }

        if (compoundTag.contains("boxes")) {
            VoxelShape finalShape = Shapes.empty();
            for (CompoundTag box : compoundTag.getListTag("boxes", CompoundTag.class)) {
                finalShape = Shapes.join(finalShape, parseNewBoxFormatToVoxelShape(box), BooleanOp.OR);
            }

            return finalShape;
        }

        return parseOldBoxFormatToVoxelShape(compoundTag);
    }

    private static VoxelShape parseNewBoxFormatToVoxelShape(CompoundTag box) {
        return Shapes.box(
            box.getFloat("minX") / 16f,
            box.getFloat("minY") / 16f,
            box.getFloat("minZ") / 16f,
            box.getFloat("maxX") / 16f,
            box.getFloat("maxY") / 16f,
            box.getFloat("maxZ") / 16f
        );
    }

    private static VoxelShape parseOldBoxFormatToVoxelShape(CompoundTag box) {
        ListTag<FloatTag> origin = box.getListTag("origin", FloatTag.class);
        ListTag<FloatTag> size = box.getListTag("size", FloatTag.class);
        if (origin == null || size == null) {
            return Shapes.empty();
        }

        float minX = (origin.get(0).asFloat() + 8.0F) / 16.0F;
        float minY = origin.get(1).asFloat() / 16.0F;
        float minZ = (origin.get(2).asFloat() + 8.0F) / 16.0F;
        float maxX = minX + size.get(0).asFloat() / 16.0F;
        float maxY = minY + size.get(1).asFloat() / 16.0F;
        float maxZ = minZ + size.get(2).asFloat() / 16.0F;
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public record Result(VoxelShape collision,
                         VoxelShape selection, float friction, float destroyTime,
                         int lightEmission, DynamicBlockCache.Transformation transformation,
                         Map<Direction, String> textures, BedrockGeometryModel model) {
        public Result.Builder toBuilder() {
            final Result.Builder builder = new Builder();
            builder.collision(collision);
            builder.selection(selection);
            builder.friction(friction);
            builder.destroyTime(destroyTime);
            builder.textures(textures);
            builder.model(model);
            return builder;
        }

        public static class Builder {
            private VoxelShape collision, selection;
            private float friction, destroyTime;

            private int lightEmission;
            private DynamicBlockCache.Transformation transformation = new DynamicBlockCache.Transformation(Position3V.zero(), Position3V.zero(), Position3V.zero(), new Position3V(1, 1, 1));

            private Map<Direction, String> textures;
            private BedrockGeometryModel model;

            public void collision(VoxelShape collision) {
                this.collision = collision;
            }

            public void selection(VoxelShape selection) {
                this.selection = selection;
            }

            public void friction(float friction) {
                this.friction = friction;
            }

            public void destroyTime(float destroyTime) {
                this.destroyTime = destroyTime;
            }

            public void textures(Map<Direction, String> textures) {
                this.textures = textures;
            }

            public void model(BedrockGeometryModel model) {
                this.model = model;
            }

            public void lightEmission(int lightEmission) {
                this.lightEmission = lightEmission;
            }

            public void transformation(DynamicBlockCache.Transformation transformation) {
                this.transformation = transformation;
            }

            public Result build() {
                return new Result(collision, selection, friction, destroyTime, lightEmission, transformation, textures, model);
            }
        }
    }
}
