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
import com.viaversion.viafabricplus.injection.access.v1_14.ICrossCollisionBlock;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
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

@Mixin(FenceBlock.class)
public abstract class MixinFenceBlock extends CrossCollisionBlock implements ICrossCollisionBlock {

    @Unique
    private VoxelShape[] viaFabricPlus$shapes_r1_12_2;

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shapes_r1_4_7;

    @Unique
    private final VoxelShape viaFabricPlus$shape_b1_8_1 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    protected MixinFenceBlock(final float postWidth, final float postHeight, final float wallWidth, final float wallHeight, final float collisionHeight, final Properties properties) {
        super(postWidth, postHeight, wallWidth, wallHeight, collisionHeight, properties);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes1_12_2(Properties properties, CallbackInfo ci) {
        this.viaFabricPlus$shapes_r1_12_2 = this.viaFabricPlus$createShapes1_12_2(false);
        this.viaFabricPlus$collision_shapes_r1_4_7 = this.viaFabricPlus$createShapes1_12_2(true);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Shapes.block();
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.viaFabricPlus$shapes_r1_12_2[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getShape(state, world, pos, context);
        }
    }

    @Override
    public @NonNull VoxelShape getCollisionShape(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return viaFabricPlus$shape_b1_8_1;
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            return this.viaFabricPlus$collision_shapes_r1_4_7[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getCollisionShape(state, world, pos, context);
        }
    }

    @Unique
    private VoxelShape[] viaFabricPlus$createShapes1_12_2(final boolean collision) {
        return new VoxelShape[]{
            Shapes.box(0.375, 0.0, 0.375, 0.625, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.375, 0.0, 0.375, 0.625, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.375, 0.625, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.0, 0.0, 0.375, 0.625, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.375, 0.0, 0.0, 0.625, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.375, 0.0, 0.0, 0.625, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 0.625, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.0, 0.0, 0.0, 0.625, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.375, 0.0, 0.375, 1.0, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.375, 0.0, 0.375, 1.0, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.375, 1.0, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.0, 0.0, 0.375, 1.0, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.375, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.375, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 1.0),
            Shapes.box(0.0, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 0.625),
            Shapes.box(0.0, 0.0, 0.0, 1.0, collision ? 1.5 : 1.0, 1.0)
        };
    }

}
