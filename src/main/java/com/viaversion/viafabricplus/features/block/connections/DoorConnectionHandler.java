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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

// Code sourced and adapted from 1.12.2 (Feather)
public final class DoorConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        final DoorBlock doorBlock = (DoorBlock) blockState.getBlock();

        final boolean lowerHalf = blockState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
        final BlockState halfState = blockGetter.getBlockState(lowerHalf ? blockPos.above() : blockPos.below());
        if (halfState.getBlock() == doorBlock) {
            if (lowerHalf) {
                return blockState
                    .setValue(DoorBlock.HINGE, halfState.getValue(DoorBlock.HINGE))
                    .setValue(DoorBlock.POWERED, halfState.getValue(DoorBlock.POWERED));
            } else {
                return blockState
                    .setValue(DoorBlock.FACING, halfState.getValue(DoorBlock.FACING))
                    .setValue(DoorBlock.OPEN, halfState.getValue(DoorBlock.OPEN));
            }
        }

        return blockState;
    }
}
