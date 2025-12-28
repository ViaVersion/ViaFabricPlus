/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.features.recipe;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

public final class AddBannerPatternRecipe extends CustomRecipe {

    public static final RecipeSerializer<AddBannerPatternRecipe> SERIALIZER = new Serializer<>(AddBannerPatternRecipe::new);

    public AddBannerPatternRecipe(CraftingBookCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    @Override
    public boolean matches(CraftingInput input, Level world) {
        boolean foundBanner = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof BannerItem) {
                if (foundBanner)
                    return false;
                if (stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().size() >= 6)
                    return false;
                foundBanner = true;
            }
        }
        return foundBanner && getBannerPattern(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        ItemStack result = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BannerItem) {
                result = stack.copy();
                result.setCount(1);
                break;
            }
        }

        final BannerPattern_1_13_2 pattern = getBannerPattern(input);
        if (pattern != null) {
            final Holder.Reference<BannerPattern> patternKey = lookup.lookupOrThrow(Registries.BANNER_PATTERN).getOrThrow(pattern.getKey());
            DyeColor color = ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) ? DyeColor.BLACK : DyeColor.WHITE;
            for (int i = 0; i < input.size(); i++) {
                Item item = input.getItem(i).getItem();
                if (item instanceof DyeItem dyeItem) {
                    color = dyeItem.getDyeColor();
                }
            }

            final BannerPatternLayers.Builder patternsBuilder = new BannerPatternLayers.Builder();
            if (result.has(DataComponents.BANNER_PATTERNS)) {
                patternsBuilder.addAll(result.get(DataComponents.BANNER_PATTERNS));
            }
            patternsBuilder.add(new BannerPatternLayers.Layer(patternKey, color));
            result.set(DataComponents.BANNER_PATTERNS, patternsBuilder.build());
        }

        return result;
    }

    @Override
    public RecipeSerializer<AddBannerPatternRecipe> getSerializer() {
        return SERIALIZER;
    }

    private static BannerPattern_1_13_2 getBannerPattern(CraftingInput input) {
        for (BannerPattern_1_13_2 pattern : BannerPattern_1_13_2.values()) {
            if (!pattern.isCraftable())
                continue;

            boolean matches = true;
            if (pattern.hasBaseStack()) {
                boolean foundBaseItem = false;
                boolean foundDye = false;
                for (int i = 0; i < input.size(); i++) {
                    ItemStack stack = input.getItem(i);
                    if (!stack.isEmpty() && !(stack.getItem() instanceof BannerItem)) {
                        if (stack.getItem() instanceof DyeItem) {
                            if (foundDye) {
                                matches = false;
                                break;
                            }
                            foundDye = true;
                        } else {
                            if (foundBaseItem || !ItemStack.isSameItem(stack, pattern.getBaseStack())) {
                                matches = false;
                                break;
                            }
                            foundBaseItem = true;
                        }
                    }
                }
                if (!foundBaseItem || (!foundDye && ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_10)))
                    matches = false;
            } else if (input.size() == pattern.getRecipePattern().length * pattern.getRecipePattern()[0].length()) {
                DyeColor patternColor = null;
                for (int i = 0; i < input.size(); i++) {
                    int row = i / 3;
                    int col = i % 3;
                    ItemStack stack = input.getItem(i);
                    Item item = stack.getItem();
                    if (!stack.isEmpty() && !(item instanceof BannerItem)) {
                        if (!(item instanceof DyeItem)) {
                            matches = false;
                            break;
                        }

                        DyeColor color = ((DyeItem) item).getDyeColor();
                        if (patternColor != null && color != patternColor) {
                            matches = false;
                            break;
                        }

                        if (pattern.getRecipePattern()[row].charAt(col) == ' ') {
                            matches = false;
                            break;
                        }

                        patternColor = color;
                    } else if (pattern.getRecipePattern()[row].charAt(col) != ' ') {
                        matches = false;
                        break;
                    }
                }
            } else {
                matches = false;
            }

            if (matches)
                return pattern;
        }

        return null;
    }

}
