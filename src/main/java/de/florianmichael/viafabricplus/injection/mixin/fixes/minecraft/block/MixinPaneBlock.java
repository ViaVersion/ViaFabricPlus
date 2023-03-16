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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PaneBlock.class)
public class MixinPaneBlock extends HorizontalConnectingBlock {

    public MixinPaneBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getClassWrapper() != null && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return viafabricplus_get1_8Shape(state);
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getClassWrapper() != null && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return viafabricplus_get1_8Shape(state);
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Unique
    private final VoxelShape viafabricplus_west_shape_v1_8_x = Block.createCuboidShape(0, 0, 7, 8, 16, 9);

    @Unique
    private final VoxelShape viafabricplus_east_shape_v1_8_x = Block.createCuboidShape(8, 0, 7, 16, 16, 9);

    @Unique
    private final VoxelShape viafabricplus_west_east_combined_shape_v1_8_x = Block.createCuboidShape(0, 0, 7, 16, 16, 9);

    @Unique
    private final VoxelShape viafabricplus_north_shape_v1_8_x = Block.createCuboidShape(7, 0, 0, 9, 16, 8);

    @Unique
    private final VoxelShape viafabricplus_south_shape_v1_8_x = Block.createCuboidShape(7, 0, 8, 9, 16, 16);

    @Unique
    private final VoxelShape viafabricplus_north_south_combined_shape_v1_8_x = Block.createCuboidShape(7, 0, 0, 9, 16, 16);

    @Unique
    public VoxelShape viafabricplus_get1_8Shape(BlockState state) {
        VoxelShape finalShape = VoxelShapes.empty();

        final boolean isNorthFacing = state.get(NORTH);
        final boolean isSouthFacing = state.get(SOUTH);
        final boolean isWestFacing = state.get(WEST);
        final boolean isEastFacing = state.get(EAST);

        if ((!isWestFacing || !isEastFacing) && (isWestFacing || isEastFacing || isNorthFacing || isSouthFacing)) {
            if (isWestFacing)
                finalShape = VoxelShapes.union(finalShape, viafabricplus_west_shape_v1_8_x);
            else if (isEastFacing)
                finalShape = VoxelShapes.union(finalShape, viafabricplus_east_shape_v1_8_x);
        } else
            finalShape = VoxelShapes.union(finalShape, viafabricplus_west_east_combined_shape_v1_8_x);

        if ((!isNorthFacing || !isSouthFacing) && (isWestFacing || isEastFacing || isNorthFacing || isSouthFacing)) {
            if (isNorthFacing)
                finalShape = VoxelShapes.union(finalShape, viafabricplus_north_shape_v1_8_x);
            else if (isSouthFacing)
                finalShape = VoxelShapes.union(finalShape, viafabricplus_south_shape_v1_8_x);
        } else
            finalShape = VoxelShapes.union(finalShape, viafabricplus_north_south_combined_shape_v1_8_x);

        return finalShape;
    }
}
