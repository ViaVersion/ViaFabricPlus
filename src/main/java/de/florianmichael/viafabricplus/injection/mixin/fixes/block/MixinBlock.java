package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {

    @Inject(method = "getBlastResistance", at = @At("RETURN"), cancellable = true)
    private void modifyBlastResistance(CallbackInfoReturnable<Float> ci) {
        final Block block = ((Block) (Object) this);

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            if (block == Blocks.END_STONE_BRICKS || block == Blocks.END_STONE_BRICK_SLAB || block == Blocks.END_STONE_BRICK_STAIRS || block == Blocks.END_STONE_BRICK_WALL) {
                ci.setReturnValue(0.8F);
            }
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD) {
                ci.setReturnValue(0.5F);
            }
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            if (block instanceof InfestedBlock) {
                ci.setReturnValue(0.75F);
            }
        }
    }
}
