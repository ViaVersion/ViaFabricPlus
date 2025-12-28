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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class CrossCollisionConnectionHandler implements IBlockConnectionHandler {

    @Override
    public BlockState connect(final BlockState blockState, final BlockGetter blockGetter, final BlockPos blockPos) {
        final CrossCollisionBlock crossCollisionBlock = (CrossCollisionBlock) blockState.getBlock();
        return blockState
            .setValue(CrossCollisionBlock.NORTH, connectsTo(crossCollisionBlock, blockGetter, blockPos.north(), Direction.NORTH))
            .setValue(CrossCollisionBlock.SOUTH, connectsTo(crossCollisionBlock, blockGetter, blockPos.south(), Direction.SOUTH))
            .setValue(CrossCollisionBlock.WEST, connectsTo(crossCollisionBlock, blockGetter, blockPos.west(), Direction.WEST))
            .setValue(CrossCollisionBlock.EAST, connectsTo(crossCollisionBlock, blockGetter, blockPos.east(), Direction.EAST));
    }

    // TODO: Fine-tune and make perfect/1:1
    private boolean connectsTo(final CrossCollisionBlock crossCollisionBlock, final BlockGetter blockGetter, final BlockPos blockPos, final Direction direction) {
        final BlockState neighborState = blockGetter.getBlockState(blockPos);

        boolean bl = true;
        if (crossCollisionBlock instanceof IronBarsBlock ironBarsBlock) {
            bl = ironBarsBlock.attachsTo(neighborState, neighborState.isFaceSturdy(blockGetter, blockPos, direction));
        } else {
            System.out.println("Hello! A pane-type-block tried to connect to something but we don't know what type of pane this is (" + crossCollisionBlock.getClass().getSimpleName() + ")! If you see this, please report it on the ViaFabricPlus GitHub!");
        }

        return !isExceptionForConnection(neighborState) && bl;
    }

    private boolean isExceptionForConnection(final BlockState blockState) {
        return blockState.isAir()
            || Block1_14.isExceptBlockForAttachWithPiston(blockState.getBlock())
            || blockState.is(Blocks.MELON)
            || blockState.is(Blocks.PUMPKIN)
            || blockState.is(Blocks.CARVED_PUMPKIN)
            || blockState.is(Blocks.JACK_O_LANTERN)
            || blockState.is(Blocks.BARRIER);
    }

}
