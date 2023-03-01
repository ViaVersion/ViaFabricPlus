package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LilyPadBlock.class)
public class MixinLilyPadBlock {

    @Unique
    private static final VoxelShape viafabricplus_shape_v1_8_x = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 0.015625D /* 1 / 64 */ * 16, 16.0D);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void changeBoundingBox(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(viafabricplus_shape_v1_8_x);
        }
    }
}
