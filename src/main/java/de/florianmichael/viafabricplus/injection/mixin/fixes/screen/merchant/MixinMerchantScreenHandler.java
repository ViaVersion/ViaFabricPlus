package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.merchant;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.screen.MerchantScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreenHandler.class)
public class MixinMerchantScreenHandler {

    @Inject(method = "switchTo", at = @At("HEAD"), cancellable = true)
    private void injectSwitchTo(int recipeId, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            ci.cancel(); // no lmao?
        }
    }
}
