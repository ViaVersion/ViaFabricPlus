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

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.recipe.BannerDuplicateRecipe;
import net.minecraft.recipe.BookCloningRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.FireworkRocketRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.MapCloningRecipe;
import net.minecraft.recipe.MapExtendingRecipe;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.TippedArrowRecipe;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

/**
 * Recipe data dump for all versions below 1.12.
 */
public final class Recipes1_11_2 {

    private static final List<Pair<LegacyRecipe, VersionRange>> LEGACY_RECIPES = new ArrayList<>();
    private static RecipeManager1_11_2 RECIPE_MANAGER;

    public static void init() {
        if (!LEGACY_RECIPES.isEmpty()) {
            throw new IllegalStateException("Recipes1_11_2 is already initialized");
        }

        final JsonArray recipes = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("recipes-1.11.2.json").getAsJsonArray("");
        for (JsonElement recipeElement : recipes) {
            final String type = recipeElement.getAsJsonObject().get("type").getAsString();
            final VersionRange versionRange = VersionRange.fromString(recipeElement.getAsJsonObject().get("version").getAsString());
            switch (type) {
                case "shaped" ->
                    LEGACY_RECIPES.add(new Pair<>(LegacyShapedRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                case "shapeless" ->
                    LEGACY_RECIPES.add(new Pair<>(LegacyShapelessRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                case "smelting" ->
                    LEGACY_RECIPES.add(new Pair<>(LegacySmeltingRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                default -> throw new IllegalArgumentException("Unknown recipe type: " + type);
            }
        }
    }

    public static RecipeManager1_11_2 getRecipeManager() {
        if (RECIPE_MANAGER == null) {
            final List<RecipeEntry<?>> recipes = new ArrayList<>();

            // Regular recipes
            for (int i = 0; i < LEGACY_RECIPES.size(); i++) {
                final Pair<LegacyRecipe, VersionRange> legacyRecipe = LEGACY_RECIPES.get(i);
                if (legacyRecipe.getRight().contains(ProtocolTranslator.getTargetVersion())) {
                    final RegistryKey<Recipe<?>> key = RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("viafabricplus", "recipe/" + i));
                    switch (legacyRecipe.getLeft()) {
                        case LegacyShapedRecipe legacyShapedRecipe -> {
                            final Map<Character, Ingredient> ingredients = new HashMap<>();
                            for (Map.Entry<Character, List<Item>> entry : legacyShapedRecipe.legend.entrySet()) {
                                final ItemConvertible[] items = new ItemConvertible[entry.getValue().size()];
                                for (int j = 0; j < entry.getValue().size(); j++) {
                                    items[j] = entry.getValue().get(j);
                                }
                                ingredients.put(entry.getKey(), Ingredient.ofItems(items));
                            }
                            final ItemStack output = legacyShapedRecipe.result.toItemStack();
                            final CraftingRecipe recipe = new ShapedRecipe(legacyShapedRecipe.group, CraftingRecipeCategory.MISC, RawShapedRecipe.create(ingredients, legacyShapedRecipe.pattern), output, false);
                            recipes.add(new RecipeEntry<>(key, recipe));
                        }
                        case LegacyShapelessRecipe legacyShapelessRecipe -> {
                            final ItemStack output = legacyShapelessRecipe.result.toItemStack();
                            final List<Ingredient> ingredients = new ArrayList<>();
                            for (List<Item> ingredientIds : legacyShapelessRecipe.ingredients) {
                                final ItemConvertible[] items = new ItemConvertible[ingredientIds.size()];
                                for (int j = 0; j < ingredientIds.size(); j++) {
                                    items[j] = ingredientIds.get(j);
                                }
                                ingredients.add(Ingredient.ofItems(items));
                            }
                            final CraftingRecipe recipe = new ShapelessRecipe(legacyShapelessRecipe.group, CraftingRecipeCategory.MISC, output, ingredients);
                            recipes.add(new RecipeEntry<>(key, recipe));
                        }
                        case LegacySmeltingRecipe legacySmeltingRecipe -> {
                            final ItemStack output = legacySmeltingRecipe.result.toItemStack();
                            final ItemConvertible[] inputItems = new ItemConvertible[legacySmeltingRecipe.input.size()];
                            for (int j = 0; j < legacySmeltingRecipe.input.size(); j++) {
                                inputItems[j] = legacySmeltingRecipe.input.get(j);
                            }
                            final Ingredient input = Ingredient.ofItems(inputItems);
                            final SmeltingRecipe recipe = new SmeltingRecipe("", CookingRecipeCategory.MISC, input, output, legacySmeltingRecipe.experience, 200);
                            recipes.add(new RecipeEntry<>(key, recipe));
                        }
                        default ->
                            throw new IllegalStateException("Unknown legacy recipe type: " + legacyRecipe.getLeft().getClass());
                    }
                }
            }

            // Special recipes
            final List<CraftingRecipe> specialRecipes = new ArrayList<>();
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
                specialRecipes.add(new ArmorDyeRecipe(CraftingRecipeCategory.MISC));
                specialRecipes.add(new MapCloningRecipe(CraftingRecipeCategory.MISC));
                specialRecipes.add(new MapExtendingRecipe(CraftingRecipeCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
                specialRecipes.add(new FireworkRocketRecipe(CraftingRecipeCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_11)) {
                specialRecipes.add(new ShulkerBoxColoringRecipe(CraftingRecipeCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_9)) {
                specialRecipes.add(new TippedArrowRecipe(CraftingRecipeCategory.MISC));
                specialRecipes.add(new ShieldDecorationRecipe(CraftingRecipeCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_8)) {
                specialRecipes.add(new RepairItemRecipe(CraftingRecipeCategory.MISC));
                specialRecipes.add(new BannerDuplicateRecipe(CraftingRecipeCategory.MISC));
                specialRecipes.add(new AddBannerPatternRecipe(CraftingRecipeCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_7_2)) {
                specialRecipes.add(new BookCloningRecipe(CraftingRecipeCategory.MISC));
            }
            for (CraftingRecipe specialRecipe : specialRecipes) {
                final RegistryKey<Recipe<?>> key = RegistryKey.of(RegistryKeys.RECIPE, Identifier.of("viafabricplus", "recipe/special_" + specialRecipe.getClass().getSimpleName().replace("Recipe", "").toLowerCase(Locale.ROOT)));
                recipes.add(new RecipeEntry<>(key, specialRecipe));
            }

            RECIPE_MANAGER = new RecipeManager1_11_2(recipes);
        }

        return RECIPE_MANAGER;
    }

    public static void reset() {
        RECIPE_MANAGER = null;
    }

    /**
     * Sets the result slot of a crafting screen handler to the correct item stack. In Minecraft versions up to 1.11.2 the result slot
     * is not updated when the input slots change, so we need to update it manually, Spigot and Paper re-syncs the slot,
     * so we don't notice this bug on servers that use Spigot or Paper
     *
     * @param syncId        The sync id of the screen handler
     * @param screenHandler The screen handler
     * @param inventory     The inventory of the screen handler
     */
    public static void setCraftingResultSlot(final int syncId, final ScreenHandler screenHandler, final RecipeInputInventory inventory) {
        final ClientPlayNetworkHandler network = MinecraftClient.getInstance().getNetworkHandler();
        final ClientWorld world = MinecraftClient.getInstance().world;
        final CraftingRecipeInput input = inventory.createRecipeInput();

        final ItemStack result = getRecipeManager()
            .getFirstMatch(RecipeType.CRAFTING, input, world) // Get the first matching recipe
            .map(recipe -> recipe.value().craft(input, network.getRegistryManager())) // Craft the recipe to get the result
            .orElse(ItemStack.EMPTY); // If there is no recipe, set the result to air

        // Update the result slot
        network.onScreenHandlerSlotUpdate(new ScreenHandlerSlotUpdateS2CPacket(syncId, screenHandler.getRevision(), 0, result));
    }

    private static Item getItemById(final Identifier id) {
        final Item item = Registries.ITEM.getOptionalValue(id).orElse(null);
        if (item == null) {
            throw new IllegalStateException("Unknown item: " + id.toString());
        }

        return item;
    }

    private sealed interface LegacyRecipe permits LegacyShapedRecipe, LegacyShapelessRecipe, LegacySmeltingRecipe {
    }

    private record RecipeItemStack(Item item, int count) {

        private static RecipeItemStack fromJson(final JsonObject obj) {
            final Identifier id = Identifier.of(obj.get("id").getAsString());
            final int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            return new RecipeItemStack(getItemById(id), count);
        }

        private ItemStack toItemStack() {
            return new ItemStack(this.item, this.count);
        }

    }

    private record LegacyShapedRecipe(String group, RecipeItemStack result, List<String> pattern,
                                      Map<Character, List<Item>> legend) implements LegacyRecipe {

        public static LegacyShapedRecipe fromJson(final JsonObject obj) {
            final String group = obj.has("group") ? obj.get("group").getAsString() : "";
            final RecipeItemStack result = RecipeItemStack.fromJson(obj.getAsJsonObject("result"));
            final List<String> pattern = new ArrayList<>();
            for (JsonElement element : obj.getAsJsonArray("pattern")) {
                pattern.add(element.getAsString());
            }
            final Map<Character, List<Item>> legend = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : obj.getAsJsonObject("legend").entrySet()) {
                final char key = entry.getKey().charAt(0);
                final List<Item> items = new ArrayList<>();
                for (JsonElement itemId : entry.getValue().getAsJsonArray()) {
                    items.add(getItemById(Identifier.of(itemId.getAsString())));
                }
                legend.put(key, items);
            }
            return new LegacyShapedRecipe(group, result, pattern, legend);
        }

    }

    private record LegacyShapelessRecipe(String group, RecipeItemStack result,
                                         List<List<Item>> ingredients) implements LegacyRecipe {

        public static LegacyShapelessRecipe fromJson(final JsonObject obj) {
            final String group = obj.has("group") ? obj.get("group").getAsString() : "";
            final RecipeItemStack result = RecipeItemStack.fromJson(obj.getAsJsonObject("result"));
            final List<List<Item>> ingredients = new ArrayList<>();
            for (JsonElement element : obj.getAsJsonArray("ingredients")) {
                final List<Item> items = new ArrayList<>();
                for (JsonElement itemId : element.getAsJsonArray()) {
                    items.add(getItemById(Identifier.of(itemId.getAsString())));
                }
                ingredients.add(items);
            }
            return new LegacyShapelessRecipe(group, result, ingredients);
        }

    }

    private record LegacySmeltingRecipe(String group, RecipeItemStack result, List<Item> input,
                                        float experience) implements LegacyRecipe {

        public static LegacySmeltingRecipe fromJson(final JsonObject obj) {
            final String group = obj.has("group") ? obj.get("group").getAsString() : "";
            final RecipeItemStack result = RecipeItemStack.fromJson(obj.getAsJsonObject("result"));
            final List<Item> input = new ArrayList<>();
            for (JsonElement element : obj.getAsJsonArray("input")) {
                input.add(getItemById(Identifier.of(element.getAsString())));
            }
            final float experience = obj.get("experience").getAsFloat();
            return new LegacySmeltingRecipe(group, result, input, experience);
        }

    }

}
