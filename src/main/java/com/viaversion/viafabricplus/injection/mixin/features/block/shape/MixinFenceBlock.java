/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.block.shape;

import com.viaversion.viafabricplus.features.block.interaction.Block1_14;
import com.viaversion.viafabricplus.injection.access.block.shape.ICrossCollisionBlock;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public abstract class MixinFenceBlock extends CrossCollisionBlock implements ICrossCollisionBlock {

    @Unique
    private final VoxelShape[] viaFabricPlus$outline_shape_r1_12_2 = new VoxelShape[]{
        Shapes.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D),
        Shapes.box(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D),
        Shapes.box(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D),
        Shapes.box(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D),
        Shapes.box(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D),
        Shapes.box(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D),
        Shapes.box(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D),
        Shapes.box(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D),
        Shapes.box(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D),
        Shapes.box(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D),
        Shapes.box(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D),
        Shapes.box(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D),
        Shapes.box(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D),
        Shapes.box(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
        Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D),
        Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
    };

    @Unique
    private final VoxelShape viaFabricPlus$shape_b1_8_1 = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shape_r1_4_7;

    @Unique
    private VoxelShape[] viaFabricPlus$outline_shape_r1_4_7;

    protected MixinFenceBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Properties settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init1_4_7Shapes(Properties settings, CallbackInfo ci) {
        this.viaFabricPlus$collision_shape_r1_4_7 = this.viaFabricPlus$createShapes1_4_7(24.0F);
        this.viaFabricPlus$outline_shape_r1_4_7 = this.viaFabricPlus$createShapes1_4_7(16.0F);
    }

    @Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
    private void canConnect1_14(BlockState state, boolean neighborIsFullSquare, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14)) {
            if (!Block1_14.isExceptBlockForAttachWithPiston(state.getBlock())) {
                cir.setReturnValue(false);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Shapes.block();
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            return this.viaFabricPlus$outline_shape_r1_4_7[this.viaFabricPlus$getShapeIndex(state)];
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.viaFabricPlus$outline_shape_r1_12_2[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getShape(state, world, pos, context);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return viaFabricPlus$shape_b1_8_1;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            return this.viaFabricPlus$collision_shape_r1_4_7[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getCollisionShape(state, world, pos, context);
        }
    }

    @Unique
    private VoxelShape[] viaFabricPlus$createShapes1_4_7(final float height) {
        final float f = 6.0F;
        final float g = 10.0F;
        final float h = 6.0F;
        final float i = 10.0F;
        final VoxelShape baseShape = Block.box(f, 0.0, f, g, height, g);
        final VoxelShape northShape = Block.box(h, (float) 0.0, 0.0, i, height, i);
        final VoxelShape southShape = Block.box(h, (float) 0.0, h, i, height, 16.0);
        final VoxelShape westShape = Block.box(0.0, (float) 0.0, h, i, height, i);
        final VoxelShape eastShape = Block.box(h, (float) 0.0, h, 16.0, height, i);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
            Shapes.empty(),
            Block.box(f, (float) 0.0, h, g, height, 16.0D),
            Block.box(0.0D, (float) 0.0, f, i, height, g),
            Block.box(f - 6, (float) 0.0, h, g, height, 16.0D),
            Block.box(f, (float) 0.0, 0.0D, g, height, i),

            Shapes.or(southShape, northShape),
            Block.box(f - 6, (float) 0.0, 0.0D, g, height, i),
            Block.box(f - 6, (float) 0.0, h - 5, g, height, 16.0D),
            Block.box(h, (float) 0.0, f, 16.0D, height, g),
            Block.box(h, (float) 0.0, f, 16.0D, height, g + 6),

            Shapes.or(westShape, eastShape),
            Block.box(h - 5, (float) 0.0, f, 16.0D, height, g + 6),
            Block.box(f, (float) 0.0, 0.0D, g + 6, height, i),
            Block.box(f, (float) 0.0, 0.0D, g + 6, height, i + 5),
            Block.box(h - 5, (float) 0.0, f - 6, 16.0D, height, g),
            Block.box(0, (float) 0.0, 0, 16.0D, height, 16.0D)
        };

        for (int j = 0; j < 16; ++j) {
            voxelShapes[j] = Shapes.or(baseShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

}
