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
    private VoxelShape[] collisionShapes1_12_2;

    @Unique
    private VoxelShape[] boundingShapes1_12_2;

    @Unique
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE1_12_2 = new Object2IntOpenHashMap<>();

    public MixinWallBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes(Settings settings, CallbackInfo ci) {
        this.collisionShapes1_12_2 = this.createShapes1_12_2(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
        this.boundingShapes1_12_2 = this.createShapes1_12_2(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
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
            cir.setReturnValue(this.boundingShapes1_12_2[this.getShapeIndex(state)]);
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void changeCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(WallBlock.UP) && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(this.collisionShapes1_12_2[this.getShapeIndex(state)]);
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
    private VoxelShape[] createShapes1_12_2(float radius1, float radius2, float height1, float offset2, float height2) {
        final float f = 8.0F - radius1;
        final float g = 8.0F + radius1;
        final float h = 8.0F - radius2;
        final float i = 8.0F + radius2;
        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0D, f, g, height1, g);
        final VoxelShape northShape = Block.createCuboidShape(h, offset2, 0.0D, i, height2, i);
        final VoxelShape southShape = Block.createCuboidShape(h, offset2, h, i, height2, 16.0D);
        final VoxelShape westShape = Block.createCuboidShape(0.0D, offset2, h, i, height2, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, offset2, h, 16.0D, height2, i);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
                VoxelShapes.empty(),
                Block.createCuboidShape(f, offset2, h, g, height1, 16.0D),
                Block.createCuboidShape(0.0D, offset2, f, i, height1, g),
                Block.createCuboidShape(f - 4, offset2, h - 1, g, height1, 16.0D),
                Block.createCuboidShape(f, offset2, 0.0D, g, height1, i),
                VoxelShapes.union(southShape, northShape),
                Block.createCuboidShape(f - 4, offset2, 0.0D, g, height1, i + 1),
                Block.createCuboidShape(f - 4, offset2, h - 5, g, height1, 16.0D),
                Block.createCuboidShape(h, offset2, f, 16.0D, height1, g),
                Block.createCuboidShape(h - 1, offset2, f, 16.0D, height1, g + 4),
                VoxelShapes.union(westShape, eastShape),
                Block.createCuboidShape(h - 5, offset2, f, 16.0D, height1, g + 4),
                Block.createCuboidShape(f, offset2, 0.0D, g + 4, height1, i + 1),
                Block.createCuboidShape(f, offset2, 0.0D, g + 4, height1, i + 5),
                Block.createCuboidShape(h - 5, offset2, f - 4, 16.0D, height1, g),
                Block.createCuboidShape(0, offset2, 0, 16.0D, height1, 16.0D)
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
        return this.SHAPE_INDEX_CACHE1_12_2.computeIntIfAbsent(state, statex -> {
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
