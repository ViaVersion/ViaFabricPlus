package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.*;
import net.minecraft.util.ActionResult;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public abstract class MixinFenceBlock extends HorizontalConnectingBlock {

    @Unique
    private VoxelShape[] collisionShapes1_4_7;

    @Unique
    private VoxelShape[] boundingShapes1_4_7;

    @Unique
    private static final VoxelShape _b1_8_1_OUTLINE_SHAPE = VoxelShapes.fullCube();

    @Unique
    private static final VoxelShape _b1_8_1_COLLISION_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    protected MixinFenceBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void modifyOnUse(CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_10)) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initShapes(Settings settings, CallbackInfo ci) {
        this.collisionShapes1_4_7 = this.createShapes1_4_7(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
        this.boundingShapes1_4_7 = this.createShapes1_4_7(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            return _b1_8_1_OUTLINE_SHAPE;
        } else if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_4_6tor1_4_7)) {
            return this.boundingShapes1_4_7[this.getShapeIndex(state)];
        }

        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            return _b1_8_1_COLLISION_SHAPE;
        } else if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_4_6tor1_4_7)) {
            return this.collisionShapes1_4_7[this.getShapeIndex(state)];
        }

        return super.getCollisionShape(state, world, pos, context);
    }

    @Unique
    private VoxelShape[] createShapes1_4_7(float radius1, float radius2, float height1, float offset2, float height2) {
        final float f = 8.0F - radius1;
        final float g = 8.0F + radius1;
        final float h = 8.0F - radius2;
        final float i = 8.0F + radius2;
        final VoxelShape baseShape = Block.createCuboidShape(f, 0.0, f, g, height1, g);
        final VoxelShape northShape = Block.createCuboidShape(h, offset2, 0.0, i, height2, i);
        final VoxelShape southShape = Block.createCuboidShape(h, offset2, h, i, height2, 16.0);
        final VoxelShape westShape = Block.createCuboidShape(0.0, offset2, h, i, height2, i);
        final VoxelShape eastShape = Block.createCuboidShape(h, offset2, h, 16.0, height2, i);
        final VoxelShape[] voxelShapes = new VoxelShape[]{
                VoxelShapes.empty(),
                Block.createCuboidShape(f, offset2, h, g, height1, 16.0D),
                Block.createCuboidShape(0.0D, offset2, f, i, height1, g),
                Block.createCuboidShape(f - 6, offset2, h, g, height1, 16.0D),
                Block.createCuboidShape(f, offset2, 0.0D, g, height1, i),
                VoxelShapes.union(southShape, northShape),
                Block.createCuboidShape(f - 6, offset2, 0.0D, g, height1, i),
                Block.createCuboidShape(f - 6, offset2, h - 5, g, height1, 16.0D),
                Block.createCuboidShape(h, offset2, f, 16.0D, height1, g),
                Block.createCuboidShape(h, offset2, f, 16.0D, height1, g + 6),
                VoxelShapes.union(westShape, eastShape),
                Block.createCuboidShape(h - 5, offset2, f, 16.0D, height1, g + 6),
                Block.createCuboidShape(f, offset2, 0.0D, g + 6, height1, i),
                Block.createCuboidShape(f, offset2, 0.0D, g + 6, height1, i + 5),
                Block.createCuboidShape(h - 5, offset2, f - 6, 16.0D, height1, g),
                Block.createCuboidShape(0, offset2, 0, 16.0D, height1, 16.0D)
        };

        for (int j = 0; j < 16; ++j) {
            voxelShapes[j] = VoxelShapes.union(baseShape, voxelShapes[j]);
        }

        return voxelShapes;
    }

}
