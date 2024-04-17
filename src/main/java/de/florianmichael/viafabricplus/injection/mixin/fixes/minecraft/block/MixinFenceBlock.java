/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.*;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public abstract class MixinFenceBlock extends HorizontalConnectingBlock {

    @Unique
    private static final VoxelShape viaFabricPlus$shape_b1_8_1 = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    @Unique
    private VoxelShape[] viaFabricPlus$collision_shape_r1_4_7;

    @Unique
    private VoxelShape[] viaFabricPlus$outline_shape_r1_4_7;

    protected MixinFenceBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void alwaysSuccess(CallbackInfoReturnable<ItemActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_10)) {
            cir.setReturnValue(ItemActionResult.SUCCESS);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init1_4_7Shapes(Settings settings, CallbackInfo ci) {
        this.viaFabricPlus$collision_shape_r1_4_7 = this.viaFabricPlus$createShapes1_4_7(24.0F);
        this.viaFabricPlus$outline_shape_r1_4_7 = this.viaFabricPlus$createShapes1_4_7(16.0F);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return VoxelShapes.fullCube();
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            return this.viaFabricPlus$outline_shape_r1_4_7[this.getShapeIndex(state)];
        } else {
            return super.getOutlineShape(state, world, pos, context);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return viaFabricPlus$shape_b1_8_1;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            return this.viaFabricPlus$collision_shape_r1_4_7[this.getShapeIndex(state)];
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
        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0, f, g, height, g);
        final VoxelShape northShape = Block.createCuboidShape(h, (float) 0.0, 0.0, i, height, i);
        final VoxelShape southShape = Block.createCuboidShape(h, (float) 0.0, h, i, height, 16.0);
        final VoxelShape westShape = Block.createCuboidShape(0.0, (float) 0.0, h, i, height, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, (float) 0.0, h, 16.0, height, i);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
                VoxelShapes.empty(),
                Block.createCuboidShape(f, (float) 0.0, h, g, height, 16.0D),
                Block.createCuboidShape(0.0D, (float) 0.0, f, i, height, g),
                Block.createCuboidShape(f - 6, (float) 0.0, h, g, height, 16.0D),
                Block.createCuboidShape(f, (float) 0.0, 0.0D, g, height, i),

                VoxelShapes.union(southShape, northShape),
                Block.createCuboidShape(f - 6, (float) 0.0, 0.0D, g, height, i),
                Block.createCuboidShape(f - 6, (float) 0.0, h - 5, g, height, 16.0D),
                Block.createCuboidShape(h, (float) 0.0, f, 16.0D, height, g),
                Block.createCuboidShape(h, (float) 0.0, f, 16.0D, height, g + 6),

                VoxelShapes.union(westShape, eastShape),
                Block.createCuboidShape(h - 5, (float) 0.0, f, 16.0D, height, g + 6),
                Block.createCuboidShape(f, (float) 0.0, 0.0D, g + 6, height, i),
                Block.createCuboidShape(f, (float) 0.0, 0.0D, g + 6, height, i + 5),
                Block.createCuboidShape(h - 5, (float) 0.0, f - 6, 16.0D, height, g),
                Block.createCuboidShape(0, (float) 0.0, 0, 16.0D, height, 16.0D)
        };

        for (int j = 0; j < 16; ++j) {
            voxelShapes[j] = VoxelShapes.union(baseShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

}
