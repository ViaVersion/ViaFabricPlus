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

import com.viaversion.viafabricplus.injection.access.block.shape.IHorizontalConnectingBlock;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronBarsBlock.class)
public abstract class MixinPaneBlock extends CrossCollisionBlock implements IHorizontalConnectingBlock {

    @Unique
    private VoxelShape[] viaFabricPlus$shape_r1_12_2;

    @Unique
    private VoxelShape[] viaFabricPlus$shape_r1_8;

    protected MixinPaneBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Properties settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes1_8(Properties settings, CallbackInfo ci) {
        final float f = 7.0F;
        final float g = 9.0F;
        final float h = 7.0F;
        final float i = 9.0F;

        final VoxelShape baseShape = Block.box(f, 0.0, f, g, (float) 16.0, g);

        viaFabricPlus$shape_r1_12_2 = new VoxelShape[]{
            baseShape,
            Block.box(h, 0.0, h, i, 16.0, 16.0), // south
            Block.box(0.0, 0.0, h, i, 16.0, i), // west
            Block.box(0.0, 0.0, h, i, 16.0, 16.0), // south-west corner
            Block.box(h, 0.0, 0.0, i, 16.0, i), // north
            Block.box(h, 0.0, 0.0, i, 16.0, 16.0), // south-north line
            Block.box(0.0, 0.0, 0.0, i, 16.0, i), // west-north corner
            Block.box(0.0, 0.0, 0.0, i, 16.0, 16.0), // south-west-north T
            Block.box(h, 0.0, h, 16.0, 16.0, i), // east
            Block.box(h, 0.0, h, 16.0, 16.0, 16.0), // south-east corner
            Block.box(0.0, 0.0, h, 16.0, 16.0, i), // west-east line
            Block.box(0.0, 0.0, h, 16.0, 16.0, 16.0), // south-west-east T
            Block.box(h, 0.0, 0.0, 16.0, 16.0, i), // north-east corner
            Block.box(h, 0.0, 0.0, 16.0, 16.0, 16.0), // south-north-east T
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, i), // west-north-east T
            Shapes.block() // cross
        };

        final VoxelShape northShape = Block.box(h, (float) 0.0, 0.0, i, (float) 16.0, i - 1);
        final VoxelShape southShape = Block.box(h, (float) 0.0, h + 1, i, (float) 16.0, 16.0);
        final VoxelShape westShape = Block.box(0.0, (float) 0.0, h, i - 1, (float) 16.0, i);
        final VoxelShape eastShape = Block.box(h + 1, (float) 0.0, h, 16.0, (float) 16.0, i);

        final VoxelShape northEastCornerShape = Shapes.or(northShape, eastShape);
        final VoxelShape southWestCornerShape = Shapes.or(southShape, westShape);

        viaFabricPlus$shape_r1_8 = new VoxelShape[]{
            baseShape,
            southShape,
            westShape,
            southWestCornerShape,
            northShape,
            Shapes.or(southShape, northShape),
            Shapes.or(westShape, northShape),
            Shapes.or(southWestCornerShape, northShape),
            eastShape,
            Shapes.or(southShape, eastShape),
            Shapes.or(westShape, eastShape),
            Shapes.or(southWestCornerShape, eastShape),
            northEastCornerShape,
            Shapes.or(southShape, northEastCornerShape),
            Shapes.or(westShape, northEastCornerShape),
            Shapes.or(southWestCornerShape, northEastCornerShape)
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (DebugSettings.INSTANCE.legacyPaneOutlines.isEnabled()) {
            return this.viaFabricPlus$shape_r1_12_2[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getShape(state, world, pos, context);
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return this.viaFabricPlus$shape_r1_8[this.viaFabricPlus$getShapeIndex(state)];
        } else {
            return super.getCollisionShape(state, world, pos, context);
        }
    }

}
