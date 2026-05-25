package com.viaversion.viafabricplus.injection.access.registry;

import net.minecraft.resources.ResourceKey;

public interface IMappedRegistry {
    void viaFabricPlus$unfreeze();
    void viaFabricPlus$refreeze();

    void viaFabricPlus$unregister(ResourceKey key);
}
