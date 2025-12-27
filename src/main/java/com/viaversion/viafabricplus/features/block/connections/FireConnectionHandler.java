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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

// Code sourced and adapted from 1.12.2 (Feather)
public final class FireConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        final FireBlock fireBlock = (FireBlock) blockState.getBlock();

        // TODO: Double check is `isSolidRender` is the same as `isFullBlock`
        final boolean canBurn = !blockGetter.getBlockState(blockPos.below()).isSolidRender() && !((FireBlock) Blocks.FIRE).canBurn(blockGetter.getBlockState(blockPos.below()));
        if (canBurn) {
            return fireBlock.defaultBlockState().setValue(FireBlock.NORTH, fireBlock.canBurn(blockGetter.getBlockState(blockPos.north())))
                .setValue(FireBlock.EAST, fireBlock.canBurn(blockGetter.getBlockState(blockPos.east())))
                .setValue(FireBlock.SOUTH, fireBlock.canBurn(blockGetter.getBlockState(blockPos.south())))
                .setValue(FireBlock.WEST, fireBlock.canBurn(blockGetter.getBlockState(blockPos.west())))
                .setValue(FireBlock.UP, fireBlock.canBurn(blockGetter.getBlockState(blockPos.above())));
        }

        return fireBlock.defaultBlockState();
    }
}
