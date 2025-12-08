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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.TippedArrowRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

/**
 * Recipe data dump for all versions below 1.12.
 */
public final class Recipes1_11_2 {

    private static final List<Tuple<LegacyRecipe, VersionRange>> LEGACY_RECIPES = new ArrayList<>();
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
                    LEGACY_RECIPES.add(new Tuple<>(LegacyShapedRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                case "shapeless" ->
                    LEGACY_RECIPES.add(new Tuple<>(LegacyShapelessRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                case "smelting" ->
                    LEGACY_RECIPES.add(new Tuple<>(LegacySmeltingRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                default -> throw new IllegalArgumentException("Unknown recipe type: " + type);
            }
        }
    }

    public static RecipeManager1_11_2 getRecipeManager() {
        if (RECIPE_MANAGER == null) {
            final List<RecipeHolder<?>> recipes = new ArrayList<>();

            // Regular recipes
            for (int i = 0; i < LEGACY_RECIPES.size(); i++) {
                final Tuple<LegacyRecipe, VersionRange> legacyRecipe = LEGACY_RECIPES.get(i);
                if (legacyRecipe.getB().contains(ProtocolTranslator.getTargetVersion())) {
                    final ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath("viafabricplus", "recipe/" + i));
                    switch (legacyRecipe.getA()) {
                        case LegacyShapedRecipe legacyShapedRecipe -> {
                            final Map<Character, Ingredient> ingredients = new HashMap<>();
                            for (Map.Entry<Character, List<Item>> entry : legacyShapedRecipe.legend.entrySet()) {
                                final ItemLike[] items = new ItemLike[entry.getValue().size()];
                                for (int j = 0; j < entry.getValue().size(); j++) {
                                    items[j] = entry.getValue().get(j);
                                }
                                ingredients.put(entry.getKey(), Ingredient.of(items));
                            }
                            final ItemStack output = legacyShapedRecipe.result.toItemStack();
                            final CraftingRecipe recipe = new ShapedRecipe(legacyShapedRecipe.group, CraftingBookCategory.MISC, ShapedRecipePattern.of(ingredients, legacyShapedRecipe.pattern), output, false);
                            recipes.add(new RecipeHolder<>(key, recipe));
                        }
                        case LegacyShapelessRecipe legacyShapelessRecipe -> {
                            final ItemStack output = legacyShapelessRecipe.result.toItemStack();
                            final List<Ingredient> ingredients = new ArrayList<>();
                            for (List<Item> ingredientIds : legacyShapelessRecipe.ingredients) {
                                final ItemLike[] items = new ItemLike[ingredientIds.size()];
                                for (int j = 0; j < ingredientIds.size(); j++) {
                                    items[j] = ingredientIds.get(j);
                                }
                                ingredients.add(Ingredient.of(items));
                            }
                            final CraftingRecipe recipe = new ShapelessRecipe(legacyShapelessRecipe.group, CraftingBookCategory.MISC, output, ingredients);
                            recipes.add(new RecipeHolder<>(key, recipe));
                        }
                        case LegacySmeltingRecipe legacySmeltingRecipe -> {
                            final ItemStack output = legacySmeltingRecipe.result.toItemStack();
                            final ItemLike[] inputItems = new ItemLike[legacySmeltingRecipe.input.size()];
                            for (int j = 0; j < legacySmeltingRecipe.input.size(); j++) {
                                inputItems[j] = legacySmeltingRecipe.input.get(j);
                            }
                            final Ingredient input = Ingredient.of(inputItems);
                            final SmeltingRecipe recipe = new SmeltingRecipe("", CookingBookCategory.MISC, input, output, legacySmeltingRecipe.experience, 200);
                            recipes.add(new RecipeHolder<>(key, recipe));
                        }
                        default ->
                            throw new IllegalStateException("Unknown legacy recipe type: " + legacyRecipe.getA().getClass());
                    }
                }
            }

            // Special recipes
            final List<CraftingRecipe> specialRecipes = new ArrayList<>();
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
                specialRecipes.add(new ArmorDyeRecipe(CraftingBookCategory.MISC));
                specialRecipes.add(new MapCloningRecipe(CraftingBookCategory.MISC));
                specialRecipes.add(new MapExtendingRecipe(CraftingBookCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
                specialRecipes.add(new FireworkRocketRecipe(CraftingBookCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_11)) {
                specialRecipes.add(new ShulkerBoxColoringRecipe(CraftingBookCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_9)) {
                specialRecipes.add(new TippedArrowRecipe(CraftingBookCategory.MISC));
                specialRecipes.add(new ShieldDecorationRecipe(CraftingBookCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_8)) {
                specialRecipes.add(new RepairItemRecipe(CraftingBookCategory.MISC));
                specialRecipes.add(new BannerDuplicateRecipe(CraftingBookCategory.MISC));
                specialRecipes.add(new AddBannerPatternRecipe(CraftingBookCategory.MISC));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_7_2)) {
                specialRecipes.add(new BookCloningRecipe(CraftingBookCategory.MISC));
            }
            for (CraftingRecipe specialRecipe : specialRecipes) {
                final ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath("viafabricplus", "recipe/special_" + specialRecipe.getClass().getSimpleName().replace("Recipe", "").toLowerCase(Locale.ROOT)));
                recipes.add(new RecipeHolder<>(key, specialRecipe));
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
    public static void setCraftingResultSlot(final int syncId, final AbstractContainerMenu screenHandler, final CraftingContainer inventory) {
        final ClientPacketListener network = Minecraft.getInstance().getConnection();
        final ClientLevel world = Minecraft.getInstance().level;
        final CraftingInput input = inventory.asCraftInput();

        final ItemStack result = getRecipeManager()
            .getFirstMatch(RecipeType.CRAFTING, input, world) // Get the first matching recipe
            .map(recipe -> recipe.value().assemble(input, network.registryAccess())) // Craft the recipe to get the result
            .orElse(ItemStack.EMPTY); // If there is no recipe, set the result to air

        // Update the result slot
        network.handleContainerSetSlot(new ClientboundContainerSetSlotPacket(syncId, screenHandler.getStateId(), 0, result));
    }

    private static Item getItemById(final ResourceLocation id) {
        final Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(null);
        if (item == null) {
            throw new IllegalStateException("Unknown item: " + id.toString());
        }

        return item;
    }

    private sealed interface LegacyRecipe permits LegacyShapedRecipe, LegacyShapelessRecipe, LegacySmeltingRecipe {
    }

    private record RecipeItemStack(Item item, int count) {

        private static RecipeItemStack fromJson(final JsonObject obj) {
            final ResourceLocation id = ResourceLocation.parse(obj.get("id").getAsString());
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
                    items.add(getItemById(ResourceLocation.parse(itemId.getAsString())));
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
                    items.add(getItemById(ResourceLocation.parse(itemId.getAsString())));
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
                input.add(getItemById(ResourceLocation.parse(element.getAsString())));
            }
            final float experience = obj.get("experience").getAsFloat();
            return new LegacySmeltingRecipe(group, result, input, experience);
        }

    }

}
