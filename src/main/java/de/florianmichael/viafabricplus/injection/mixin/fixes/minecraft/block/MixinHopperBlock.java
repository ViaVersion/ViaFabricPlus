/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public abstract class MixinHopperBlock extends BlockWithEntity {

    @Unique
    private final static VoxelShape viaFabricPlus$inside_shape_r1_12_2 = Block.createCuboidShape(2, 10, 2, 14, 16, 14);

    @Unique
    private final static VoxelShape viaFabricPlus$hopper_shape_r1_12_2 = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), viaFabricPlus$inside_shape_r1_12_2, BooleanBiFunction.ONLY_FIRST);

    @Unique
    private boolean viaFabricPlus$requireOriginalShape;

    public MixinHopperBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (viaFabricPlus$requireOriginalShape) {
            viaFabricPlus$requireOriginalShape = false;
            return;
        }
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(viaFabricPlus$hopper_shape_r1_12_2);
        }
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    public void injectGetRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(viaFabricPlus$inside_shape_r1_12_2);
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        viaFabricPlus$requireOriginalShape = true;
        return super.getCullingShape(state, world, pos);
    }
}
