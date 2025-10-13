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

import com.viaversion.viafabricplus.features.block.interaction.Block1_14;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.WallShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class MixinWallBlock extends Block {

    @Unique
    private final Object2IntMap<BlockState> viaFabricPlus$shapeIndexCache_r1_12_2 = new Object2IntOpenHashMap<>();

    @Shadow
    @Final
    private Function<BlockState, VoxelShape> outlineShapeFunction;

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shape_r1_12_2;

    @Unique
    private VoxelShape[] viaFabricPlus$outline_shape_r1_12_2;

    public MixinWallBlock(Settings settings) {
        super(settings);
    }

    @Unique
    private static BlockState viaFabricPlus$oldWallPlacementLogic(BlockState state) {
        boolean addUp = false;
        if (state.get(WallBlock.NORTH_WALL_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.NORTH_WALL_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.EAST_WALL_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.EAST_WALL_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.SOUTH_WALL_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.SOUTH_WALL_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.WEST_WALL_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.WEST_WALL_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (addUp) {
            state = state.with(WallBlock.UP, true);
        }
        return state;
    }

    @Unique
    private static int viaFabricPlus$getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontalQuarterTurns();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes1_12_2(Settings settings, CallbackInfo ci) {
        this.viaFabricPlus$collision_shape_r1_12_2 = this.viaFabricPlus$createShapes1_12_2(24.0F, 24.0F);
        this.viaFabricPlus$outline_shape_r1_12_2 = this.viaFabricPlus$createShapes1_12_2(16.0F, 14.0F);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void modifyPlacementState(CallbackInfoReturnable<BlockState> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            cir.setReturnValue(viaFabricPlus$oldWallPlacementLogic(cir.getReturnValue()));
        }
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    private void modifyBlockState(CallbackInfoReturnable<BlockState> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            cir.setReturnValue(viaFabricPlus$oldWallPlacementLogic(cir.getReturnValue()));
        }
    }

    @Inject(method = "shouldConnectTo", at = @At("RETURN"), cancellable = true)
    private void shouldConnectTo1_14(BlockState state, boolean neighborIsFullSquare, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14)) {
            if (!Block1_14.isExceptBlockForAttachWithPiston(state.getBlock())) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void changeCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(WallBlock.UP) && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(this.viaFabricPlus$collision_shape_r1_12_2[this.viaFabricPlus$getShapeIndex(state)]);
        }
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(WallBlock.UP) && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(this.viaFabricPlus$outline_shape_r1_12_2[this.viaFabricPlus$getShapeIndex(state)]);
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state) {
        if (state.get(WallBlock.UP) && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.outlineShapeFunction.apply(state);
        } else {
            return super.getCullingShape(state);
        }
    }

    @Unique
    private VoxelShape[] viaFabricPlus$createShapes1_12_2(final float height1, final float height2) {
        final float f = 4.0F;
        final float g = 12.0F;
        final float h = 5.0F;
        final float i = 11.0F;

        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0D, f, g, height1, g);
        final VoxelShape northShape = Block.createCuboidShape(h, 0.0, 0.0D, i, height2, i);
        final VoxelShape southShape = Block.createCuboidShape(h, 0.0, h, i, height2, 16.0D);
        final VoxelShape westShape = Block.createCuboidShape(0.0D, 0.0, h, i, height2, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, 0.0, h, 16.0D, height2, i);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
            VoxelShapes.empty(),
            Block.createCuboidShape(f, 0.0, h, g, height1, 16.0D),
            Block.createCuboidShape(0.0D, 0.0, f, i, height1, g),
            Block.createCuboidShape(f - 4, 0.0, h - 1, g, height1, 16.0D),
            Block.createCuboidShape(f, 0.0, 0.0D, g, height1, i),
            VoxelShapes.union(southShape, northShape),
            Block.createCuboidShape(f - 4, 0.0, 0.0D, g, height1, i + 1),
            Block.createCuboidShape(f - 4, 0.0, h - 5, g, height1, 16.0D),
            Block.createCuboidShape(h, 0.0, f, 16.0D, height1, g),
            Block.createCuboidShape(h - 1, 0.0, f, 16.0D, height1, g + 4),
            VoxelShapes.union(westShape, eastShape),
            Block.createCuboidShape(h - 5, 0.0, f, 16.0D, height1, g + 4),
            Block.createCuboidShape(f, 0.0, 0.0D, g + 4, height1, i + 1),
            Block.createCuboidShape(f, 0.0, 0.0D, g + 4, height1, i + 5),
            Block.createCuboidShape(h - 5, 0.0, f - 4, 16.0D, height1, g),
            Block.createCuboidShape(0, 0.0, 0, 16.0D, height1, 16.0D)
        };

        for (int j = 0; j < 16; ++j) {
            voxelShapes[j] = VoxelShapes.union(baseShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

    @Unique
    private int viaFabricPlus$getShapeIndex(BlockState state) {
        return this.viaFabricPlus$shapeIndexCache_r1_12_2.computeIntIfAbsent(state, statex -> {
            int i = 0;
            if (!WallShape.NONE.equals(statex.get(WallBlock.NORTH_WALL_SHAPE))) {
                i |= viaFabricPlus$getDirectionMask(Direction.NORTH);
            }

            if (!WallShape.NONE.equals(statex.get(WallBlock.EAST_WALL_SHAPE))) {
                i |= viaFabricPlus$getDirectionMask(Direction.EAST);
            }

            if (!WallShape.NONE.equals(statex.get(WallBlock.SOUTH_WALL_SHAPE))) {
                i |= viaFabricPlus$getDirectionMask(Direction.SOUTH);
            }

            if (!WallShape.NONE.equals(statex.get(WallBlock.WEST_WALL_SHAPE))) {
                i |= viaFabricPlus$getDirectionMask(Direction.WEST);
            }

            return i;
        });
    }

}
