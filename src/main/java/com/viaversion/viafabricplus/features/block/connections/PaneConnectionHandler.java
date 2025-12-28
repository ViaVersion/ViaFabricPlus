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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class PaneConnectionHandler implements IBlockConnectionHandler {

    @Override
    public BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        return blockState
            .setValue(IronBarsBlock.NORTH, connectsTo(blockGetter, blockPos.north(), Direction.NORTH))
            .setValue(IronBarsBlock.SOUTH, connectsTo(blockGetter, blockPos.south(), Direction.SOUTH))
            .setValue(IronBarsBlock.WEST, connectsTo(blockGetter, blockPos.west(), Direction.WEST))
            .setValue(IronBarsBlock.EAST, connectsTo(blockGetter, blockPos.east(), Direction.EAST));
    }

    private boolean connectsTo(final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState neighborState = blockGetter.getBlockState(blockPos);

        final Block block = neighborState.getBlock();
        if (block instanceof StairBlock) {
            // TODO: Sometimes isn't right
            return neighborState.getValue(StairBlock.FACING) == direction.getOpposite(); // Only connect to the backside of stairs
        }

        return !isExceptionForConnection(neighborState) && (neighborState.getBlock() instanceof IronBarsBlock || neighborState.isSolidRender());
    }

}
