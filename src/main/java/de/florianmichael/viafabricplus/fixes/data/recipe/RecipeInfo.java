/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes.data.recipe;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class RecipeInfo<T extends Recipe<?>> {

    private final Supplier<Recipe<?>> creator;
    private final RecipeSerializer<T> recipeType;
    private final ItemStack output;
    private String distinguisher = "";

    private RecipeInfo(Supplier<Recipe<?>> creator, RecipeSerializer<T> recipeType, ItemStack output) {
        this.creator = creator;
        this.recipeType = recipeType;
        this.output = output;
    }

    public static <T extends Recipe<?>> RecipeInfo<T> of(Supplier<Recipe<?>> creator, RecipeSerializer<T> recipeType, ItemStack output) {
        return new RecipeInfo<>(creator, recipeType, output);
    }

    public static <T extends Recipe<?>> RecipeInfo<T> of(Supplier<Recipe<?>> creator, RecipeSerializer<T> recipeType, ItemConvertible output) {
        return of(creator, recipeType, new ItemStack(output));
    }

    public static <T extends Recipe<?>> RecipeInfo<T> of(Supplier<Recipe<?>> creator, RecipeSerializer<T> recipeType, ItemConvertible output, int count) {
        return of(creator, recipeType, new ItemStack(output, count));
    }

    public static RecipeInfo<ShapedRecipe> shaped(ItemStack output, Object... args) {
        return shaped("", output, args);
    }

    public static RecipeInfo<ShapedRecipe> shaped(ItemConvertible output, Object... args) {
        return shaped("", output, args);
    }

    public static RecipeInfo<ShapedRecipe> shaped(int count, ItemConvertible output, Object... args) {
        return shaped("", count, output, args);
    }

    public static RecipeInfo<ShapedRecipe> shaped(String group, ItemStack output, Object... args) {
        int i;
        int width = 0;
        List<String> shape = new ArrayList<>();
        for (i = 0; i < args.length && args[i] instanceof String str; i++) {
            if (i == 0)
                width = str.length();
            else if (str.length() != width)
                throw new IllegalArgumentException("Rows do not have consistent width");
            shape.add(str);
        }
        var legend = new HashMap<Character, Ingredient>();
        while (i < args.length && args[i] instanceof Character key) {
            i++;
            List<ItemConvertible> items = new ArrayList<>();
            for (; i < args.length && args[i] instanceof ItemConvertible; i++) {
                items.add((ItemConvertible) args[i]);
            }
            legend.put(key, Ingredient.ofItems(items.toArray(new ItemConvertible[0])));
        }
        if (i != args.length)
            throw new IllegalArgumentException("Unexpected argument at index " + i + ": " + args[i]);

        int height = shape.size();
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (String row : shape) {
            for (int x = 0; x < width; x++) {
                char key = row.charAt(x);
                Ingredient ingredient = legend.get(key);
                if (ingredient == null) {
                    if (key == ' ')
                        ingredient = Ingredient.EMPTY;
                    else
                        throw new IllegalArgumentException("Unknown character in shape: " + key);
                }
                ingredients.add(ingredient);
            }
        }

        final int width_f = width;
        return new RecipeInfo<>(() -> new ShapedRecipe(group, CraftingRecipeCategory.MISC, new RawShapedRecipe(width_f, height, ingredients, Optional.empty()), output, false), RecipeSerializer.SHAPED, output);
    }

    public static RecipeInfo<ShapedRecipe> shaped(String group, ItemConvertible output, Object... args) {
        return shaped(group, new ItemStack(output), args);
    }

    public static RecipeInfo<ShapedRecipe> shaped(String group, int count, ItemConvertible output, Object... args) {
        return shaped(group, new ItemStack(output, count), args);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(String group, ItemStack output, ItemConvertible... inputs) {
        ItemConvertible[][] newInputs = new ItemConvertible[inputs.length][1];
        for (int i = 0; i < inputs.length; i++)
            newInputs[i] = new ItemConvertible[]{inputs[i]};
        return shapeless(group, output, newInputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(String group, ItemConvertible output, ItemConvertible... inputs) {
        return shapeless(group, new ItemStack(output), inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(String group, int count, ItemConvertible output, ItemConvertible... inputs) {
        return shapeless(group, new ItemStack(output, count), inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(String group, ItemStack output, ItemConvertible[]... inputs) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        for (ItemConvertible[] input : inputs) {
            ingredients.add(Ingredient.ofItems(input));
        }
        return new RecipeInfo<>(() -> new ShapelessRecipe(group, CraftingRecipeCategory.MISC, output, ingredients), RecipeSerializer.SHAPELESS, output);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(String group, ItemConvertible output, ItemConvertible[]... inputs) {
        return shapeless(group, new ItemStack(output), inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(String group, int count, ItemConvertible output, ItemConvertible[]... inputs) {
        return shapeless(group, new ItemStack(output, count), inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(ItemStack output, ItemConvertible... inputs) {
        return shapeless("", output, inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(ItemConvertible output, ItemConvertible... inputs) {
        return shapeless("", output, inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(int count, ItemConvertible output, ItemConvertible... inputs) {
        return shapeless("", count, output, inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(ItemStack output, ItemConvertible[]... inputs) {
        return shapeless("", output, inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(ItemConvertible output, ItemConvertible[]... inputs) {
        return shapeless("", output, inputs);
    }

    public static RecipeInfo<ShapelessRecipe> shapeless(int count, ItemConvertible output, ItemConvertible[]... inputs) {
        return shapeless("", count, output, inputs);
    }

    public static RecipeInfo<SmeltingRecipe> smelting(ItemConvertible output, ItemConvertible input, float experience) {
        return smelting(output, input, experience, 200);
    }

    public static RecipeInfo<SmeltingRecipe> smelting(ItemConvertible output, Ingredient input, float experience) {
        return smelting(output, input, experience, 200);
    }

    public static RecipeInfo<SmeltingRecipe> smelting(ItemConvertible output, ItemConvertible input, float experience, int cookTime) {
        return smelting(output, Ingredient.ofItems(input), experience, cookTime);
    }

    public static RecipeInfo<SmeltingRecipe> smelting(ItemConvertible output, Ingredient input, float experience, int cookTime) {
        ItemStack outputStack = new ItemStack(output);
        return new RecipeInfo<>(() -> new SmeltingRecipe("", CookingRecipeCategory.MISC, input, outputStack, experience, cookTime), RecipeSerializer.SMELTING, outputStack);
    }

    public RecipeInfo<T> distinguisher(String distinguisher) {
        this.distinguisher = distinguisher;
        return this;
    }


    public RecipeEntry<?> create(Identifier id) {
        return new RecipeEntry<Recipe<?>>(id, this.creator.get());
    }

    public RecipeSerializer<T> getRecipeType() {
        return this.recipeType;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public String getDistinguisher() {
        return this.distinguisher;
    }

}
