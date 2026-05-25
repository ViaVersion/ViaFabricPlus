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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.raphi;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.NumberTag;
import com.viaversion.nbt.tag.Tag;
import com.viaversion.viafabricplus.injection.access.raphi.IModelDefinitions;
import com.viaversion.viafabricplus.injection.access.raphi.IResourcePackStorage;
import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import com.viaversion.viafabricplus.util.BlockLoaderUtil;
import com.viaversion.viafabricplus.util.RandomBullshitGoUtil;
import com.viaversion.viafabricplus.util.block.CustomBlock;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.raphimc.viabedrock.api.model.BedrockBlockState;
import net.raphimc.viabedrock.protocol.model.BlockProperties;
import net.raphimc.viabedrock.protocol.rewriter.BlockStateRewriter;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.cube.converter.converter.enums.RotationType;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.cube.converter.util.element.Direction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mixin(BlockStateRewriter.class)
public class MixinBlockStateRewriter {
    @Redirect(
        method = {"<init>"},
        at = @At(
            value = "INVOKE",
            target = "Lcom/viaversion/nbt/tag/CompoundTag;putInt(Ljava/lang/String;I)V"
        )
    )
    public void pullMoreComponents(CompoundTag instance, String tagName, int value, @Local Map.Entry<String, CompoundTag> blockProperty) {
        // We need more data to properly implement these blocks.
        instance.putInt(tagName, value);
        CompoundTag tag = blockProperty.getValue();
        if (tag.contains("components")) {
            CompoundTag components = tag.getCompoundTag("components");
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

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z", shift = At.Shift.BEFORE))
    public void registerCustomBlocks(BlockProperties[] blockProperties, boolean hashedRuntimeBlockIds, CallbackInfo ci, @Local(ordinal = 1) List<BedrockBlockState> states) {
        final List<Pair<ResourceKey<@NotNull Block>, Block>> blocks = new ArrayList<>();
        final Map<BlockState, String> models = new HashMap<>();

        ((IMappedRegistry) BuiltInRegistries.BLOCK).viaFabricPlus$unfreeze();
        final Set<String> registeredBlocks = new HashSet<>();
        for (BedrockBlockState state : states) {
            if (registeredBlocks.contains(state.namespacedIdentifier())) {
                continue;
            }

            final VoxelShape collision = RandomBullshitGoUtil.tagToVoxelShape(state.blockStateTag().get("minecraft:collision_box"));
            final VoxelShape selection = RandomBullshitGoUtil.tagToVoxelShape(state.blockStateTag().get("minecraft:selection_box"));

            registeredBlocks.add(state.namespacedIdentifier());
            ResourceKey<Block> blockResourceKey = BlockLoaderUtil.key(state.namespacedIdentifier());

            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().setId(blockResourceKey);
            properties.noOcclusion();
            if (state.blockStateTag().contains("minecraft:destructible_by_mining")) {
                Tag tag = state.blockStateTag().get("minecraft:destructible_by_mining");
                if (tag instanceof NumberTag nt) {
                    properties.destroyTime(nt.asBoolean() ? 0 : -1);
                } else if (tag instanceof CompoundTag ct && ct.contains("value")) {
                    properties.destroyTime(ct.getFloat("value"));
                }
            }
            if (state.blockStateTag().contains("minecraft:friction")) {
                Tag tag = state.blockStateTag().get("minecraft:friction");
                if (tag instanceof CompoundTag ct && ct.contains("value")) {
                    properties.friction(Math.max(0, 1 - ct.getFloat("value")));
                }
            }

            Block block = new CustomBlock(properties, collision, selection);
            blocks.add(new Pair<>(blockResourceKey, block));

            BlockLoaderUtil.ID_TO_STATE.put(state.namespacedIdentifier(), block.defaultBlockState());

            if (RandomBullshitGoUtil.STORAGE == null) {
                continue;
            }

            final ResourcePackStorage storage = RandomBullshitGoUtil.STORAGE;
            BedrockGeometryModel model;
            if (state.blockStateTag().contains("minecraft:geometry")) {
                String identifier = state.blockStateTag().getCompoundTag("minecraft:geometry").getString("identifier");
                model = ((IModelDefinitions)storage.getModels()).viaFabricPlus$getBlockModel(identifier);
            } else {
                model = ((IModelDefinitions)storage.getModels()).viaFabricPlus$getBlockModel("minecraft:geometry.full_block");
            }

            if (model == null) {
                continue;
            }

            Map<Direction, String> textureMap = ((IResourcePackStorage)storage).viaFabricPlus$textures(state.namespacedIdentifier());
            if (textureMap.isEmpty() && state.blockStateTag().contains("minecraft:material_instances")) {
                CompoundTag materialInstance = state.blockStateTag().getCompoundTag("minecraft:material_instances");

                CompoundTag materials = materialInstance.getCompoundTag("materials");
                if (materials == null) {
                    continue;
                }

                if (materials.contains("*")) {
                    CompoundTag all = materials.getCompoundTag("*");

                    if (!all.contains("texture")) {
                        continue;
                    }

                    String result = ((IResourcePackStorage)storage).viaFabricPlus$texturesPathFromId(all.getString("texture"));
                    if (result != null) {
                        final Map<Direction, String> map = new HashMap<>();

                        for (Direction direction : Direction.values()) {
                            map.put(direction, result);
                        }

                        if (!map.isEmpty()) {
                            textureMap = map;
                        }
                    }
                } else {
                    final Map<Direction, String> map = new HashMap<>();
                    for (Direction direction : Direction.values()) {
                        RandomBullshitGoUtil.putIfExist(direction, materials, map);
                    }
                    if (!map.isEmpty()) {
                        textureMap = map;
                    }
                }
            }

            String javaModelJson = model.toJavaItemModel(textureMap, RotationType.POST_1_21_11).compile().toString();
            models.put(block.defaultBlockState(), javaModelJson);
        }
        BlockLoaderUtil.register(blocks);

        ((IMappedRegistry) BuiltInRegistries.BLOCK).viaFabricPlus$refreeze();

        BlockLoaderUtil.loadModels(models);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/logging/Logger;log(Ljava/util/logging/Level;Ljava/lang/String;)V"))
    private void cancelMissingBlockLog(Logger instance, Level level, String msg) {
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE",
        target = "Lcom/viaversion/viaversion/libs/fastutil/ints/Int2IntMap;put(II)I", ordinal = 1))
    public int overrideCustomBlocks(Int2IntMap instance, int i, int i1, @Local BedrockBlockState bedrockBlockState) {
        BlockState state = BlockLoaderUtil.ID_TO_STATE.get(bedrockBlockState.namespacedIdentifier());

        if (state == null) {
            instance.put(i, i1);
        } else {
            instance.put(i, Block.getId(state));
        }
        return i;
    }
}
