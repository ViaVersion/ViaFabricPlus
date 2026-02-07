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

package com.viaversion.viafabricplus.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.viaversion.viafabricplus.save.AbstractSave.GSON;

/**
 * Generates a file containing all collision shapes of static blocks. Can be used for game updates where Mojang refactors the collision shapes.
 * Please only use this when there are large changes where manual comparison is not feasible. Otherwise, manually checking the changes is recommended.
 */
public final class CollisionShapesTaskTest {

    //@Test
    void generate() {
        final File toCompare = new File("old_blocks_collision_shapes.json");

        // First step, generate the "before" file if it doesn't exist
        if (!toCompare.exists() || !toCompare.isFile()) {
            final Path path = new File("blocks_collision_shapes.json").toPath();
            try {
                Files.write(path, dumpData().toString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        // Compare current data against before file
        System.out.println("Starting comparison...");
        try {
            final JsonObject first = GSON.fromJson(Files.readString(toCompare.toPath()), JsonObject.class);
            final JsonObject second = GSON.fromJson(Files.readString(new File("blocks_collision_shapes.json").toPath()), JsonObject.class);
            compare(first, second);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printVoxelShape(final VoxelShape shape) {
//        if (shape.getBoundingBoxes().size() > 1) {
//            System.out.println("Multiple boxes in shape: " + shape);
//        }
        final AABB box = shape.bounds();
        System.out.println("Min: " + box.minX * 16 + ", " + box.minY * 16 + ", " + box.minZ * 16 + " Max: " + box.maxX * 16 + ", " + box.maxY * 16 + ", " + box.maxZ * 16);
    }

    private static StringBuilder dumpData() {
        final Set<Class<? extends Block>> skippedBlocks = new HashSet<>();
        final JsonObject data = new JsonObject();
        for (final Block block : BuiltInRegistries.BLOCK) {
            final JsonObject blockData = new JsonObject();
            for (final BlockState blockStates : block.getStateDefinition().getPossibleStates()) {
                final JsonObject blockStateData = new JsonObject();
                try {
                    final VoxelShape collisionShape = blockStates.getCollisionShape(null, null);
                    blockStateData.addProperty("collisionShape", collisionShape.toString());
                    final VoxelShape outlineShape = blockStates.getShape(null, null);
                    blockStateData.addProperty("outlineShape", outlineShape.toString());
                    final VoxelShape raycastShape = blockStates.getInteractionShape(null, null);
                    blockStateData.addProperty("raycastShape", raycastShape.toString());
                } catch (Exception e) {
                    skippedBlocks.add(block.getClass());
                    break;
                }
                blockData.add(blockStates.toString(), blockStateData);
            }

            data.add(BuiltInRegistries.BLOCK.getKey(block).toString(), blockData);
        }

        final JsonArray skippedBlocksData = new JsonArray();
        for (final Class<? extends Block> skippedBlock : skippedBlocks) {
            System.out.println("Skipped Block, check this one manually: " + skippedBlock.getSimpleName());
            skippedBlocksData.add(skippedBlock.getSimpleName());
        }
        data.add("skippedBlocks", skippedBlocksData);
        return new StringBuilder(data.toString());
    }

    private static void compare(final JsonObject first, final JsonObject second) {
        final JsonArray firstSkippedBlocks = first.getAsJsonArray("skippedBlocks");
        final JsonArray secondSkippedBlocks = second.getAsJsonArray("skippedBlocks");
        if (firstSkippedBlocks.size() != secondSkippedBlocks.size()) {
            System.out.println("The skipped blocks are different.");
            System.out.println("First: " + firstSkippedBlocks);
            System.out.println("Second: " + secondSkippedBlocks);
        } else {
            for (final JsonElement block : firstSkippedBlocks) {
                System.out.println("Skipped Block, check this one manually: " + block.getAsString());
            }
        }

        for (final Block block : BuiltInRegistries.BLOCK) {
            final JsonObject firstBlock = first.getAsJsonObject(BuiltInRegistries.BLOCK.getKey(block).toString());
            final JsonObject secondBlock = second.getAsJsonObject(BuiltInRegistries.BLOCK.getKey(block).toString());
            if (firstBlock == null) {
                // New block (or renamed), can be ignored
                //System.out.println("The block " + Registries.BLOCK.getId(block) + " is missing in the first file.");
                continue;
            }

            boolean missingBlockState = false;
            for (final BlockState blockState : block.getStateDefinition().getPossibleStates()) {
                final JsonObject firstBlockState = firstBlock.getAsJsonObject(blockState.toString());
                final JsonObject secondBlockState = secondBlock.getAsJsonObject(blockState.toString());
                if (firstBlockState == null || secondBlockState == null) {
                    // Can be ignored for comparing (usually)
                    //System.out.println("BlockState " + blockState + " is missing in one of the files.");
                    missingBlockState = true;
                    continue;
                }

                final String firstCollisionShape = firstBlockState.get("collisionShape").getAsString();
                final String secondCollisionShape = secondBlockState.get("collisionShape").getAsString();
                if (!firstCollisionShape.equals(secondCollisionShape)) {
                    System.out.println("Collision shape of " + blockState + " in " + BuiltInRegistries.BLOCK.getKey(block) + " is different.");
                    System.out.println("First: " + firstCollisionShape);
                    System.out.println("Second: " + secondCollisionShape);
                }

                final String firstOutlineShape = firstBlockState.get("outlineShape").getAsString();
                final String secondOutlineShape = secondBlockState.get("outlineShape").getAsString();
                if (!firstOutlineShape.equals(secondOutlineShape)) {
                    System.out.println("Outline shape of " + blockState + " in " + BuiltInRegistries.BLOCK.getKey(block) + " is different.");
                    System.out.println("First: " + firstOutlineShape);
                    System.out.println("Second: " + secondOutlineShape);
                }

                final String firstRaycastShape = firstBlockState.get("raycastShape").getAsString();
                final String secondRaycastShape = secondBlockState.get("raycastShape").getAsString();
                if (!firstRaycastShape.equals(secondRaycastShape)) {
                    System.out.println("Raycast shape of " + blockState + " in " + BuiltInRegistries.BLOCK.getKey(block) + " is different.");
                    System.out.println("First: " + firstRaycastShape);
                    System.out.println("Second: " + secondRaycastShape);
                }
            }
            if (missingBlockState && !firstSkippedBlocks.contains(new JsonPrimitive(block.getClass().getSimpleName()))) {
                System.out.println("The block " + BuiltInRegistries.BLOCK.getKey(block) + " is missing block states in one of the files.");
            }
        }
    }

}
