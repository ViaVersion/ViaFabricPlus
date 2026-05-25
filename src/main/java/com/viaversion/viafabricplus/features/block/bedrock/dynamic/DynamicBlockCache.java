/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.features.block.bedrock.dynamic;

import com.viaversion.viafabricplus.injection.access.registry.IHolderReference;
import com.viaversion.viafabricplus.injection.access.registry.IIdMapper;
import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import com.viaversion.viaversion.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class data needed to load/unload blocks dynamically, include the actual blocks and models.
 */
public class DynamicBlockCache {

    /**
     * General block translation related part.
     */

    // Cache of a list of registered blocks so we can unregister them later
    private static final List<Block> REGISTERED_BLOCKS = new ArrayList<>();

    // Helps to map the custom blocks to the one we register in ViaBedrock.
    private static final Map<String, BlockState> KEY_TO_STATE = new HashMap<>();
    public static Integer blockKeyToId(String key)  {
        return Block.getId(KEY_TO_STATE.get(key));
    }
    public static void putKeyToState(String key, BlockState state)  {
        KEY_TO_STATE.put(key, state);
    }

    /**
     * Block model cache, baking related part.
     */

    // A map of models that we need to bake for block states so it can be used later.
    private static Map<BlockState, String> MODELS_TO_BAKE;

    public static void requestBakeModelsAndLoad(Map<BlockState, String> states) {
        MODELS_TO_BAKE = states;

        // Manually reload texture packs to trigger block model bake/set.
        if (MODELS_TO_BAKE != null && !MODELS_TO_BAKE.isEmpty()) {
            Minecraft.getInstance().reloadResourcePacks();
        }
    }

    // A map of baked model that should be mapped to a block state.
    private static Map<BlockState, SingleVariant> STATES_TO_MODEL = new HashMap<>();

    public static void mapBlockStateToBakedModel(Map<BlockState, BlockStateModel> models) {
        models.putAll(STATES_TO_MODEL);

        STATES_TO_MODEL.clear();
    }

    public static void bakeModels(ModelBaker baker) {
        if (MODELS_TO_BAKE == null) {
            return;
        }

        for (Map.Entry<BlockState, String> entry : MODELS_TO_BAKE.entrySet()) {
            final String model = entry.getValue();

            CuboidModel cuboidModel = CuboidModel.fromStream(new StringReader(model));
            TextureSlots.Resolver resolver = new TextureSlots.Resolver();
            resolver.addFirst(cuboidModel.textureSlots());

            QuadCollection quadCollection = cuboidModel.geometry().bake(resolver.resolve(() -> ""), baker, BlockModelRotation.IDENTITY, () -> "");

            // This is only ever used for block particles, we can translate it but for now just set it to STONE.
            final Material.Baked baked = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(Blocks.STONE.defaultBlockState());
            STATES_TO_MODEL.put(entry.getKey(), new SingleVariant(new SimpleModelWrapper(quadCollection, Boolean.TRUE.equals(cuboidModel.ambientOcclusion()), baked)));
        }

        MODELS_TO_BAKE = null;
    }

    /**
     * Other utility methods.
     */

    @SuppressWarnings("deprecation")
    public static void register(List<Pair<ResourceKey<@NotNull Block>, Block>> blocks) {
        for (Pair<ResourceKey<@NotNull Block>, Block> pair : blocks) {
            Registry.register(BuiltInRegistries.BLOCK, pair.key(), pair.value());

            // This has to be done manually.
            for (BlockState state : pair.value().getStateDefinition().getPossibleStates()) {
                Block.BLOCK_STATE_REGISTRY.add(state);
                state.initCache();
            }

            // This fixes crashes with tags because we're not re-freezing registry properly, this is actually a better a way to do it.
            ((IHolderReference<?>)pair.value().builtInRegistryHolder()).viaFabricPlus$resolveTags();

            REGISTERED_BLOCKS.add(pair.value());
        }
    }

    public static ResourceKey<@NotNull Block> key(String identifier) {
        return ResourceKey.create(Registries.BLOCK, Identifier.parse(identifier));
    }

    @SuppressWarnings("deprecation")
    public static void clear() {
        for (Block block : REGISTERED_BLOCKS) {
            ((IMappedRegistry)BuiltInRegistries.BLOCK).viaFabricPlus$unregister(block.builtInRegistryHolder().key());

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ((IIdMapper)Block.BLOCK_STATE_REGISTRY).viaFabricPlus$unregisterState(state);
            }
        }

        REGISTERED_BLOCKS.clear();
        KEY_TO_STATE.clear();

        MODELS_TO_BAKE = null;
        STATES_TO_MODEL.clear();
    }
}
