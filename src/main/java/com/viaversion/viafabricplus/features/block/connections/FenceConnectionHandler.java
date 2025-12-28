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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class FenceConnectionHandler implements IBlockConnectionHandler {

    @Override
    public BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        return blockState
            .setValue(FenceBlock.NORTH, connectsTo(blockGetter, blockPos.north(), Direction.NORTH))
            .setValue(FenceBlock.SOUTH, connectsTo(blockGetter, blockPos.south(), Direction.SOUTH))
            .setValue(FenceBlock.WEST, connectsTo(blockGetter, blockPos.west(), Direction.WEST))
            .setValue(FenceBlock.EAST, connectsTo(blockGetter, blockPos.east(), Direction.EAST));
    }

    // TODO: Fine-tune and make perfect/1:1
    private boolean connectsTo(final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState neighborState = blockGetter.getBlockState(blockPos);

        final Block block = neighborState.getBlock();
        if (block instanceof StairBlock) {
            // TODO: Sometimes isn't right
            return neighborState.getValue(StairBlock.FACING) == direction.getOpposite(); // Only connect to the backside of stairs
        }

        return !isExceptionForConnection(neighborState) && (block instanceof FenceBlock || block instanceof FenceGateBlock || block instanceof SlimeBlock || neighborState.isSolidRender());
    }

    private boolean isExceptionForConnection(final BlockState blockState) {
        return blockState.isAir()
            || Block1_14.isExceptBlockForAttachWithPiston(blockState.getBlock())
            || blockState.is(Blocks.MELON)
            || blockState.is(Blocks.PUMPKIN)
            || blockState.is(Blocks.CARVED_PUMPKIN)
            || blockState.is(Blocks.JACK_O_LANTERN)
            || blockState.is(Blocks.BARRIER);
    }

}
