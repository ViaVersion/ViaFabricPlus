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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

// Code sourced and adapted from 1.12.2 (Feather)
public final class FireStateHandler implements IBlockStateHandler {

    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        // TODO: Double check is `isSolidRender` is the same as `isFullBlock`
        final boolean canBurn = !levelReader.getBlockState(blockPos.below()).isSolidRender() && !((FireBlock) Blocks.FIRE).canBurn(levelReader.getBlockState(blockPos.below()));
        if (canBurn) {
            final FireBlock fireBlock = (FireBlock) blockState.getBlock();
            return blockState.setValue(FireBlock.NORTH, fireBlock.canBurn(levelReader.getBlockState(blockPos.north())))
                .setValue(FireBlock.EAST, fireBlock.canBurn(levelReader.getBlockState(blockPos.east())))
                .setValue(FireBlock.SOUTH, fireBlock.canBurn(levelReader.getBlockState(blockPos.south())))
                .setValue(FireBlock.WEST, fireBlock.canBurn(levelReader.getBlockState(blockPos.west())))
                .setValue(FireBlock.UP, fireBlock.canBurn(levelReader.getBlockState(blockPos.above())));
        }

        return blockState;
    }

}
