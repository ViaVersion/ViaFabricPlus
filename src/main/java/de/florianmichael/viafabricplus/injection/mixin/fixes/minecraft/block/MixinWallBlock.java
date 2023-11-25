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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(WallBlock.class)
public abstract class MixinWallBlock extends Block {

    @Shadow
    @Final
    private Map<BlockState, VoxelShape> shapeMap;

    @Unique
    private final Object2IntMap<BlockState> viaFabricPlus$shape_index_cache_r1_12_2 = new Object2IntOpenHashMap<>();

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shape_r1_12_2;

    @Unique
    private VoxelShape[] viaFabricPlus$outline_shape_r1_12_2;

    public MixinWallBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes1_12_2(Settings settings, CallbackInfo ci) {
        this.viaFabricPlus$collision_shape_r1_12_2 = this.createShapes1_12_2(24.0F);
        this.viaFabricPlus$outline_shape_r1_12_2 = this.createShapes1_12_2(16.0F);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void modifyPlacementState(CallbackInfoReturnable<BlockState> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
            cir.setReturnValue(oldWallPlacementLogic(cir.getReturnValue()));
        }
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    private void modifyBlockState(CallbackInfoReturnable<BlockState> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
            cir.setReturnValue(oldWallPlacementLogic(cir.getReturnValue()));
        }
    }

    @Unique
    private static BlockState oldWallPlacementLogic(BlockState state) {
        boolean addUp = false;
        if (state.get(WallBlock.NORTH_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.NORTH_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.EAST_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.EAST_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.SOUTH_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.SOUTH_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.WEST_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.WEST_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (addUp) {
            state = state.with(WallBlock.UP, true);
        }
        return state;
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(WallBlock.UP) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(this.viaFabricPlus$outline_shape_r1_12_2[this.getShapeIndex(state)]);
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void changeCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(WallBlock.UP) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(this.viaFabricPlus$collision_shape_r1_12_2[this.getShapeIndex(state)]);
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        if (state.get(WallBlock.UP) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            return this.shapeMap.get(state);
        }

        return super.getCullingShape(state, world, pos);
    }

    @Unique
    private VoxelShape[] createShapes1_12_2(final float height) {
        final float f = 4.0F;
        final float g = 12.0F;
        final float h = 5.0F;
        final float i = 11.0F;

        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0D, f, g, height, g);
        final VoxelShape northShape = Block.createCuboidShape(h, (float) 0.0, 0.0D, i, height, i);
        final VoxelShape southShape = Block.createCuboidShape(h, (float) 0.0, h, i, height, 16.0D);
        final VoxelShape westShape = Block.createCuboidShape(0.0D, (float) 0.0, h, i, height, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, (float) 0.0, h, 16.0D, height, i);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
                VoxelShapes.empty(),
                Block.createCuboidShape(f, (float) 0.0, h, g, height, 16.0D),
                Block.createCuboidShape(0.0D, (float) 0.0, f, i, height, g),
                Block.createCuboidShape(f - 4, (float) 0.0, h - 1, g, height, 16.0D),
                Block.createCuboidShape(f, (float) 0.0, 0.0D, g, height, i),
                VoxelShapes.union(southShape, northShape),
                Block.createCuboidShape(f - 4, (float) 0.0, 0.0D, g, height, i + 1),
                Block.createCuboidShape(f - 4, (float) 0.0, h - 5, g, height, 16.0D),
                Block.createCuboidShape(h, (float) 0.0, f, 16.0D, height, g),
                Block.createCuboidShape(h - 1, (float) 0.0, f, 16.0D, height, g + 4),
                VoxelShapes.union(westShape, eastShape),
                Block.createCuboidShape(h - 5, (float) 0.0, f, 16.0D, height, g + 4),
                Block.createCuboidShape(f, (float) 0.0, 0.0D, g + 4, height, i + 1),
                Block.createCuboidShape(f, (float) 0.0, 0.0D, g + 4, height, i + 5),
                Block.createCuboidShape(h - 5, (float) 0.0, f - 4, 16.0D, height, g),
                Block.createCuboidShape(0, (float) 0.0, 0, 16.0D, height, 16.0D)
        };

        for (int j = 0; j < 16; ++j) {
            voxelShapes[j] = VoxelShapes.union(baseShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

    @Unique
    private static int getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontal();
    }

    @Unique
    private int getShapeIndex(BlockState state) {
        return this.viaFabricPlus$shape_index_cache_r1_12_2.computeIntIfAbsent(state, statex -> {
            int i = 0;
            if (!WallShape.NONE.equals(statex.get(WallBlock.NORTH_SHAPE))) {
                i |= getDirectionMask(Direction.NORTH);
            }

            if (!WallShape.NONE.equals(statex.get(WallBlock.EAST_SHAPE))) {
                i |= getDirectionMask(Direction.EAST);
            }

            if (!WallShape.NONE.equals(statex.get(WallBlock.SOUTH_SHAPE))) {
                i |= getDirectionMask(Direction.SOUTH);
            }

            if (!WallShape.NONE.equals(statex.get(WallBlock.WEST_SHAPE))) {
                i |= getDirectionMask(Direction.WEST);
            }

            return i;
        });
    }

}
