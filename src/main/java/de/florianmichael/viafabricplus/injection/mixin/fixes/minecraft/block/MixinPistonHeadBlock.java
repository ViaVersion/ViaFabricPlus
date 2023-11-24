package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHeadBlock.class)
public abstract class MixinPistonHeadBlock extends FacingBlock {

    @Shadow
    @Final
    protected static VoxelShape DOWN_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape UP_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape NORTH_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape SOUTH_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape WEST_HEAD_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EAST_HEAD_SHAPE;

    @Unique
    private static final VoxelShape _1_8_UP_ARM_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);

    @Unique
    private static final VoxelShape _1_8_DOWN_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);

    @Unique
    private static final VoxelShape _1_8_SOUTH_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 0.0, 12.0, 10.0, 12.0);

    @Unique
    private static final VoxelShape _1_8_NORTH_ARM_SHAPE = Block.createCuboidShape(4.0, 6.0, 4.0, 12.0, 10.0, 16.0);

    @Unique
    private static final VoxelShape _1_8_EAST_ARM_SHAPE = Block.createCuboidShape(0.0, 6.0, 4.0, 12.0, 10.0, 12.0);

    @Unique
    private static final VoxelShape _1_8_WEST_ARM_SHAPE = Block.createCuboidShape(6.0, 4.0, 4.0, 10.0, 12.0, 16.0);

    protected MixinPistonHeadBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(switch (state.get(PistonHeadBlock.FACING)) {
                case DOWN -> DOWN_HEAD_SHAPE;
                case UP -> UP_HEAD_SHAPE;
                case NORTH -> NORTH_HEAD_SHAPE;
                case SOUTH -> SOUTH_HEAD_SHAPE;
                case WEST -> WEST_HEAD_SHAPE;
                case EAST -> EAST_HEAD_SHAPE;
            });
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return switch (state.get(PistonHeadBlock.FACING)) {
                case DOWN -> VoxelShapes.union(DOWN_HEAD_SHAPE, _1_8_DOWN_ARM_SHAPE);
                case UP -> VoxelShapes.union(UP_HEAD_SHAPE, _1_8_UP_ARM_SHAPE);
                case NORTH -> VoxelShapes.union(NORTH_HEAD_SHAPE, _1_8_NORTH_ARM_SHAPE);
                case SOUTH -> VoxelShapes.union(SOUTH_HEAD_SHAPE, _1_8_SOUTH_ARM_SHAPE);
                case WEST -> VoxelShapes.union(WEST_HEAD_SHAPE, _1_8_WEST_ARM_SHAPE);
                case EAST -> VoxelShapes.union(EAST_HEAD_SHAPE, _1_8_EAST_ARM_SHAPE);
            };
        }

        return super.getCollisionShape(state, world, pos, context);
    }

}
