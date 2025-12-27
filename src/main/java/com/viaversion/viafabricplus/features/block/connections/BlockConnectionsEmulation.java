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
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.jspecify.annotations.Nullable;

public final class BlockConnectionsEmulation {

    private static final Map<Class<? extends Block>, IBlockConnectionHandler> connectionHandlers = new HashMap<>();
    private static final Map<Class<? extends Block>, IBlockConnectionHandler> lookupCache = new HashMap<>();

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
        connectionHandlers.put(ChorusPlantBlock.class, new ChorusPlantConnectionHandler());
    }

    public static boolean isApplicable() {
        return ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest);
    }

    public static void updateChunkConnections(final LevelReader levelReader, final ChunkAccess chunkAccess) {
        if (!isApplicable()) return;

        final int minY = chunkAccess.getMinY();
        final int maxY = chunkAccess.getMaxY();
        if (chunkAccess.isYSpaceEmpty(minY, maxY)) return;

        final ChunkPos chunkPos = chunkAccess.getPos();
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = 0; z < 16; ++z) {
                    blockPos.set(chunkPos.getBlockX(x), y, chunkPos.getBlockZ(z));

                    final BlockState blockState = chunkAccess.getBlockState(blockPos);
                    if (blockState.isAir()) continue;

                    final BlockState newState = connect(blockState, levelReader, blockPos);
                    if (newState != null) {
                        chunkAccess.setBlockState(blockPos, newState, 18);
                    }
                }
            }
        }
    }

    public static void updateChunkConnections(final LevelReader levelReader, final int chunkX, final int chunkZ) {
        updateChunkConnections(levelReader, levelReader.getChunk(chunkX, chunkZ));
    }

    public static void updateChunkConnections(final LevelReader levelReader, final BlockPos blockPos) {
        updateChunkConnections(levelReader, SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getZ()));
    }

    public static @Nullable BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        if (!isApplicable()) return null;

        final IBlockConnectionHandler connectionHandler = getConnectionHandler(blockState.getBlock().getClass());
        if (connectionHandler == null) return null;

        final BlockState newState = connectionHandler.connect(blockState, blockGetter, blockPos);
        return newState != blockState ? newState : null;
    }

    private static @Nullable IBlockConnectionHandler getConnectionHandler(final Class<? extends Block> blockClass) {
        return lookupCache.computeIfAbsent(blockClass, clazz -> {
            for (final Map.Entry<Class<? extends Block>, IBlockConnectionHandler> entry : connectionHandlers.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    return entry.getValue();
                }
            }

            return null;
        });
    }
}
