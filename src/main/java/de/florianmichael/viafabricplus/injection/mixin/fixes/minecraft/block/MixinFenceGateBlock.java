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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceGateBlock.class)
public abstract class MixinFenceGateBlock extends HorizontalFacingBlock {

    @Unique
    private static final VoxelShape _b1_8_1_X_AXIS_SHAPE = VoxelShapes.fullCube();

    @Unique
    private static final VoxelShape _b1_8_1_Z_AXIS_SHAPE = VoxelShapes.fullCube();

    @Unique
    private static final VoxelShape _b1_8_1_X_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    @Unique
    private static final VoxelShape _b1_8_1_Z_AXIS_COLLISION_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    protected MixinFenceGateBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!state.get(FenceGateBlock.IN_WALL) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            cir.setReturnValue(state.get(FACING).getAxis() == Direction.Axis.X ? _b1_8_1_X_AXIS_SHAPE : _b1_8_1_Z_AXIS_SHAPE);
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void changeCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!state.get(FenceGateBlock.OPEN) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            cir.setReturnValue(state.get(FACING).getAxis() == Direction.Axis.X ? _b1_8_1_X_AXIS_COLLISION_SHAPE : _b1_8_1_Z_AXIS_COLLISION_SHAPE);
        }
    }

}
