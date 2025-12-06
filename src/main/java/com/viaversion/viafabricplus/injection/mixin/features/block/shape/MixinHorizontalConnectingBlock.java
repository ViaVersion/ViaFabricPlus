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

package com.viaversion.viafabricplus.injection.mixin.features.block.shape;

import com.viaversion.viafabricplus.injection.access.block.shape.IHorizontalConnectingBlock;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CrossCollisionBlock.class)
public abstract class MixinHorizontalConnectingBlock implements IHorizontalConnectingBlock {

    @Unique
    private final Object2IntMap<BlockState> viaFabricPlus$SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

    @Override
    public int viaFabricPlus$getShapeIndex(final BlockState blockState) {
        return viaFabricPlus$SHAPE_INDEX_CACHE.computeIfAbsent(blockState, statex -> {
            int index = 0;
            if (blockState.getValue(CrossCollisionBlock.NORTH)) {
                index |= 1 << Direction.NORTH.get2DDataValue();
            }
            if (blockState.getValue(CrossCollisionBlock.EAST)) {
                index |= 1 << Direction.EAST.get2DDataValue();
            }
            if (blockState.getValue(CrossCollisionBlock.SOUTH)) {
                index |= 1 << Direction.SOUTH.get2DDataValue();
            }
            if (blockState.getValue(CrossCollisionBlock.WEST)) {
                index |= 1 << Direction.WEST.get2DDataValue();
            }

            return index;
        });
    }

}
