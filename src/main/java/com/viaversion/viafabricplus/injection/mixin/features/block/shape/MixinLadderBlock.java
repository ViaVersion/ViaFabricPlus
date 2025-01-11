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

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LadderBlock.class)
public abstract class MixinLadderBlock {

    @Unique
    private static final VoxelShape viaFabricPlus$east_shape_r1_8_x = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);

    @Unique
    private static final VoxelShape viaFabricPlus$west_shape_r1_8_x = Block.createCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    @Unique
    private static final VoxelShape viaFabricPlus$south_shape_r1_8_x = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);

    @Unique
    private static final VoxelShape viaFabricPlus$north_shape_r1_8_x = Block.createCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            switch (state.get(LadderBlock.FACING)) {
                case NORTH -> ci.setReturnValue(viaFabricPlus$north_shape_r1_8_x);
                case SOUTH -> ci.setReturnValue(viaFabricPlus$south_shape_r1_8_x);
                case WEST -> ci.setReturnValue(viaFabricPlus$west_shape_r1_8_x);
                default -> ci.setReturnValue(viaFabricPlus$east_shape_r1_8_x);
            }
        }
    }

}
