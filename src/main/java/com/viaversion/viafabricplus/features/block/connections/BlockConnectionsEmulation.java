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

package com.viaversion.viafabricplus.features.block.connections;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.jspecify.annotations.Nullable;

/**
 * TODO/FIX:
 *  visual artifacts (collision/hitbox is correct, but visually the connection is wrong)
 *  inaccuracies if any (bedrock most likely)
 */
public final class BlockConnectionsEmulation {

    private static final int UPDATE_FLAGS = 18;
    private static final Object2ObjectOpenHashMap<Class<? extends Block>, IBlockConnectionHandler> connectionHandlers = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Class<? extends Block>, IBlockConnectionHandler> lookupCache = new Object2ObjectOpenHashMap<>();

    public static void init() {
        connectionHandlers.put(SnowyDirtBlock.class, new SnowyGrassConnectionHandler());
        connectionHandlers.put(FireBlock.class, new FireConnectionHandler());
        connectionHandlers.put(StairBlock.class, new StairsConnectionHandler());
        connectionHandlers.put(IronBarsBlock.class, new BarsConnectionHandler());
        connectionHandlers.put(RedStoneWireBlock.class, new RedStoneConnectionHandler());
        connectionHandlers.put(FenceBlock.class, new FenceConnectionHandler());
        connectionHandlers.put(WallBlock.class, new WallConnectionHandler());
        connectionHandlers.put(DoorBlock.class, new DoorConnectionHandler());
        connectionHandlers.put(ChestBlock.class, new DoubleChestConnectionHandler());
        connectionHandlers.put(PipeBlock.class, new PipeConnectionHandler());
        // TODO: FenceGateBlock (WALL | fence gate | WALL)
    }

    public static boolean isApplicable() {
        return ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest);
    }

    public static void updateChunkConnections(final LevelReader levelReader, final int chunkX, final int chunkZ) {
        if (!isApplicable() || !levelReader.hasChunk(chunkX, chunkZ)) return;
        final ChunkAccess chunkAccess = levelReader.getChunk(chunkX, chunkZ);
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 16; ++x) {
            final boolean insideX = x >= 0 && x < 16;
            for (int sectionY = chunkAccess.getMinSectionY(); sectionY < chunkAccess.getMaxSectionY(); sectionY++) {
                final LevelChunkSection section = chunkAccess.getSection(sectionY);
                if (section.hasOnlyAir()) continue;
                final int baseY = SectionPos.sectionToBlockCoord(sectionY);
                for (int y = baseY; y < baseY + 16; y++) {
                    for (int z = -1; z <= 16; ++z) {
                        blockPos.set(SectionPos.sectionToBlockCoord(chunkX) + x, y, SectionPos.sectionToBlockCoord(chunkZ) + z);
                        if (!levelReader.hasChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()))) {
                            continue;
                        }

                        final BlockState blockState = levelReader.getBlockState(blockPos);
                        if (blockState.isAir()) continue;

                        final IBlockConnectionHandler connectionHandler = getConnectionHandler(blockState.getBlock().getClass());
                        if (connectionHandler == null) continue;

                        final BlockState newState = connectionHandler.connect(blockState, levelReader, blockPos);
                        if (newState != blockState && insideX && z >= 0 && z < 16) {
                            chunkAccess.setBlockState(blockPos, newState, UPDATE_FLAGS);
                        }
                    }
                }
            }
        }
    }

    public static void updateChunkNeighborConnections(final LevelReader levelReader, final int chunkX, final int chunkZ) {
        updateChunkConnections(levelReader, chunkX, chunkZ);
        updateChunkConnections(levelReader, chunkX + 1, chunkZ);
        updateChunkConnections(levelReader, chunkX - 1, chunkZ);
        updateChunkConnections(levelReader, chunkX, chunkZ + 1);
        updateChunkConnections(levelReader, chunkX, chunkZ - 1);
    }

    public static void updateChunkNeighborConnections(final LevelReader levelReader, final BlockPos blockPos) {
        updateChunkNeighborConnections(levelReader, SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()));
    }

    private static @Nullable IBlockConnectionHandler getConnectionHandler(final Class<? extends Block> blockClass) {
        return lookupCache.computeIfAbsent(blockClass, clazz -> {
            Class<?> current = (Class<?>) clazz;
            while (current != Block.class && current != null) {
                final IBlockConnectionHandler handler = connectionHandlers.get(current);
                if (handler != null) {
                    return handler;
                }

                current = current.getSuperclass();
            }

            return null;
        });
    }

}
