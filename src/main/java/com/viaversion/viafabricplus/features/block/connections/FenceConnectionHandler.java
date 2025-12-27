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

import com.viaversion.viafabricplus.features.block.interaction.Block1_14;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;

// Code sourced and adapted from 1.12.2 (Feather)
public final class FenceConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final FenceBlock fenceBlock = (FenceBlock) blockState.getBlock();
        final boolean connectsSouth = shouldConnectTo(fenceBlock, levelReader, blockPos.north(), Direction.SOUTH);
        final boolean connectsWest = shouldConnectTo(fenceBlock, levelReader, blockPos.east(), Direction.WEST);
        final boolean connectsNorth = shouldConnectTo(fenceBlock, levelReader, blockPos.south(), Direction.NORTH);
        final boolean connectsEast = shouldConnectTo(fenceBlock, levelReader, blockPos.west(), Direction.EAST);
        return fenceBlock.defaultBlockState()
            .setValue(FenceBlock.NORTH, connectsSouth)
            .setValue(FenceBlock.EAST, connectsWest)
            .setValue(FenceBlock.SOUTH, connectsNorth)
            .setValue(FenceBlock.WEST, connectsEast);
    }

    private boolean shouldConnectTo(final FenceBlock fenceBlock, final LevelReader levelReader, final BlockPos blockPos, final Direction direction) {
        final BlockState blockState = levelReader.getBlockState(blockPos);
        final Block block = blockState.getBlock();
        final boolean same = /*middlePole && */ (fenceBlock.isSameFence(blockState) || blockState.is(BlockTags.FENCE_GATES));
        return !shouldConnectTo(block) && blockState.isSolidRender() || same;
    }

    private boolean shouldConnectTo(final Block block) {
        return Block1_14.isExceptBlockForAttachWithPiston(block)
            || block == Blocks.BARRIER
            || block == Blocks.MELON
            || block == Blocks.PUMPKIN
            || block == Blocks.CARVED_PUMPKIN;
    }
}
