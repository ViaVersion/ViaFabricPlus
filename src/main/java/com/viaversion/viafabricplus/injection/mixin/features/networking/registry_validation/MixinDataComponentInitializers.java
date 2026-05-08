package com.viaversion.viafabricplus.injection.mixin.features.networking.registry_validation;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DataComponentInitializers.class)
public abstract class MixinDataComponentInitializers {

    @Redirect(method = "runInitializers", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentInitializers$InitializerEntry;run(Lnet/minecraft/core/component/DataComponentMap$Builder;Lnet/minecraft/core/HolderLookup$Provider;)V"))
    private void ignoreMissingRegistryElement(@Coerce Object initializer, DataComponentMap.Builder components, HolderLookup.Provider context) {
        try {
            ((MixinDataComponentInitializers_InitializerEntry) initializer).viaFabricPlus$run(components, context);
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            if (message == null || (!message.startsWith("Missing element ResourceKey[") && !message.startsWith("Missing tag TagKey["))) {
                throw e;
            }
        }
    }

}
