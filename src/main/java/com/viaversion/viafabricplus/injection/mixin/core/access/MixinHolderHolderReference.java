package com.viaversion.viafabricplus.injection.mixin.core.access;

import java.util.HashSet;
import java.util.Set;
import com.viaversion.viafabricplus.injection.access.registry.IHolderReference;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.Reference.class)
public class MixinHolderHolderReference<T> implements IHolderReference<T> {
    @Shadow
    private @Nullable Set<TagKey<T>> tags;

    @Override
    public void viaFabricPlus$resolveTags() {
        this.tags = new HashSet<>();
    }
}
