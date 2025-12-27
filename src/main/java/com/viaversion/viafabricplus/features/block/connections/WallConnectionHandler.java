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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        return blockState
            .setValue(WallBlock.UP, hasUp(levelReader, blockPos, north, south, west, east))
            .setValue(WallBlock.NORTH, getWallSide(north))
            .setValue(WallBlock.SOUTH, getWallSide(south))
            .setValue(WallBlock.WEST, getWallSide(west))
            .setValue(WallBlock.EAST, getWallSide(east));
    }

    // TODO: Fine-tune and make perfect/1:1
    private boolean connectsTo(final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState neighbor = blockGetter.getBlockState(blockPos);

        final Block block = neighbor.getBlock();
        if (block instanceof StairBlock) {
            return neighbor.getValue(StairBlock.FACING) == direction.getOpposite(); // Only connect to the backside of stairs
        }

        return !neighbor.isAir() && !isExceptionForConnection(block) && (block instanceof WallBlock || neighbor.isSolidRender());
    }

    private boolean isExceptionForConnection(Block block) {
        return Block1_14.isExceptBlockForAttachWithPiston(block)
            || block == Blocks.BARRIER
            || block == Blocks.PUMPKIN
            || block == Blocks.CARVED_PUMPKIN
            || block == Blocks.JACK_O_LANTERN;
    }

    // TODO: Fine-tune and make perfect/1:1
    private boolean hasUp(final LevelReader levelReader, final BlockPos blockPos, final boolean north, final boolean south, final boolean west, final boolean east) {
        final BlockState aboveState = levelReader.getBlockState(blockPos.above());

        int sides = 0;
        if (north) sides++;
        if (south) sides++;
        if (west) sides++;
        if (east) sides++;

        final boolean isLShape = (north && east && !south && !west)
            || (north && west && !south && !east)
            || (south && east && !north && !west)
            || (south && west && !north && !east);

        return aboveState.isFaceSturdy(levelReader, blockPos.above(), Direction.DOWN) || isLShape || sides == 0 || sides == 1 || sides == 4;
    }

    private WallSide getWallSide(final boolean value) {
        return value ? WallSide.LOW : WallSide.NONE;
    }
}
