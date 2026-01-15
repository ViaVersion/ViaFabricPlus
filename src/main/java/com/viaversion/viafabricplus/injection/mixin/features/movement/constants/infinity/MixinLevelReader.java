package com.viaversion.viafabricplus.injection.mixin.features.movement.constants.infinity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelReader.class)
public interface MixinLevelReader {
    @ModifyExpressionValue(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", at = {@At(value = "CONSTANT", args = "intValue=-30000000"), @At(value = "CONSTANT", args = "intValue=30000000")})
    private static int modifyMaxDist(final int original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return original < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        } else {
            return original;
        }
    }
}
