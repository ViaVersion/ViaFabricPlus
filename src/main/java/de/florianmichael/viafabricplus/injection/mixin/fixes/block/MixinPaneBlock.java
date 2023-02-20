/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

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
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return protocolhack_get1_8Shape(state);
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return protocolhack_get1_8Shape(state);
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Unique
    private final VoxelShape protocolhack_WEST_SHAPE_1_8 = Block.createCuboidShape(0, 0, 7, 8, 16, 9);

    @Unique
    private final VoxelShape protocolhack_EAST_SHAPE_1_8 = Block.createCuboidShape(8, 0, 7, 16, 16, 9);

    @Unique
    private final VoxelShape protocolhack_WEST_EAST_COMBINED_SHAPE_1_8 = Block.createCuboidShape(0, 0, 7, 16, 16, 9);

    @Unique
    private final VoxelShape protocolhack_NORTH_SHAPE_1_8 = Block.createCuboidShape(7, 0, 0, 9, 16, 8);

    @Unique
    private final VoxelShape protocolhack_SOUTH_SHAPE_1_8 = Block.createCuboidShape(7, 0, 8, 9, 16, 16);

    @Unique
    private final VoxelShape protocolhack_NORTH_SOUTH_COMBINED_SHAPE_1_8 = Block.createCuboidShape(7, 0, 0, 9, 16, 16);

    @Unique
    public VoxelShape protocolhack_get1_8Shape(BlockState state) {
        VoxelShape finalShape = VoxelShapes.empty();

        final boolean isNorthFacing = state.get(NORTH);
        final boolean isSouthFacing = state.get(SOUTH);
        final boolean isWestFacing = state.get(WEST);
        final boolean isEastFacing = state.get(EAST);

        if ((!isWestFacing || !isEastFacing) && (isWestFacing || isEastFacing || isNorthFacing || isSouthFacing)) {
            if (isWestFacing)
                finalShape = VoxelShapes.union(finalShape, protocolhack_WEST_SHAPE_1_8);
            else if (isEastFacing)
                finalShape = VoxelShapes.union(finalShape, protocolhack_EAST_SHAPE_1_8);
        } else
            finalShape = VoxelShapes.union(finalShape, protocolhack_WEST_EAST_COMBINED_SHAPE_1_8);

        if ((!isNorthFacing || !isSouthFacing) && (isWestFacing || isEastFacing || isNorthFacing || isSouthFacing)) {
            if (isNorthFacing)
                finalShape = VoxelShapes.union(finalShape, protocolhack_NORTH_SHAPE_1_8);
            else if (isSouthFacing)
                finalShape = VoxelShapes.union(finalShape, protocolhack_SOUTH_SHAPE_1_8);
        } else
            finalShape = VoxelShapes.union(finalShape, protocolhack_NORTH_SOUTH_COMBINED_SHAPE_1_8);

        return finalShape;
    }
}
