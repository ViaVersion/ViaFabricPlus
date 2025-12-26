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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class BlockConnectionsEmulation1_12_2 {

    public static void updateChunkConnections(final BlockGetter blockGetter, final ChunkAccess chunkAccess) {
        if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) return;
        final ChunkPos chunkPos = chunkAccess.getPos();
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 256; ++y) {
                for (int z = 0; z < 16; ++z) {
                    blockPos.set(chunkPos.getBlockX(x), y, chunkPos.getBlockZ(z));
                    final BlockState blockState = chunkAccess.getBlockState(blockPos);
                    if (blockState.isAir()) continue;
                    chunkAccess.setBlockState(blockPos, connect(blockState, blockGetter, blockPos));
                }
            }
        }
    }

    // Following code adapted and sourced from 1.12.2 (Feather Mappings)
    private static BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        return blockState; // TODO
    }

}
