/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.generator.blocks;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.viaversion.viafabricplus.generator.util.Generator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates a file containing all collision shapes of static blocks. Can be used for game updates where Mojang refactors the collision shapes.
 * Please only use this when there are large changes where manual comparison is not feasible. Otherwise, manually checking the changes is recommended.
 */
public final class GenerateCollisionShapes implements Generator {

    @Test
    void generate() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();

        final File toCompare = new File("old_blocks_collision_shapes.json");
        if (toCompare.exists() && toCompare.isFile()) {
            System.out.println("Starting comparison...");
            final Gson gson = new Gson();
            final JsonObject first = gson.fromJson(readFromFile(toCompare), JsonObject.class);
            final JsonObject second = gson.fromJson(readFromFile(new File("blocks_collision_shapes.json")), JsonObject.class);
            compare(first, second);
        } else {
            new GenerateCollisionShapes().writeToFile(null, new File(""), "blocks_collision_shapes.json");
        }
    }

    private void printVoxelShape(final VoxelShape shape) {
//        if (shape.getBoundingBoxes().size() > 1) {
//            System.out.println("Multiple boxes in shape: " + shape);
//        }
        final Box box = shape.getBoundingBox();
        System.out.println("Min: " + box.minX * 16 + ", " + box.minY * 16 + ", " + box.minZ * 16 + " Max: " + box.maxX * 16 + ", " + box.maxY * 16 + ", " + box.maxZ * 16);
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

        for (final Block block : Registries.BLOCK) {
            final JsonObject firstBlock = first.getAsJsonObject(Registries.BLOCK.getId(block).toString());
            final JsonObject secondBlock = second.getAsJsonObject(Registries.BLOCK.getId(block).toString());
            if (firstBlock == null) {
                // New block (or renamed), can be ignored
                //System.out.println("The block " + Registries.BLOCK.getId(block) + " is missing in the first file.");
                continue;
            }

            boolean missingBlockState = false;
            for (final BlockState blockState : block.getStateManager().getStates()) {
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
                    System.out.println("Collision shape of " + blockState + " in " + Registries.BLOCK.getId(block) + " is different.");
                    System.out.println("First: " + firstCollisionShape);
                    System.out.println("Second: " + secondCollisionShape);
                }

                final String firstOutlineShape = firstBlockState.get("outlineShape").getAsString();
                final String secondOutlineShape = secondBlockState.get("outlineShape").getAsString();
                if (!firstOutlineShape.equals(secondOutlineShape)) {
                    System.out.println("Outline shape of " + blockState + " in " + Registries.BLOCK.getId(block) + " is different.");
                    System.out.println("First: " + firstOutlineShape);
                    System.out.println("Second: " + secondOutlineShape);
                }

                final String firstRaycastShape = firstBlockState.get("raycastShape").getAsString();
                final String secondRaycastShape = secondBlockState.get("raycastShape").getAsString();
                if (!firstRaycastShape.equals(secondRaycastShape)) {
                    System.out.println("Raycast shape of " + blockState + " in " + Registries.BLOCK.getId(block) + " is different.");
                    System.out.println("First: " + firstRaycastShape);
                    System.out.println("Second: " + secondRaycastShape);
                }
            }
            if (missingBlockState && !firstSkippedBlocks.contains(new JsonPrimitive(block.getClass().getSimpleName()))) {
                System.out.println("The block " + Registries.BLOCK.getId(block) + " is missing block states in one of the files.");
            }
        }
    }

    @Override
    public StringBuilder generate(final ProtocolVersion nativeVersion) {
        final Set<Class<? extends Block>> skippedBlocks = new HashSet<>();
        final JsonObject data = new JsonObject();
        for (final Block block : Registries.BLOCK) {
            final JsonObject blockData = new JsonObject();
            for (final BlockState blockStates : block.getStateManager().getStates()) {
                final JsonObject blockStateData = new JsonObject();
                try {
                    final VoxelShape collisionShape = blockStates.getCollisionShape(null, null);
                    blockStateData.addProperty("collisionShape", collisionShape.toString());
                    final VoxelShape outlineShape = blockStates.getOutlineShape(null, null);
                    blockStateData.addProperty("outlineShape", outlineShape.toString());
                    final VoxelShape raycastShape = blockStates.getRaycastShape(null, null);
                    blockStateData.addProperty("raycastShape", raycastShape.toString());
                } catch (Exception e) {
                    skippedBlocks.add(block.getClass());
                    break;
                }
                blockData.add(blockStates.toString(), blockStateData);
            }

            data.add(Registries.BLOCK.getId(block).toString(), blockData);
        }

        final JsonArray skippedBlocksData = new JsonArray();
        for (final Class<? extends Block> skippedBlock : skippedBlocks) {
            System.out.println("Skipped Block, check this one manually: " + skippedBlock.getSimpleName());
            skippedBlocksData.add(skippedBlock.getSimpleName());
        }
        data.add("skippedBlocks", skippedBlocksData);
        return new StringBuilder(data.toString());
    }

}
