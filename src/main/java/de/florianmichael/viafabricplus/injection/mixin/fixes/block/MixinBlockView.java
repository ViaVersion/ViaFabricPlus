package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockView.class)
public interface MixinBlockView {

    @Redirect(method = "raycastBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getRaycastShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"))
    default VoxelShape hookedGetRaycastShape(BlockState instance, BlockView blockView, BlockPos blockPos) {
        VoxelShape shape = instance.getRaycastShape(blockView, blockPos);
        // It appears, that certain game states react unstable to the shape changes we are producing.
        if (shape == null)
            shape = VoxelShapes.empty();
        return shape;
    }
}
