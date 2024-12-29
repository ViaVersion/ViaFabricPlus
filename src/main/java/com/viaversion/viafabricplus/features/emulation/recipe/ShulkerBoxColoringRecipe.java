/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.features.emulation.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public final class ShulkerBoxColoringRecipe extends SpecialCraftingRecipe {

    public static final RecipeSerializer<ShulkerBoxColoringRecipe> SERIALIZER = new SpecialRecipeSerializer<>(ShulkerBoxColoringRecipe::new);

    public ShulkerBoxColoringRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    public boolean matches(CraftingRecipeInput input, World world) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < input.size(); k++) {
            ItemStack stack = input.getStackInSlot(k);
            if (!stack.isEmpty()) {
                if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock) {
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

    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack result = ItemStack.EMPTY;
        DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
                    result = stack;
                } else if (item instanceof DyeItem) {
                    dyeItem = (DyeItem) item;
                }
            }
        }

        return result.copyComponentsToNewStack(ShulkerBoxBlock.get(dyeItem.getColor()), 1);
    }

    @Override
    public RecipeSerializer<ShulkerBoxColoringRecipe> getSerializer() {
        return SERIALIZER;
    }

}
