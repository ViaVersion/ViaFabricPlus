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
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return protocolhack_get1_8Shape(state);
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return protocolhack_get1_8Shape(state);
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
    public VoxelShape protocolhack_get1_8Shape(BlockState state) {
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
