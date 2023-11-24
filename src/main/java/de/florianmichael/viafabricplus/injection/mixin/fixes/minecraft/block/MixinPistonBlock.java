package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.ShapeContext;
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

@Mixin(PistonBlock.class)
public abstract class MixinPistonBlock extends FacingBlock {

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_DOWN_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_UP_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_NORTH_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_SOUTH_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_WEST_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape EXTENDED_EAST_SHAPE;

    @Unique
    private static final VoxelShape _1_1_SHAPE = VoxelShapes.fullCube();

    protected MixinPistonBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void changeShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_1)) {
            cir.setReturnValue(_1_1_SHAPE);
        }
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_1)) {
            if (state.get(PistonBlock.EXTENDED)) {
                return switch (state.get(FACING)) {
                    case DOWN -> EXTENDED_DOWN_SHAPE;
                    case UP -> EXTENDED_UP_SHAPE;
                    case NORTH -> EXTENDED_NORTH_SHAPE;
                    case SOUTH -> EXTENDED_SOUTH_SHAPE;
                    case WEST -> EXTENDED_WEST_SHAPE;
                    case EAST -> EXTENDED_EAST_SHAPE;
                };
            } else {
                return VoxelShapes.fullCube();
            }
        }

        return super.getCullingShape(state, world, pos);
    }

}
