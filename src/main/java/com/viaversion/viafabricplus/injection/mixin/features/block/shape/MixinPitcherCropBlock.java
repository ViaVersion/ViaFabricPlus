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
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PitcherCropBlock.class)
public abstract class MixinPitcherCropBlock extends TallPlantBlock {

    @Shadow
    @Final
    public static IntProperty AGE;

    @Shadow
    @Final
    public static EnumProperty<DoubleBlockHalf> HALF;

    @Shadow
    @Final
    private static VoxelShape AGE_0_SHAPE;

    @Shadow
    @Final
    private static VoxelShape LOWER_COLLISION_SHAPE;

    @Unique
    private static final VoxelShape viaFabricPlus$grown_upper_outline_shape_r1_21_4 = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);

    @Unique
    private static final VoxelShape viaFabricPlus$grown_lower_outline_shape_r1_21_4 = Block.createCuboidShape(3.0, -1.0, 3.0, 13.0, 16.0, 13.0);

    @Unique
    private static final VoxelShape[] viaFabricPlus$upper_outline_shapes_r1_21_4 = new VoxelShape[]{Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 11.0, 13.0), viaFabricPlus$grown_upper_outline_shape_r1_21_4};

    @Unique
    private static final VoxelShape[] viaFabricPlus$lower_outline_shapes_r1_21_4 = new VoxelShape[]{AGE_0_SHAPE, Block.createCuboidShape(3.0, -1.0, 3.0, 13.0, 14.0, 13.0), viaFabricPlus$grown_lower_outline_shape_r1_21_4, viaFabricPlus$grown_lower_outline_shape_r1_21_4, viaFabricPlus$grown_lower_outline_shape_r1_21_4};

    public MixinPitcherCropBlock(final Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final int age = state.get(AGE);
            if (state.get(HALF) == DoubleBlockHalf.UPPER) {
                cir.setReturnValue(viaFabricPlus$upper_outline_shapes_r1_21_4[Math.min(Math.abs(4 - (age + 1)), viaFabricPlus$upper_outline_shapes_r1_21_4.length - 1)]);
            } else {
                cir.setReturnValue(viaFabricPlus$lower_outline_shapes_r1_21_4[age]);
            }
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void changeBlockStatePropertyPriority(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            if (state.get(AGE) == 0) {
                cir.setReturnValue(AGE_0_SHAPE);
            } else {
                cir.setReturnValue(state.get(HALF) == DoubleBlockHalf.LOWER ? LOWER_COLLISION_SHAPE : super.getCollisionShape(state, world, pos, context));
            }
        }
    }

}
