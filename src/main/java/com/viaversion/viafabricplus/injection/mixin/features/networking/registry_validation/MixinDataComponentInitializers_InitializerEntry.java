package com.viaversion.viafabricplus.injection.mixin.features.networking.registry_validation;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.core.component.DataComponentInitializers$InitializerEntry")
public interface MixinDataComponentInitializers_InitializerEntry {

    @Invoker("run")
    void viaFabricPlus$run(DataComponentMap.Builder components, HolderLookup.Provider context);

}
