/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.block;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class MixinWallBlock extends Block {

    @Unique
    private VoxelShape[] viaFabricPlus$shapes_r1_12_2;

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shapes_r1_12_2;

    @Unique
    private VoxelShape[] viaFabricPlus$shapes_r1_8;

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shapes_r1_4_2;

    @Unique
    private final Object2IntMap<BlockState> viaFabricPlus$shapeIndexCache_r1_12_2 = new Object2IntOpenHashMap<>();

    public MixinWallBlock(Properties settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes1_12_2(Properties properties, CallbackInfo ci) {
        this.viaFabricPlus$shapes_r1_12_2 = this.viaFabricPlus$createShapes1_12_2(false);
        this.viaFabricPlus$collision_shapes_r1_12_2 = this.viaFabricPlus$createShapes1_12_2(true);

        this.viaFabricPlus$shapes_r1_8 = this.viaFabricPlus$shapes_r1_12_2.clone();
        this.viaFabricPlus$shapes_r1_8[5] = Shapes.box(0.3125, 0.0, 0.0, 0.6875, 0.8125, 1.0);
        this.viaFabricPlus$shapes_r1_8[10] = Shapes.box(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);

        this.viaFabricPlus$collision_shapes_r1_4_2 = this.viaFabricPlus$createCollisionShapes1_4_2();
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void changeCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
            cir.setReturnValue(this.viaFabricPlus$collision_shapes_r1_4_2[this.viaFabricPlus$getShapeIndex(state)]);
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(this.viaFabricPlus$collision_shapes_r1_12_2[this.viaFabricPlus$getShapeIndex(state)]);
        }
    }

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    private void changeOutlineShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(this.viaFabricPlus$shapes_r1_8[this.viaFabricPlus$getShapeIndex(state)]);
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(this.viaFabricPlus$shapes_r1_12_2[this.viaFabricPlus$getShapeIndex(state)]);
        }
    }

    @Override
    public @NonNull VoxelShape getOcclusionShape(@NonNull BlockState state) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return this.viaFabricPlus$shapes_r1_8[this.viaFabricPlus$getShapeIndex(state)];
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.viaFabricPlus$shapes_r1_12_2[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getOcclusionShape(state);
        }
    }

    @Unique
    private VoxelShape[] viaFabricPlus$createShapes1_12_2(final boolean collision) {
        return new VoxelShape[]{
            Shapes.box(0.25, 0.0, 0.25, 0.75, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.25, 0.0, 0.25, 0.75, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.25, 0.75, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.0, 0.0, 0.25, 0.75, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.25, 0.0, 0.0, 0.75, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.3125, 0.0, 0.0, 0.6875, collision ? 1.5 : 0.875, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 0.75, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.0, 0.0, 0.0, 0.75, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.25, 0.0, 0.25, 1.0, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.25, 0.0, 0.25, 1.0, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.3125, 1.0, collision ? 1.5 : 0.875, 0.6875),
            Shapes.box(0.0, 0.0, 0.25, 1.0, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.25, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.25, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 0.75),
            Shapes.box(0.0, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 1.0)
        };
    }

    @Unique
    private VoxelShape[] viaFabricPlus$createCollisionShapes1_4_2() {
        return new VoxelShape[]{
            Shapes.box(0.375, 0.0, 0.375, 0.625, 1.5, 0.625),
            Shapes.box(0.375, 0.0, 0.375, 0.625, 1.5, 1.0),
            Shapes.box(0.0, 0.0, 0.375, 0.625, 1.5, 0.625),
            Shapes.box(0.0, 0.0, 0.375, 0.625, 1.5, 1.0),
            Shapes.box(0.375, 0.0, 0.0, 0.625, 1.5, 0.625),
            Shapes.box(0.375, 0.0, 0.0, 0.625, 1.5, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 0.625, 1.5, 0.625),
            Shapes.box(0.0, 0.0, 0.0, 0.625, 1.5, 1.0),
            Shapes.box(0.375, 0.0, 0.375, 1.0, 1.5, 0.625),
            Shapes.box(0.375, 0.0, 0.375, 1.0, 1.5, 1.0),
            Shapes.box(0.0, 0.0, 0.375, 1.0, 1.5, 0.625),
            Shapes.box(0.0, 0.0, 0.375, 1.0, 1.5, 1.0),
            Shapes.box(0.375, 0.0, 0.0, 1.0, 1.5, 0.625),
            Shapes.box(0.375, 0.0, 0.0, 1.0, 1.5, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 1.0, 1.5, 0.625),
            Shapes.box(0.0, 0.0, 0.0, 1.0, 1.5, 1.0)
        };
    }

    @Unique
    private int viaFabricPlus$getShapeIndex(BlockState state) {
        return this.viaFabricPlus$shapeIndexCache_r1_12_2.computeIfAbsent(state, _ -> {
            int index = 0;
            if (!WallSide.NONE.equals(state.getValue(WallBlock.NORTH))) {
                index |= 1 << Direction.NORTH.get2DDataValue();
            }

            if (!WallSide.NONE.equals(state.getValue(WallBlock.EAST))) {
                index |= 1 << Direction.EAST.get2DDataValue();
            }

            if (!WallSide.NONE.equals(state.getValue(WallBlock.SOUTH))) {
                index |= 1 << Direction.SOUTH.get2DDataValue();
            }

            if (!WallSide.NONE.equals(state.getValue(WallBlock.WEST))) {
                index |= 1 << Direction.WEST.get2DDataValue();
            }

            return index;
        });
    }

}
