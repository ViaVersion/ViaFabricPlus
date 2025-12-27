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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;

public final class WallConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final WallBlock wallBlock = (WallBlock) blockState.getBlock();
        final boolean connectsSouth = this.connectsTo(levelReader, blockPos.north(), Direction.SOUTH);
        final boolean connectsWest = this.connectsTo(levelReader, blockPos.east(), Direction.WEST);
        final boolean connectsNorth = this.connectsTo(levelReader, blockPos.south(), Direction.NORTH);
        final boolean connectsEast = this.connectsTo(levelReader, blockPos.west(), Direction.EAST);
        final boolean down = connectsSouth && !connectsWest && connectsNorth && !connectsEast || !connectsSouth && connectsWest && !connectsNorth && connectsEast;
        return wallBlock.defaultBlockState()
            .setValue(WallBlock.UP, !down || !levelReader.getBlockState(blockPos.above()).isAir())
            .setValue(WallBlock.NORTH, getWallSide(connectsSouth))
            .setValue(WallBlock.EAST, getWallSide(connectsWest))
            .setValue(WallBlock.SOUTH, getWallSide(connectsNorth))
            .setValue(WallBlock.WEST, getWallSide(connectsEast));
    }

    private boolean connectsTo(final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState blockState = blockGetter.getBlockState(blockPos);
        final Block block = blockState.getBlock();
        // TODO: Figure out modern code
        final boolean same = /*faceShape == FaceShape.MIDDLE_POLE_THICK || faceShape == FaceShape.MIDDLE_POLE &&*/ blockState.is(BlockTags.FENCE_GATES);
        return !isExceptionForConnection(block) && blockState.isSolidRender() || same;
    }

    private boolean isExceptionForConnection(Block block) {
        return Block1_14.isExceptBlockForAttachWithPiston(block)
            || block == Blocks.BARRIER
            || block == Blocks.MELON
            || block == Blocks.PUMPKIN
            || block == Blocks.CARVED_PUMPKIN;
    }

    private WallSide getWallSide(final boolean value) {
        return value ? WallSide.LOW : WallSide.NONE; // TODO: Incorrect/fix
    }
}
