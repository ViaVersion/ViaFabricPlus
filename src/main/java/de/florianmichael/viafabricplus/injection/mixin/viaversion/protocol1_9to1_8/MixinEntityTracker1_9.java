package de.florianmichael.viafabricplus.injection.mixin.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

// Copyright RaphiMC/RK_01 - GPL v3 LICENSE
@Mixin(value = EntityTracker1_9.class, remap = false)
public abstract class MixinEntityTracker1_9 {

    @Redirect(method = "handleMetadata", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z")))
    private float removeMin(float a, float b) {
        return a;
    }

    @Redirect(method = "handleMetadata", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z")))
    private float removeMax(float a, float b) {
        return b;
    }

    @Redirect(method = "handleMetadata", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/minecraft/metadata/Metadata;getValue()Ljava/lang/Object;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z")))
    private Object remapNaNToZero(Metadata instance) {
        if (instance.getValue() instanceof Float && ((Float) instance.getValue()).isNaN()) {
            return 0F;
        }

        return instance.getValue();
    }

}
