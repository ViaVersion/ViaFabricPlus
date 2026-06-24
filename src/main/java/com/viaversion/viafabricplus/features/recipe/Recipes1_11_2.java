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

package com.viaversion.viafabricplus.features.recipe;

import com.mojang.datafixers.util.Pair;
import com.viaversion.viafabricplus.features.recipe.custom.AddBannerPatternRecipe;
import com.viaversion.viafabricplus.features.recipe.custom.ShulkerBoxColoringRecipe;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.DyeRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.ImbueRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.level.ItemLike;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

/**
 * Recipe data dump for all versions below 1.12.
 */
public final class Recipes1_11_2 {

    private static final List<Pair<LegacyRecipe, ProtocolVersionRange>> LEGACY_RECIPES = new ArrayList<>();
    private static RecipeManager1_11_2 RECIPE_MANAGER;

    public static void init() {
        if (!LEGACY_RECIPES.isEmpty()) {
            throw new IllegalStateException("Recipes1_11_2 is already initialized");
        }

        final JsonArray recipes = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("recipes-1.11.2.json").getAsJsonArray("");
        for (JsonElement recipeElement : recipes) {
            final String type = recipeElement.getAsJsonObject().get("type").getAsString();
            final ProtocolVersionRange versionRange = ProtocolVersionRange.fromString(recipeElement.getAsJsonObject().get("version").getAsString());
            switch (type) {
                case "shaped" -> LEGACY_RECIPES.add(new Pair<>(LegacyShapedRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                case "shapeless" -> LEGACY_RECIPES.add(new Pair<>(LegacyShapelessRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                case "smelting" -> LEGACY_RECIPES.add(new Pair<>(LegacySmeltingRecipe.fromJson(recipeElement.getAsJsonObject()), versionRange));
                default -> throw new IllegalArgumentException("Unknown recipe type: " + type);
            }
        }
    }

    public static RecipeManager1_11_2 getRecipeManager(final RegistryAccess.Frozen registryAccess) {
        if (RECIPE_MANAGER == null) {
            final List<RecipeHolder<?>> recipes = new ArrayList<>();

            // Regular recipes
            for (int i = 0; i < LEGACY_RECIPES.size(); i++) {
                final Pair<LegacyRecipe, ProtocolVersionRange> legacyRecipe = LEGACY_RECIPES.get(i);
                if (legacyRecipe.getSecond().contains(ProtocolTranslator.getTargetVersion())) {
                    final ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("viafabricplus", "recipe/" + i));
                    switch (legacyRecipe.getFirst()) {
                        case LegacyShapedRecipe legacyShapedRecipe -> {
                            final Map<Character, Ingredient> ingredients = new HashMap<>();
                            for (Map.Entry<Character, List<Item>> entry : legacyShapedRecipe.legend.entrySet()) {
                                final ItemLike[] items = new ItemLike[entry.getValue().size()];
                                for (int j = 0; j < entry.getValue().size(); j++) {
                                    items[j] = entry.getValue().get(j);
                                }
                                ingredients.put(entry.getKey(), Ingredient.of(items));
                            }
                            final ItemStackTemplate output = legacyShapedRecipe.result.toItemStack();
                            final Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(false);
                            final CraftingRecipe.CraftingBookInfo craftingBookInfo = new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, legacyShapedRecipe.group);
                            final CraftingRecipe recipe = new ShapedRecipe(commonInfo, craftingBookInfo, ShapedRecipePattern.of(ingredients, legacyShapedRecipe.pattern), output);
                            recipes.add(new RecipeHolder<>(key, recipe));
                        }
                        case LegacyShapelessRecipe legacyShapelessRecipe -> {
                            final ItemStackTemplate output = legacyShapelessRecipe.result.toItemStack();
                            final List<Ingredient> ingredients = new ArrayList<>();
                            for (List<Item> ingredientIds : legacyShapelessRecipe.ingredients) {
                                final ItemLike[] items = new ItemLike[ingredientIds.size()];
                                for (int j = 0; j < ingredientIds.size(); j++) {
                                    items[j] = ingredientIds.get(j);
                                }
                                ingredients.add(Ingredient.of(items));
                            }
                            final Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(false);
                            final CraftingRecipe.CraftingBookInfo craftingBookInfo = new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, legacyShapelessRecipe.group);
                            final CraftingRecipe recipe = new ShapelessRecipe(commonInfo, craftingBookInfo, output, ingredients);
                            recipes.add(new RecipeHolder<>(key, recipe));
                        }
                        case LegacySmeltingRecipe legacySmeltingRecipe -> {
                            final ItemStackTemplate output = legacySmeltingRecipe.result.toItemStack();
                            final ItemLike[] inputItems = new ItemLike[legacySmeltingRecipe.input.size()];
                            for (int j = 0; j < legacySmeltingRecipe.input.size(); j++) {
                                inputItems[j] = legacySmeltingRecipe.input.get(j);
                            }
                            final Ingredient input = Ingredient.of(inputItems);
                            final Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(false);
                            final AbstractCookingRecipe.CookingBookInfo cookingBookInfo = new AbstractCookingRecipe.CookingBookInfo(CookingBookCategory.MISC, "");
                            final SmeltingRecipe recipe = new SmeltingRecipe(commonInfo, cookingBookInfo, input, output, legacySmeltingRecipe.experience, 200);
                            recipes.add(new RecipeHolder<>(key, recipe));
                        }
                        default -> throw new IllegalStateException("Unknown legacy recipe type: " + legacyRecipe.getFirst().getClass());
                    }
                }
            }

            // Special recipes
            final List<CraftingRecipe> specialRecipes = new ArrayList<>();
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
                specialRecipes.add(createDyeRecipe(registryAccess, Items.LEATHER_HELMET));
                specialRecipes.add(createDyeRecipe(registryAccess, Items.LEATHER_CHESTPLATE));
                specialRecipes.add(createDyeRecipe(registryAccess, Items.LEATHER_LEGGINGS));
                specialRecipes.add(createDyeRecipe(registryAccess, Items.LEATHER_BOOTS));
                specialRecipes.add(createDyeRecipe(registryAccess, Items.LEATHER_HORSE_ARMOR));
                specialRecipes.add(new TransmuteRecipe(RecipeBuilder.createCraftingCommonInfo(true), RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "map_cloning"), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.MAP), TransmuteRecipe.FULL_RANGE_MATERIAL_COUNT, new ItemStackTemplate(Items.FILLED_MAP), true));
                specialRecipes.add(new MapExtendingRecipe(Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), new ItemStackTemplate(Items.FILLED_MAP)));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
                specialRecipes.add(new FireworkRocketRecipe(Ingredient.of(Items.PAPER), Ingredient.of(Items.GUNPOWDER), Ingredient.of(Items.FIREWORK_STAR), new ItemStackTemplate(Items.FIREWORK_ROCKET, 3)));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_11)) {
                specialRecipes.add(new ShulkerBoxColoringRecipe());
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_9)) {
                specialRecipes.add(new ImbueRecipe(RecipeBuilder.createCraftingCommonInfo(true), RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "tipped_arrow"), Ingredient.of(Items.LINGERING_POTION), Ingredient.of(Items.ARROW), new ItemStackTemplate(Items.TIPPED_ARROW, 8)));
                specialRecipes.add(new ShieldDecorationRecipe(Ingredient.of(registryAccess.getOrThrow(ItemTags.BANNERS)), Ingredient.of(Items.SHIELD), new ItemStackTemplate(Items.SHIELD)));
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_8)) {
                specialRecipes.add(new RepairItemRecipe());
                Items.BANNER.forEach(item -> specialRecipes.add(new BannerDuplicateRecipe(Ingredient.of(item), new ItemStackTemplate(item))));
                specialRecipes.add(new AddBannerPatternRecipe());
            }
            if (ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_7_2)) {
                specialRecipes.add(new BookCloningRecipe(Ingredient.of(Items.WRITTEN_BOOK), Ingredient.of(Items.WRITABLE_BOOK), BookCloningRecipe.DEFAULT_BOOK_GENERATION_RANGES, new ItemStackTemplate(Items.WRITTEN_BOOK)));
            }
            int index = 0;
            for (CraftingRecipe specialRecipe : specialRecipes) {
                final ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("viafabricplus", "recipe/special_" + specialRecipe.getClass().getSimpleName().replace("Recipe", "").toLowerCase(Locale.ROOT) + (index == 0 ? "" : index)));
                recipes.add(new RecipeHolder<>(key, specialRecipe));
                index++;
            }

            RECIPE_MANAGER = new RecipeManager1_11_2(recipes);
        }

        return RECIPE_MANAGER;
    }

    private static DyeRecipe createDyeRecipe(final RegistryAccess.Frozen registryAccess, final Item result) {
        final Recipe.CommonInfo commonInfo = RecipeBuilder.createCraftingCommonInfo(true);
        final CraftingRecipe.CraftingBookInfo craftingBookInfo = RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, "dyed_armor");
        final Ingredient ingredient = Ingredient.of(registryAccess.getOrThrow(ItemTags.DYES));
        return new DyeRecipe(commonInfo, craftingBookInfo, Ingredient.of(result), ingredient, new ItemStackTemplate(result));
    }

    public static void reset() {
        RECIPE_MANAGER = null;
    }

    /**
     * Sets the result slot of a crafting screen handler to the correct item stack. In Minecraft versions up to 1.11.2 the result slot
     * is not updated when the input slots change, so we need to update it manually, Spigot and Paper re-sync the slot,
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

        final ItemStack result = getRecipeManager(network.registryAccess())
            .getFirstMatch(RecipeType.CRAFTING, input, world) // Get the first matching recipe
            .map(recipe -> recipe.value().assemble(input)) // Craft the recipe to get the result
            .orElse(ItemStack.EMPTY); // If there is no recipe, set the result to air

        // Update the result slot
        network.handleContainerSetSlot(new ClientboundContainerSetSlotPacket(syncId, screenHandler.getStateId(), 0, result));
    }

    private static Item getItemById(final Identifier id) {
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
            final Identifier id = Identifier.parse(obj.get("id").getAsString());
            final int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            return new RecipeItemStack(getItemById(id), count);
        }

        private ItemStackTemplate toItemStack() {
            return new ItemStackTemplate(this.item, this.count);
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
                    items.add(getItemById(Identifier.parse(itemId.getAsString())));
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
                    items.add(getItemById(Identifier.parse(itemId.getAsString())));
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
                input.add(getItemById(Identifier.parse(element.getAsString())));
            }
            final float experience = obj.get("experience").getAsFloat();
            return new LegacySmeltingRecipe(group, result, input, experience);
        }

    }

}
