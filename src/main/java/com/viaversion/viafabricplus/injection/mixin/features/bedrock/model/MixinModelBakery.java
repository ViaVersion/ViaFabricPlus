package com.viaversion.viafabricplus.injection.mixin.features.bedrock.model;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import com.viaversion.viafabricplus.util.BlockLoaderUtil;
import com.viaversion.viafabricplus.util.block.DummyModelBaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public class MixinModelBakery {
    @Inject(at = @At(value = "HEAD"), method = "bakeModels", cancellable = true)
    private void bake(MaterialBaker materials, Executor taskExecutor, CallbackInfoReturnable<CompletableFuture<ModelBakery.BakingResult>> cir) {
        if (BlockLoaderUtil.modelsToResolve() == null) {
            return;
        }

        // We need to first bake the models to then replace it properly.
        final DummyModelBaker baker = new DummyModelBaker(materials, new ModelBakery.InternerImpl());

        for (Map.Entry<BlockState, String> entry : BlockLoaderUtil.modelsToResolve().entrySet()) {
            BlockState state = entry.getKey();
            String model = entry.getValue();

            CuboidModel cuboidModel = CuboidModel.fromStream(new StringReader(model));
            TextureSlots.Resolver resolver = new TextureSlots.Resolver();
            resolver.addFirst(cuboidModel.textureSlots());

            QuadCollection quadCollection = cuboidModel.geometry().bake(resolver.resolve(() -> ""), baker, BlockModelRotation.IDENTITY, () -> "");

            // This is actually only used for particle, so we can just whatever. TODO: Implement this properly later, probably never since it's a pain.
            final Material.Baked baked = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(Blocks.STONE.defaultBlockState());

            BlockLoaderUtil.queue(state, new SingleVariant(new SimpleModelWrapper(quadCollection, Boolean.TRUE.equals(cuboidModel.ambientOcclusion()), baked)));
        }

        BlockLoaderUtil.invalidateModelsToResolve();
    }
}
