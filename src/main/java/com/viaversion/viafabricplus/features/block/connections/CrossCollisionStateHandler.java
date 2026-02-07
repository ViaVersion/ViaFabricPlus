/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

// Handles [Fences, Iron Bars, Glass Panes]
public record CrossCollisionStateHandler(Predicate<BlockState> customAllowed) implements IBlockStateHandler {

    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        return blockState
            .setValue(CrossCollisionBlock.NORTH, connectsTo(levelReader, blockPos.north(), Direction.NORTH))
            .setValue(CrossCollisionBlock.SOUTH, connectsTo(levelReader, blockPos.south(), Direction.SOUTH))
            .setValue(CrossCollisionBlock.WEST, connectsTo(levelReader, blockPos.west(), Direction.WEST))
            .setValue(CrossCollisionBlock.EAST, connectsTo(levelReader, blockPos.east(), Direction.EAST));
    }

    private boolean connectsTo(final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState neighborState = blockGetter.getBlockState(blockPos);

        boolean bl = false;
        if (neighborState.is(BlockTags.STAIRS)) {
            // TODO: Sometimes isn't right
            bl = neighborState.getValue(StairBlock.FACING) == direction.getOpposite(); // Only connect to the backside of stairs
        }

        return !isExceptionForConnection(neighborState) && (this.customAllowed.test(neighborState) || bl || neighborState.isSolidRender());
    }

}
