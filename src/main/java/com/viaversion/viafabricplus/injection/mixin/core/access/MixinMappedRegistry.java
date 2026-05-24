package com.viaversion.viafabricplus.injection.mixin.core.access;

import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(MappedRegistry.class)
public class MixinMappedRegistry<T> implements IMappedRegistry {
    @Shadow
    private @Nullable Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    @Shadow
    private boolean frozen;

    @Override
    public void viaFabricPlus$unfreeze() {
        this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
        this.frozen = false;
    }

    @Override
    public void viaFabricPlus$refreeze() {
        this.unregisteredIntrusiveHolders = null;
        this.frozen = true;
    }
}
