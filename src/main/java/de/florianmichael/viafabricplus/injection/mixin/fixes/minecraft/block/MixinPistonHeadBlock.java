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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHeadBlock.class)
public abstract class MixinPistonHeadBlock extends FacingBlock {

    @Shadow
    @Final
    protected static VoxelShape DOWN_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape UP_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape NORTH_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape SOUTH_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape WEST_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EAST_HEAD_SHAPE;

    @Unique
    private static final VoxelShape viaFabricPlus$up_arm_shape_r1_8_x = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);

    @Unique
    private static final VoxelShape viaFabricPlus$down_arm_shape_r1_8_x = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);

    @Unique
    private static final VoxelShape viaFabricPlus$south_arm_shape_r1_8_x = Block.createCuboidShape(4.0, 6.0, 0.0, 12.0, 10.0, 12.0);

    @Unique
    private static final VoxelShape viaFabricPlus$north_arm_shape_r1_8_x = Block.createCuboidShape(4.0, 6.0, 4.0, 12.0, 10.0, 16.0);

    @Unique
    private static final VoxelShape viaFabricPlus$east_arm_shape_r1_8_x = Block.createCuboidShape(0.0, 6.0, 4.0, 12.0, 10.0, 12.0);

    @Unique
    private static final VoxelShape viaFabricPlus$west_arm_shape_r1_8_x = Block.createCuboidShape(6.0, 4.0, 4.0, 10.0, 12.0, 16.0);

    protected MixinPistonHeadBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(switch (state.get(PistonHeadBlock.FACING)) {
                case DOWN -> DOWN_HEAD_SHAPE;
                case UP -> UP_HEAD_SHAPE;
                case NORTH -> NORTH_HEAD_SHAPE;
                case SOUTH -> SOUTH_HEAD_SHAPE;
                case WEST -> WEST_HEAD_SHAPE;
                case EAST -> EAST_HEAD_SHAPE;
            });
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return switch (state.get(PistonHeadBlock.FACING)) {
                case DOWN -> VoxelShapes.union(DOWN_HEAD_SHAPE, viaFabricPlus$down_arm_shape_r1_8_x);
                case UP -> VoxelShapes.union(UP_HEAD_SHAPE, viaFabricPlus$up_arm_shape_r1_8_x);
                case NORTH -> VoxelShapes.union(NORTH_HEAD_SHAPE, viaFabricPlus$north_arm_shape_r1_8_x);
                case SOUTH -> VoxelShapes.union(SOUTH_HEAD_SHAPE, viaFabricPlus$south_arm_shape_r1_8_x);
                case WEST -> VoxelShapes.union(WEST_HEAD_SHAPE, viaFabricPlus$west_arm_shape_r1_8_x);
                case EAST -> VoxelShapes.union(EAST_HEAD_SHAPE, viaFabricPlus$east_arm_shape_r1_8_x);
            };
        }

        return super.getCollisionShape(state, world, pos, context);
    }

}
