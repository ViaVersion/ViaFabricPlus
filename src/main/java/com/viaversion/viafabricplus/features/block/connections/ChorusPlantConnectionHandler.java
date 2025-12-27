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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;

// Code sourced and adapted from 1.12.2 (Feather)
public final class ChorusPlantConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final Block block = blockState.getBlock();
        final Block belowBlock = levelReader.getBlockState(blockPos.below()).getBlock();
        final Block aboveBlock = levelReader.getBlockState(blockPos.above()).getBlock();
        final Block northBlock = levelReader.getBlockState(blockPos.north()).getBlock();
        final Block eastBlock = levelReader.getBlockState(blockPos.east()).getBlock();
        final Block southBlock = levelReader.getBlockState(blockPos.south()).getBlock();
        final Block westBlock = levelReader.getBlockState(blockPos.west()).getBlock();
        return blockState.setValue(PipeBlock.DOWN, belowBlock == block || belowBlock == Blocks.CHORUS_FLOWER || belowBlock == Blocks.END_STONE)
            .setValue(PipeBlock.UP, aboveBlock == block || aboveBlock == Blocks.CHORUS_FLOWER)
            .setValue(PipeBlock.NORTH, northBlock == block || northBlock == Blocks.CHORUS_FLOWER)
            .setValue(PipeBlock.EAST, eastBlock == block || eastBlock == Blocks.CHORUS_FLOWER)
            .setValue(PipeBlock.SOUTH, southBlock == block || southBlock == Blocks.CHORUS_FLOWER)
            .setValue(PipeBlock.WEST, westBlock == block || westBlock == Blocks.CHORUS_FLOWER);
    }
}
