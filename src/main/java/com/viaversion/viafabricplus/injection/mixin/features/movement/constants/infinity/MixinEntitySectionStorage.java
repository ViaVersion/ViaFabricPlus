package com.viaversion.viafabricplus.injection.mixin.features.movement.constants.infinity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntitySectionStorage.class)
public abstract class MixinEntitySectionStorage {
    @WrapMethod(method = "forEachAccessibleNonEmptySection")
    private <T extends EntityAccess> void noopRendering(final AABB aABB, final AbortableIterationConsumer<EntitySection<T>> abortableIterationConsumer, final Operation<Void> original) {
        if (!GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue()) {
            original.call(aABB, abortableIterationConsumer);
        }
    }
}
