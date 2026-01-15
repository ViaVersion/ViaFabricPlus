package com.viaversion.viafabricplus.injection.mixin.features.movement.constants.infinity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkPos.class)
public abstract class MixinChunkPos {
    @WrapMethod(method = "isValid(II)Z")
    private static boolean isValidAlways(final int cx, final int cz, final Operation<Boolean> original) {
        return original.call(cx, cz) || GeneralSettings.INSTANCE.experimentalFarlandsDistance.getValue();
    }
}
