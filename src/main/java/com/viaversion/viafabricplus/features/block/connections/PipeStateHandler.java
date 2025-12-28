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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

// Code sourced and adapted from 1.12.2 (Feather)
public final class PipeStateHandler implements IBlockStateHandler {

    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final Block block = blockState.getBlock();
        final BlockState belowState = levelReader.getBlockState(blockPos.below());
        final BlockState aboveState = levelReader.getBlockState(blockPos.above());
        final BlockState northState = levelReader.getBlockState(blockPos.north());
        final BlockState eastState = levelReader.getBlockState(blockPos.east());
        final BlockState southState = levelReader.getBlockState(blockPos.south());
        final BlockState westState = levelReader.getBlockState(blockPos.west());
        return blockState.setValue(PipeBlock.DOWN, belowState.is(block) || belowState.is(Blocks.CHORUS_FLOWER) || belowState.is(Blocks.END_STONE))
            .setValue(PipeBlock.UP, aboveState.is(block) || aboveState.is(Blocks.CHORUS_FLOWER))
            .setValue(PipeBlock.NORTH, northState.is(block) || northState.is(Blocks.CHORUS_FLOWER))
            .setValue(PipeBlock.EAST, eastState.is(block) || eastState.is(Blocks.CHORUS_FLOWER))
            .setValue(PipeBlock.SOUTH, southState.is(block) || southState.is(Blocks.CHORUS_FLOWER))
            .setValue(PipeBlock.WEST, westState.is(block) || westState.is(Blocks.CHORUS_FLOWER));
    }

}
