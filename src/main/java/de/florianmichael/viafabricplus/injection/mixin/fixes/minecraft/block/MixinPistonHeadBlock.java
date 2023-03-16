/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHeadBlock.class)
public class MixinPistonHeadBlock extends FacingBlock {

    public MixinPistonHeadBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaLoadingBase.getClassWrapper() != null && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(viafabricplus_getCoreShape_v1_8_x(state));
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getClassWrapper() != null && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return VoxelShapes.union(viafabricplus_getHeadShape_v1_8_x(state), viafabricplus_getCoreShape_v1_8_x(state));
        }

        return super.getCollisionShape(state, world, pos, context);
    }

    @Unique
    private final VoxelShape viafabricplus_core_down_shape_v1_8_x = Block.createCuboidShape(6, 4, 6, 10, 16, 10);

    @Unique
    private final VoxelShape viafabricplus_core_up_shape_v1_8_x = Block.createCuboidShape(6, 0, 6, 10, 12, 10);

    @Unique
    private final VoxelShape viafabricplus_core_north_shape_v1_8_x = Block.createCuboidShape(4, 6, 4, 12, 10, 16);

    @Unique
    private final VoxelShape viafabricplus_core_south_shape_v1_8_x = Block.createCuboidShape(4, 6, 0, 12, 10, 12);

    @Unique
    private final VoxelShape viafabricplus_core_west_shape_v1_8_x = Block.createCuboidShape(6, 4, 4, 10, 12, 16);

    @Unique
    private final VoxelShape viafabricplus_core_east_shape_v1_8_x = Block.createCuboidShape(0, 6, 4, 12, 10, 12);

    @Unique
    private VoxelShape viafabricplus_getCoreShape_v1_8_x(BlockState state) {
        final Direction direction = state.get(FACING);

        return switch (direction) {
            case DOWN -> viafabricplus_core_down_shape_v1_8_x;
            case UP -> viafabricplus_core_up_shape_v1_8_x;
            case NORTH -> viafabricplus_core_north_shape_v1_8_x;
            case SOUTH -> viafabricplus_core_south_shape_v1_8_x;
            case WEST -> viafabricplus_core_west_shape_v1_8_x;
            case EAST -> viafabricplus_core_east_shape_v1_8_x;
        };
    }

    @Unique
    private final VoxelShape viafabricplus_head_down_shape_v1_8_x = Block.createCuboidShape(0, 0, 0, 16, 4, 16);

    @Unique
    private final VoxelShape viafabricplus_head_up_shape_v1_8_x = Block.createCuboidShape(0, 12, 0, 16, 16, 16);

    @Unique
    private final VoxelShape viafabricplus_head_north_shape_v1_8_x = Block.createCuboidShape(0, 0, 0, 16, 16, 4);

    @Unique
    private final VoxelShape viafabricplus_head_south_shape_v1_8_x = Block.createCuboidShape(0, 0, 12, 16, 16, 16);

    @Unique
    private final VoxelShape viafabricplus_head_west_shape_v1_8_x = Block.createCuboidShape(0, 0, 0, 4, 16, 16);

    @Unique
    private final VoxelShape viafabricplus_head_east_shape_v1_8_x = Block.createCuboidShape(12, 0, 0, 16, 16, 16);

    @Unique
    private VoxelShape viafabricplus_getHeadShape_v1_8_x(BlockState state) {
        return switch (state.get(FACING)) {
            case DOWN -> viafabricplus_head_down_shape_v1_8_x;
            case UP -> viafabricplus_head_up_shape_v1_8_x;
            case NORTH -> viafabricplus_head_north_shape_v1_8_x;
            case SOUTH -> viafabricplus_head_south_shape_v1_8_x;
            case WEST -> viafabricplus_head_west_shape_v1_8_x;
            case EAST -> viafabricplus_head_east_shape_v1_8_x;
        };
    }
}
