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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.rewriter;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.DynamicBlockCache;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.block.CustomBlock;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.custom.BlockComponentsTranslator;
import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.raphimc.viabedrock.api.model.BedrockBlockState;
import net.raphimc.viabedrock.protocol.model.BlockProperties;
import net.raphimc.viabedrock.protocol.rewriter.BlockStateRewriter;
import org.cube.converter.converter.enums.RotationType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(BlockStateRewriter.class)
public class MixinBlockStateRewriter {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/viaversion/nbt/tag/CompoundTag;putInt(Ljava/lang/String;I)V"))
    public void pullMoreComponents(CompoundTag instance, String tagName, int value, @Local Map.Entry<String, CompoundTag> blockProperty) {
        instance.putInt(tagName, value);
        final CompoundTag tag = blockProperty.getValue();

        // Add more data into block state tag so we can read it later.
        final CompoundTag components = tag.getCompoundTag("components");
        if (components != null) {
            if (components.contains("minecraft:collision_box")) {
                instance.put("minecraft:collision_box", components.getCompoundTag("minecraft:collision_box"));
            }

            if (components.contains("minecraft:selection_box")) {
                instance.put("minecraft:selection_box", components.getCompoundTag("minecraft:selection_box"));
            }

            if (components.contains("minecraft:geometry")) {
                instance.put("minecraft:geometry", components.getCompoundTag("minecraft:geometry"));
            }

            if (components.contains("minecraft:destructible_by_mining")) {
                instance.put("minecraft:destructible_by_mining", components.get("minecraft:destructible_by_mining"));
            }

            if (components.contains("minecraft:friction")) {
                instance.put("minecraft:friction", components.get("minecraft:friction"));
            }

            if (components.contains("minecraft:material_instances")) {
                instance.put("minecraft:material_instances", components.getCompoundTag("minecraft:material_instances"));
            }
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
    public void registerCustomBlocks(BlockProperties[] blockProperties, boolean hashedRuntimeBlockIds, CallbackInfo ci, @Local(ordinal = 1) List<BedrockBlockState> states) {
        final List<Pair<ResourceKey<@NotNull Block>, Block>> blocks = new ArrayList<>();
        final Map<BlockState, DynamicBlockCache.ModelToBeBake> models = new HashMap<>();

        // We have to "un-freeze" the registry in order to register new blocks.
        ((IMappedRegistry) BuiltInRegistries.BLOCK).viaFabricPlus$unfreeze();

        for (BedrockBlockState state : states) {
            // Since Bedrock block state is a lot more flexible than Java, each block state on Bedrock will equal to a new block on Java.
            final ResourceKey<@NotNull Block> key = DynamicBlockCache.key(state.namespacedIdentifier() + "-" + state.blockStateTag().hashCode());
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().setId(key);
            properties.noOcclusion(); // Fix culling.

            final BlockComponentsTranslator.Result result = BlockComponentsTranslator.parseBlockComponents(new BlockComponentsTranslator.Result.Builder(), state.namespacedIdentifier(), state.blockStateTag());

            properties.destroyTime(result.destroyTime());
            properties.friction(result.friction());

            final Block block = new CustomBlock(properties, result.collision(), result.selection());
            blocks.add(new Pair<>(key, block));

            DynamicBlockCache.putKeyToState(state.namespacedIdentifier() + "-" + state.blockStateTag().hashCode(), block.defaultBlockState());

            if (result.textures() == null || result.textures().isEmpty() || result.model() == null) {
                continue;
            }

            models.put(block.defaultBlockState(), new DynamicBlockCache.ModelToBeBake(result.model(), result.textures()));
        }

        DynamicBlockCache.register(blocks);

        // This is important, after done registering everything, it will cause issues if we don't re-freeze it.
        ((IMappedRegistry) BuiltInRegistries.BLOCK).viaFabricPlus$refreeze();

        DynamicBlockCache.requestBakeModelsAndLoad(models); // Now request a texture pack reload to load the models.
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/libs/fastutil/ints/Int2IntMap;put(II)I", ordinal = 1))
    public int mapCustomBlocksToOurBlocks(Int2IntMap instance, int i, int i1, @Local BedrockBlockState state) {
        instance.put(i, DynamicBlockCache.blockKeyToId(state.namespacedIdentifier() + "-" + state.blockStateTag().hashCode()));
        return i;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;)V"))
    private void cancelMissingBlockLog(Logger instance, Level level, String msg) {
    }
}
