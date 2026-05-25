package com.viaversion.viafabricplus.injection.mixin.features.bedrock.model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.DynamicBlockCache;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.baker.DummyModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public class MixinModelBakery {
    @Inject(at = @At(value = "HEAD"), method = "bakeModels")
    private void bakeDynamicModels(MaterialBaker materials, Executor taskExecutor, CallbackInfoReturnable<CompletableFuture<ModelBakery.BakingResult>> cir) {
        DynamicBlockCache.bakeModels(new DummyModelBaker(materials, new ModelBakery.InternerImpl()));
    }
}
