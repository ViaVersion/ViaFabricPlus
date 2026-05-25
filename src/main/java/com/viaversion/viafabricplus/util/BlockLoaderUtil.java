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

package com.viaversion.viafabricplus.util;

import com.viaversion.viafabricplus.injection.access.registry.IHolderReference;
import com.viaversion.viafabricplus.injection.access.registry.IIdMapper;
import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import com.viaversion.viaversion.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockLoaderUtil {
    public static Map<String, BlockState> ID_TO_STATE = new HashMap<>();
    private static Map<BlockState, String> STATE_TO_MODEL;
    private static Map<BlockState, SingleVariant> QUEUED_MODEL;
    private static List<Block> BLOCK_TO_UNREGISTER = new ArrayList<>();

    public static Map<BlockState, String> modelsToResolve() {
        return STATE_TO_MODEL == null ? null : Collections.unmodifiableMap(STATE_TO_MODEL);
    }

    public static void invalidateModelsToResolve() {
        STATE_TO_MODEL = null;
    }

    public static Map<BlockState, SingleVariant> queuedModels() {
        return QUEUED_MODEL == null ? null : Collections.unmodifiableMap(QUEUED_MODEL);
    }

    public static void invalidateQueuedModels() {
        STATE_TO_MODEL = null;
    }

    public static void queue(BlockState state, SingleVariant variant) {
        if (QUEUED_MODEL == null) {
            QUEUED_MODEL = new HashMap<>();
        }

        QUEUED_MODEL.put(state, variant);
    }

    public static void loadModels(Map<BlockState, String> states) {
        STATE_TO_MODEL = states;

        Minecraft.getInstance().reloadResourcePacks();
    }

    public static void unregisterAll() {
        for (Block block : BLOCK_TO_UNREGISTER) {
            ((IMappedRegistry)BuiltInRegistries.BLOCK).viaFabricPlus$unregister(block.builtInRegistryHolder().key());

            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                ((IIdMapper)Block.BLOCK_STATE_REGISTRY).viaFabricPlus$unregisterState(state);
            }
        }

        BLOCK_TO_UNREGISTER.clear();
    }

    public static void register(List<Pair<ResourceKey<@NotNull Block>, Block>> blocks) {
        for (Pair<ResourceKey<@NotNull Block>, Block> pair : blocks) {
            Registry.register(BuiltInRegistries.BLOCK, pair.key(), pair.value());

            // This has to be done manually.
            for (BlockState state : pair.value().getStateDefinition().getPossibleStates()) {
                Block.BLOCK_STATE_REGISTRY.add(state);
                state.initCache();
            }

            // This fixes crashes with tags because of how we're registering blocks.
            ((IHolderReference)pair.value().builtInRegistryHolder()).viaFabricPlus$resolveTags();

            BLOCK_TO_UNREGISTER.add(pair.value());
        }
    }

    public static ResourceKey<Block> key(String identifier) {
        return ResourceKey.create(Registries.BLOCK, Identifier.parse(identifier));
    }
}
