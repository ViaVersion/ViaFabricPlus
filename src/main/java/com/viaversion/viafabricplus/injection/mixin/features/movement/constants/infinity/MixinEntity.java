package com.viaversion.viafabricplus.injection.mixin.features.movement.constants.infinity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @WrapOperation(method = "absSnapTo(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private double dontClampSnapPos(final double value, final double min, final double max, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return value;
        } else {
            return original.call(value, min, max);
        }
    }

    @WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(DDD)D"))
    private double dontClampLoadPos(final double value, final double min, final double max, final Operation<Double> original) {
        if (GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            return value;
        } else {
            return original.call(value, min, max);
        }
    }
}
