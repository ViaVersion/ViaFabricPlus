/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.function.Supplier;

/**
 * A helper class for creating recipes.
 */
public final class RecipeInfo {

    private final Supplier<Recipe<?>> creator;

    private RecipeInfo(Supplier<Recipe<?>> creator) {
        this.creator = creator;
    }

    /**
     * Creates a new recipe info with the given creator.
     *
     * @param creator The creator
     * @return The recipe info
     */
    public static RecipeInfo of(Supplier<Recipe<?>> creator) {
        return new RecipeInfo(creator);
    }

    /**
     * Creates a new shaped recipe info with the given creator.
     *
     * @param output The output
     * @param args   The arguments
     * @return The recipe info containing a shaped recipe
     */
    public static RecipeInfo shaped(ItemConvertible output, Object... args) {
        return shaped("", output, args);
    }

    /**
     * Creates a new shaped recipe info with the given creator.
     *
     * @param count  The count
     * @param output The output
     * @param args   The arguments
     * @return The recipe info containing a shaped recipe
     */
    public static RecipeInfo shaped(int count, ItemConvertible output, Object... args) {
        return shaped("", count, output, args);
    }

    /**
     * Creates a new shaped recipe info with the given creator.
     *
     * @param group  The group
     * @param output The output
     * @param args   The arguments
     * @return The recipe info containing a shaped recipe
     */
    public static RecipeInfo shaped(String group, ItemStack output, Object... args) {
        final List<String> shape = new ArrayList<>();

        int i;
        int width = 0;
        for (i = 0; i < args.length && args[i] instanceof String str; i++) {
            if (i == 0) {
                width = str.length();
            } else if (str.length() != width) {
                throw new IllegalArgumentException("Rows do not have consistent width");
            }
            shape.add(str);
        }
        Map<Character, Ingredient> legend = new HashMap<>();
        while (i < args.length && args[i] instanceof Character key) {
            i++;
            List<ItemConvertible> items = new ArrayList<>();
            for (; i < args.length && args[i] instanceof ItemConvertible; i++) {
                items.add((ItemConvertible) args[i]);
            }
            legend.put(key, Ingredient.ofItems(items.toArray(new ItemConvertible[0])));
        }
        if (i != args.length) {
            throw new IllegalArgumentException("Unexpected argument at index " + i + ": " + args[i]);
        }

        final int height = shape.size();
        final DefaultedList<Optional<Ingredient>> ingredients = DefaultedList.of();
        for (String row : shape) {
            for (int x = 0; x < width; x++) {
                final char key = row.charAt(x);
                Ingredient ingredient = legend.get(key);
                if (ingredient == null) {
                    if (key == ' ') {
                        ingredients.add(Optional.empty());
                    } else {
                        throw new IllegalArgumentException("Unknown character in shape: " + key);
                    }
                } else {
                    ingredients.add(Optional.of(ingredient));
                }
            }
        }

        final int width_f = width;
        return new RecipeInfo(() -> new ShapedRecipe(group, CraftingRecipeCategory.MISC, new RawShapedRecipe(width_f, height, ingredients, Optional.empty()), output, false));
    }

    /**
     * Creates a new shaped recipe info with the given creator.
     *
     * @param group  The group
     * @param output The output
     * @param args   The arguments
     * @return The recipe info containing a shaped recipe
     */
    public static RecipeInfo shaped(String group, ItemConvertible output, Object... args) {
        return shaped(group, new ItemStack(output), args);
    }

    /**
     * Creates a new shaped recipe info with the given creator.
     *
     * @param group  The group
     * @param count  The count
     * @param output The output
     * @param args   The arguments
     * @return The recipe info containing a shaped recipe
     */
    public static RecipeInfo shaped(String group, int count, ItemConvertible output, Object... args) {
        return shaped(group, new ItemStack(output, count), args);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param group  The group
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(String group, ItemStack output, ItemConvertible... inputs) {
        final ItemConvertible[][] newInputs = new ItemConvertible[inputs.length][1];
        for (int i = 0; i < inputs.length; i++) {
            newInputs[i] = new ItemConvertible[]{inputs[i]};
        }
        return shapeless(group, output, newInputs);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param group  The group
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(String group, ItemConvertible output, ItemConvertible... inputs) {
        return shapeless(group, new ItemStack(output), inputs);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param group  The group
     * @param count  The count
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(String group, int count, ItemConvertible output, ItemConvertible... inputs) {
        return shapeless(group, new ItemStack(output, count), inputs);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param group  The group
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(String group, ItemStack output, ItemConvertible[]... inputs) {
        final DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (ItemConvertible[] input : inputs) {
            ingredients.add(Ingredient.ofItems(input));
        }
        return new RecipeInfo(() -> new ShapelessRecipe(group, CraftingRecipeCategory.MISC, output, ingredients));
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param group  The group
     * @param count  The count
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(String group, int count, ItemConvertible output, ItemConvertible[]... inputs) {
        return shapeless(group, new ItemStack(output, count), inputs);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(ItemConvertible output, ItemConvertible... inputs) {
        return shapeless("", output, inputs);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param count  The count
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(int count, ItemConvertible output, ItemConvertible... inputs) {
        return shapeless("", count, output, inputs);
    }

    /**
     * Creates a new shapeless recipe info with the given creator.
     *
     * @param count  The count
     * @param output The output
     * @param inputs The inputs
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo shapeless(int count, ItemConvertible output, ItemConvertible[]... inputs) {
        return shapeless("", count, output, inputs);
    }

    /**
     * Creates a new smelting recipe info with the given creator.
     *
     * @param output     The output
     * @param input      The input
     * @param experience The experience
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo smelting(ItemConvertible output, ItemConvertible input, float experience) {
        return smelting(output, input, experience, 200);
    }

    /**
     * Creates a new smelting recipe info with the given creator.
     *
     * @param output     The output
     * @param input      The input
     * @param experience The experience
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo smelting(ItemConvertible output, Ingredient input, float experience) {
        return smelting(output, input, experience, 200);
    }

    /**
     * Creates a new smelting recipe info with the given creator.
     *
     * @param output     The output
     * @param input      The input
     * @param experience The experience
     * @param cookTime   The cook time
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo smelting(ItemConvertible output, ItemConvertible input, float experience, int cookTime) {
        return smelting(output, Ingredient.ofItems(input), experience, cookTime);
    }

    /**
     * Creates a new smelting recipe info with the given creator.
     *
     * @param output     The output
     * @param input      The input
     * @param experience The experience
     * @param cookTime   The cook time
     * @return The recipe info containing a shapeless recipe
     */
    public static RecipeInfo smelting(ItemConvertible output, Ingredient input, float experience, int cookTime) {
        return new RecipeInfo(() -> new SmeltingRecipe("", CookingRecipeCategory.MISC, input, new ItemStack(output), experience, cookTime));
    }

    /**
     * Creates a new recipe info with the given creator.
     *
     * @param id The id
     * @return The recipe info
     */
    public RecipeEntry<?> create(RegistryKey<Recipe<?>> id) {
        return new RecipeEntry<>(id, this.creator.get());
    }

}
