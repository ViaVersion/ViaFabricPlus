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
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class BarsConnectionHandler implements IBlockConnectionHandler {
    @Override
    public BlockState connect(final BlockState blockState, final LevelReader levelReader, final BlockPos blockPos) {
        final IronBarsBlock ironBarsBlock = (IronBarsBlock) blockState.getBlock();

        final BlockPos northPos = blockPos.north();
        final BlockState northState = levelReader.getBlockState(northPos);

        final BlockPos southPos = blockPos.south();
        final BlockState southState = levelReader.getBlockState(southPos);

        final BlockPos westPos = blockPos.west();
        final BlockState westState = levelReader.getBlockState(westPos);

        final BlockPos eastPos = blockPos.east();
        final BlockState eastState = levelReader.getBlockState(eastPos);

        return ironBarsBlock.defaultBlockState()
            .setValue(IronBarsBlock.NORTH, ironBarsBlock.attachsTo(northState, northState.isFaceSturdy(levelReader, northPos, Direction.SOUTH)))
            .setValue(IronBarsBlock.SOUTH, ironBarsBlock.attachsTo(southState, southState.isFaceSturdy(levelReader, southPos, Direction.NORTH)))
            .setValue(IronBarsBlock.WEST, ironBarsBlock.attachsTo(westState, westState.isFaceSturdy(levelReader, westPos, Direction.EAST)))
            .setValue(IronBarsBlock.EAST, ironBarsBlock.attachsTo(eastState, eastState.isFaceSturdy(levelReader, eastPos, Direction.WEST)));
    }
}
