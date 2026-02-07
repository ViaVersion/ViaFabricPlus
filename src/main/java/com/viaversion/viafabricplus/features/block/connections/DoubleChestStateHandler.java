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

package com.viaversion.viafabricplus.features.block.connections;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public final class DoubleChestStateHandler implements IBlockStateHandler {

    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        if (!blockState.is(Blocks.ENDER_CHEST)) { // Ignore Ender-chests
            return blockState.setValue(ChestBlock.TYPE, getChestType(blockState, levelReader, blockPos));
        } else {
            return blockState;
        }
    }

    private ChestType getChestType(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        final Direction facing = blockState.getValue(ChestBlock.FACING);
        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            final BlockState neighborState = blockGetter.getBlockState(blockPos.relative(direction));
            if (neighborState.is(blockState.getBlock()) && neighborState.getValue(ChestBlock.FACING).equals(facing)) {
                if (direction == facing.getClockWise()) {
                    return ChestType.LEFT;
                } else if (direction == facing.getCounterClockWise()) {
                    return ChestType.RIGHT;
                }
            }
        }

        return ChestType.SINGLE;
    }

}
