package com.viaversion.viafabricplus.injection.mixin.core.access;

import com.mojang.serialization.Lifecycle;
import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(MappedRegistry.class)
public class MixinMappedRegistry<T> implements IMappedRegistry {
    @Shadow
    @Final
    private ObjectList<Holder.Reference<T>> byId;

    @Shadow
    @Final
    private Reference2IntMap<T> toId;

    @Shadow
    @Final
    private Map<Identifier, Holder.Reference<T>> byLocation;

    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;

    @Shadow
    @Final
    private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

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

    @Override
    public void viaFabricPlus$unregister(final ResourceKey key) {
        Holder.Reference<@NotNull T> reference = this.byKey.remove(key);
        this.byLocation.remove(key.identifier());
        this.byValue.remove(reference.value());
        this.byId.remove(reference);
        this.toId.remove(reference.value());
        this.registrationInfos.remove(key);
    }
}
