package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaneBlock.class)
public abstract class MixinPaneBlock extends HorizontalConnectingBlock {

    @Unique
    private VoxelShape[] collisionShapes1_8;

    @Unique
    private VoxelShape[] boundingShapes1_8;

    protected MixinPaneBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes(Settings settings, CallbackInfo ci) {
        this.collisionShapes1_8 = this.createShapes1_8(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
        this.boundingShapes1_8 = this.createShapes1_8(1.0F, 1.0F, 16.0F, 0.0F, 16.0F);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return this.boundingShapes1_8[this.getShapeIndex(state)];
        }

        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return this.collisionShapes1_8[this.getShapeIndex(state)];
        }

        return super.getCollisionShape(state, world, pos, context);
    }

    @Unique
    private VoxelShape[] createShapes1_8(float radius1, float radius2, float height1, float offset2, float height2) {
        final float f = 8.0F - radius1;
        final float g = 8.0F + radius1;
        final float h = 8.0F - radius2;
        final float i = 8.0F + radius2;
        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0, f, g, height1, g);
        final VoxelShape northShape = Block.createCuboidShape(h, offset2, 0.0, i, height2, i);
        final VoxelShape southShape = Block.createCuboidShape(h, offset2, h, i, height2, 16.0);
        final VoxelShape westShape = Block.createCuboidShape(0.0, offset2, h, i, height2, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, offset2, h, 16.0, height2, i);
        final VoxelShape northEastCornerShape = VoxelShapes.union(northShape, eastShape);
        final VoxelShape southWestCornerShape = VoxelShapes.union(southShape, westShape);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
                VoxelShapes.empty(),
                Block.createCuboidShape(h, offset2, h + 1, i, height2, 16.0D), // south
                Block.createCuboidShape(0.0D, offset2, h, i - 1, height2, i), // west
                southWestCornerShape,
                Block.createCuboidShape(h, offset2, 0.0D, i, height2, i - 1), // north
                VoxelShapes.union(southShape, northShape),
                VoxelShapes.union(westShape, northShape),
                VoxelShapes.union(southWestCornerShape, northShape),
                Block.createCuboidShape(h + 1, offset2, h, 16.0D, height2, i), // east
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
            voxelShapes[j] = VoxelShapes.union(baseShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

}
