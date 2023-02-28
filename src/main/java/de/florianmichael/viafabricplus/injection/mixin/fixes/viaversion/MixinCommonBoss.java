package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.legacy.bossbar.CommonBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CommonBoss.class, remap = false)
public class MixinCommonBoss {

    @Redirect(method = "setHealth", at = @At(value = "INVOKE", target = "Lcom/google/common/base/Preconditions;checkArgument(ZLjava/lang/Object;)V"))
    public void ignoreHealthCheck(boolean expression, Object errorMessage) {
    }
}
