package com.viaversion.viafabricplus.injection.mixin.features.movement.constants.infinity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public abstract class MixinLevel {
    @ModifyExpressionValue(method = "isInWorldBoundsHorizontal", at = {@At(value = "CONSTANT", args = "intValue=-30000000"), @At(value = "CONSTANT", args = "intValue=30000000")})
    private static int modifyMaxDist(final int original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return original < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        } else {
            return original;
        }
    }

    @ModifyExpressionValue(method = "getHeight", at = {@At(value = "CONSTANT", args = "intValue=-30000000"), @At(value = "CONSTANT", args = "intValue=30000000")})
    private int modifyMaxDistHeight(final int original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return original < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        } else {
            return original;
        }
    }
}
