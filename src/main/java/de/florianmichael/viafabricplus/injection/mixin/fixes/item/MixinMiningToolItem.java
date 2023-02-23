package de.florianmichael.viafabricplus.injection.mixin.fixes.item;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MiningToolItem.class)
public class MixinMiningToolItem {

    @Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
    public void changeHoeEffectiveBlocks(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        //noinspection ConstantValue
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2) && (Object) this instanceof HoeItem) {
            cir.setReturnValue(1.0F);
        }
    }
}
