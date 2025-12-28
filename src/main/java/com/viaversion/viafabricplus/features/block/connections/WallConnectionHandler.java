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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;

public final class WallConnectionHandler implements IBlockConnectionHandler {

    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final boolean north = connectsTo(levelReader, blockPos.north(), Direction.NORTH);
        final boolean south = connectsTo(levelReader, blockPos.south(), Direction.SOUTH);
        final boolean west = connectsTo(levelReader, blockPos.west(), Direction.WEST);
        final boolean east = connectsTo(levelReader, blockPos.east(), Direction.EAST);
        final boolean up = !(south && !west && north && !east || !south && west && !north && east) || !levelReader.getBlockState(blockPos.above()).isAir();
        return blockState
            .setValue(WallBlock.UP, up)
            .setValue(WallBlock.NORTH, getWallSide(north))
            .setValue(WallBlock.SOUTH, getWallSide(south))
            .setValue(WallBlock.WEST, getWallSide(west))
            .setValue(WallBlock.EAST, getWallSide(east));
    }

    // TODO: Fine-tune and make perfect/1:1
    private boolean connectsTo(final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState neighborState = blockGetter.getBlockState(blockPos);

        final Block block = neighborState.getBlock();
        if (block instanceof StairBlock) {
            // TODO: Sometimes isn't right
            return neighborState.getValue(StairBlock.FACING) == direction.getOpposite(); // Only connect to the backside of stairs
        }

        return !isExceptionForConnection(neighborState) && (block instanceof WallBlock || block instanceof FenceGateBlock || block instanceof SlimeBlock || neighborState.isSolidRender());
    }

    private WallSide getWallSide(final boolean value) {
        return value ? WallSide.LOW : WallSide.NONE;
    }

}
