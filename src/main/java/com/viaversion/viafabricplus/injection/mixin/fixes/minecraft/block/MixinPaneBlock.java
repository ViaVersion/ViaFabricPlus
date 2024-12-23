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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaneBlock.class)
public abstract class MixinPaneBlock extends HorizontalConnectingBlock {

    @Unique
    private VoxelShape[] viaFabricPlus$shape_r1_8;

    protected MixinPaneBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes1_8(Settings settings, CallbackInfo ci) {
        final float f = 7.0F;
        final float g = 9.0F;
        final float h = 7.0F;
        final float i = 9.0F;

        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0, f, g, (float) 16.0, g);
        final VoxelShape northShape = Block.createCuboidShape(h, (float) 0.0, 0.0, i, (float) 16.0, i);
        final VoxelShape southShape = Block.createCuboidShape(h, (float) 0.0, h, i, (float) 16.0, 16.0);
        final VoxelShape westShape = Block.createCuboidShape(0.0, (float) 0.0, h, i, (float) 16.0, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, (float) 0.0, h, 16.0, (float) 16.0, i);

        final VoxelShape northEastCornerShape = VoxelShapes.union(northShape, eastShape);
        final VoxelShape southWestCornerShape = VoxelShapes.union(southShape, westShape);

        viaFabricPlus$shape_r1_8 = new VoxelShape[] {
                VoxelShapes.empty(),
                Block.createCuboidShape(h, (float) 0.0, h + 1, i, (float) 16.0, 16.0D), // south
                Block.createCuboidShape(0.0D, (float) 0.0, h, i - 1, (float) 16.0, i), // west
                southWestCornerShape,
                Block.createCuboidShape(h, (float) 0.0, 0.0D, i, (float) 16.0, i - 1), // north
                VoxelShapes.union(southShape, northShape),
                VoxelShapes.union(westShape, northShape),
                VoxelShapes.union(southWestCornerShape, northShape),
                Block.createCuboidShape(h + 1, (float) 0.0, h, 16.0D, (float) 16.0, i), // east
                VoxelShapes.union(southShape, eastShape),
                VoxelShapes.union(westShape, eastShape),
                VoxelShapes.union(southWestCornerShape, eastShape),
                northEastCornerShape,
                VoxelShapes.union(southShape, northEastCornerShape),
                VoxelShapes.union(westShape, northEastCornerShape),
                VoxelShapes.union(southWestCornerShape, northEastCornerShape)
        };

        for (int j = 0; j < 16; ++j) {
            if (j == 1 || j == 2 || j == 4 || j == 8) continue;
            viaFabricPlus$shape_r1_8[j] = VoxelShapes.union(baseShape, viaFabricPlus$shape_r1_8[j]);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return this.viaFabricPlus$shape_r1_8[this.getShapeIndex(state)];
        } else {
            return super.getOutlineShape(state, world, pos, context);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return this.viaFabricPlus$shape_r1_8[this.getShapeIndex(state)];
        } else {
            return super.getCollisionShape(state, world, pos, context);
        }
    }

}
