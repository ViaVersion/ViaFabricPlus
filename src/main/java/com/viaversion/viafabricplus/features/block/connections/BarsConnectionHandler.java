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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class BarsConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        return blockState.setValue(IronBarsBlock.NORTH, this.connectsTo(levelReader.getBlockState(blockPos.north()), blockPos.north(), Direction.SOUTH))
            .setValue(IronBarsBlock.SOUTH, this.connectsTo(levelReader.getBlockState(blockPos.south()), blockPos.south(), Direction.NORTH))
            .setValue(IronBarsBlock.WEST, this.connectsTo(levelReader.getBlockState(blockPos.west()), blockPos.west(), Direction.EAST))
            .setValue(IronBarsBlock.EAST, this.connectsTo(levelReader.getBlockState(blockPos.east()), blockPos.east(), Direction.WEST));
    }

    private boolean connectsTo(final BlockState blockState, final BlockPos blockPos, final Direction direction) {
        final Block block = blockState.getBlock();
        // TODO: Figure out modern code
        return !connectsTo(block) && blockState.isSolidRender() /*|| faceShape == FaceShape.MIDDLE_POLE_THIN*/;
    }

    private boolean connectsTo(Block block) {
        return block instanceof ShulkerBoxBlock
            || block instanceof LeavesBlock
            || block == Blocks.BEACON
            || block == Blocks.CAULDRON
            || block == Blocks.GLOWSTONE
            || block == Blocks.ICE
            || block == Blocks.SEA_LANTERN
            || block == Blocks.PISTON
            || block == Blocks.STICKY_PISTON
            || block == Blocks.PISTON_HEAD
            || block == Blocks.MELON
            || block == Blocks.PUMPKIN
            || block == Blocks.CARVED_PUMPKIN
            || block == Blocks.BARRIER;
    }
}
