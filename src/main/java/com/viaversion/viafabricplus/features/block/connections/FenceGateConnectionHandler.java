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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;

// Code sourced and adapted from 1.12.2 (Feather)
public final class FenceGateConnectionHandler implements IBlockConnectionHandler {

    @Override
    public BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        final Direction.Axis axis = blockState.getValue(FenceGateBlock.FACING).getAxis();
        final BlockState westState = blockGetter.getBlockState(blockPos.west());
        final BlockState eastState = blockGetter.getBlockState(blockPos.east());
        final BlockState northState = blockGetter.getBlockState(blockPos.north());
        final BlockState southState = blockGetter.getBlockState(blockPos.south());
        if (axis == Direction.Axis.Z && (westState.is(Blocks.COBBLESTONE_WALL) || eastState.is(Blocks.COBBLESTONE_WALL))
            || axis == Direction.Axis.X && (northState.is(Blocks.COBBLESTONE_WALL) || southState.is(Blocks.COBBLESTONE_WALL))) {
            return blockState.setValue(FenceGateBlock.IN_WALL, true);
        } else {
            return blockState;
        }
    }

}
