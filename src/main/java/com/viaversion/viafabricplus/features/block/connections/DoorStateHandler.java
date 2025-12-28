/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

// TODO: Fix on place/powered (works fine/normal when joining or updating the block)
// Code sourced and adapted from 1.12.2 (Feather)
public final class DoorStateHandler implements IBlockStateHandler {

    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final boolean lowerHalf = blockState.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
        final BlockState halfState = levelReader.getBlockState(lowerHalf ? blockPos.above() : blockPos.below());
        if (!halfState.is(blockState.getBlock())) return blockState; // Not the same type of door, ignore

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

}
