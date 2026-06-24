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

package com.viaversion.viafabricplus.features.recipe.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jspecify.annotations.NonNull;

public final class ShulkerBoxColoringRecipe extends CustomRecipe {

    public static final RecipeSerializer<ShulkerBoxColoringRecipe> SERIALIZER = new RecipeSerializer<>(MapCodec.unit(new ShulkerBoxColoringRecipe()), StreamCodec.unit(new ShulkerBoxColoringRecipe()));

    public boolean matches(CraftingInput input, @NonNull Level world) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < input.size(); k++) {
            ItemStack stack = input.getItem(k);
            if (!stack.isEmpty()) {
                if (Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock) {
                    i++;
                } else {
                    if (!(stack.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    j++;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }

        return i == 1 && j == 1;
    }

    public @NonNull ItemStack assemble(CraftingInput input) {
        ItemStack result = ItemStack.EMPTY;
        ItemStack dyeStack = Items.DYE.white().getDefaultInstance();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (Block.byItem(item) instanceof ShulkerBoxBlock) {
                    result = stack;
                } else if (item instanceof DyeItem) {
                    dyeStack = stack;
                }
            }
        }

        final DyeColor dye = dyeStack.get(DataComponents.DYE);
        return result.transmuteCopy(dye == null ? Blocks.SHULKER_BOX : Blocks.DYED_SHULKER_BOX.pick(dye), 1);
    }

    @Override
    public @NonNull RecipeSerializer<ShulkerBoxColoringRecipe> getSerializer() {
        return SERIALIZER;
    }

}
