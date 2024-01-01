/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe data dump for all versions below 1.12.
 */
public class Recipes1_11_2 {

    /**
     * @return A list of all recipes for the given version.
     */
    public static List<RecipeInfo<?>> getRecipes(final VersionEnum targetVersion) {
        final List<RecipeInfo<?>> recipes = new ArrayList<>();

        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_4_2)) {
            recipes.add(RecipeInfo.of(() -> new ArmorDyeRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.ARMOR_DYE, Items.LEATHER_HELMET));
            recipes.add(RecipeInfo.of(() -> new MapCloningRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.MAP_CLONING, Items.FILLED_MAP, 2));
            recipes.add(RecipeInfo.of(() -> new MapExtendingRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.MAP_EXTENDING, Items.FILLED_MAP));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_4_6tor1_4_7)) {
            recipes.add(RecipeInfo.of(() -> new FireworkRocketRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.FIREWORK_ROCKET, Items.FIREWORK_ROCKET));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_11)) {
            recipes.add(RecipeInfo.of(() -> new ShulkerBoxColoringRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.SHULKER_BOX, Items.WHITE_SHULKER_BOX));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_9)) {
            recipes.add(RecipeInfo.of(() -> new TippedArrowRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.TIPPED_ARROW, Items.TIPPED_ARROW));
            recipes.add(RecipeInfo.of(() -> new ShieldDecorationRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.SHIELD_DECORATION, Items.SHIELD));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_8)) {
            recipes.add(RecipeInfo.of(() -> new RepairItemRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.REPAIR_ITEM, Items.WOODEN_SWORD));
            recipes.add(RecipeInfo.of(() -> new BannerDuplicateRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.BANNER_DUPLICATE, Items.WHITE_BANNER, 2));
            recipes.add(RecipeInfo.of(() -> new AddBannerPatternRecipe(CraftingRecipeCategory.MISC), AddBannerPatternRecipe.SERIALIZER, Items.WHITE_BANNER));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_7_2tor1_7_5)) {
            recipes.add(RecipeInfo.of(() -> new BookCloningRecipe(CraftingRecipeCategory.MISC), RecipeSerializer.BOOK_CLONING, Items.WRITABLE_BOOK, 2));
        }

        recipes.add(RecipeInfo.shaped(Items.WOODEN_SWORD, "X", "X", "#", '#', Items.STICK, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.WOODEN_SHOVEL, "X", "#", "#", '#', Items.STICK, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.WOODEN_PICKAXE, "XXX", " # ", " # ", '#', Items.STICK, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.WOODEN_HOE, "XX", " #", " #", '#', Items.STICK, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.WOODEN_AXE, "XX", "X#", " #", '#', Items.STICK, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.STONE_SWORD, "X", "X", "#", '#', Items.STICK, 'X', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(4, Blocks.COBBLESTONE_STAIRS, "#  ", "## ", "###", '#', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(Items.STONE_SHOVEL, "X", "#", "#", '#', Items.STICK, 'X', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(Items.STONE_PICKAXE, "XXX", " # ", " # ", '#', Items.STICK, 'X', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(Items.STONE_HOE, "XX", " #", " #", '#', Items.STICK, 'X', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(Blocks.STONE_BUTTON, "#", '#', Blocks.STONE));
        recipes.add(RecipeInfo.shaped(Items.STONE_AXE, "XX", "X#", " #", '#', Items.STICK, 'X', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(4, Items.STICK, "#", "#", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Blocks.SNOW_BLOCK, "##", "##", '#', Items.SNOWBALL));
        recipes.add(RecipeInfo.shaped(Blocks.REDSTONE_TORCH, "X", "#", '#', Items.STICK, 'X', Blocks.REDSTONE_WIRE));
        recipes.add(RecipeInfo.shaped(16, Blocks.RAIL, "X X", "X#X", "X X", '#', Items.STICK, 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(3, Items.PAPER, "###", '#', Blocks.SUGAR_CANE));
        recipes.add(RecipeInfo.shaped(Items.PAINTING, "###", "#X#", "###", '#', Items.STICK, 'X', Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL));
        recipes.add(RecipeInfo.shaped("wooden_stairs", 4, Blocks.OAK_STAIRS, "#  ", "## ", "###", '#', Blocks.OAK_PLANKS));
        recipes.add(RecipeInfo.shaped("planks", 4, Blocks.OAK_PLANKS, "#", '#', Blocks.OAK_LOG));
        recipes.add(RecipeInfo.shaped(Items.MINECART, "# #", "###", '#', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Blocks.LEVER, "X", "#", '#', Blocks.COBBLESTONE, 'X', Items.STICK));
        recipes.add(RecipeInfo.shaped(Items.LEATHER_LEGGINGS, "XXX", "X X", "X X", 'X', Items.LEATHER));
        recipes.add(RecipeInfo.shaped(Items.LEATHER_HELMET, "XXX", "X X", 'X', Items.LEATHER));
        recipes.add(RecipeInfo.shaped(Items.LEATHER_CHESTPLATE, "X X", "XXX", "XXX", 'X', Items.LEATHER));
        recipes.add(RecipeInfo.shaped(Items.LEATHER_BOOTS, "X X", "X X", 'X', Items.LEATHER));
        recipes.add(RecipeInfo.shaped(Blocks.JUKEBOX, "###", "#X#", "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.IRON_SWORD, "X", "X", "#", '#', Items.STICK, 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_SHOVEL, "X", "#", "#", '#', Items.STICK, 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_PICKAXE, "XXX", " # ", " # ", '#', Items.STICK, 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_LEGGINGS, "XXX", "X X", "X X", 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped("iron_ingot", 9, Items.IRON_INGOT, "#", '#', Blocks.IRON_BLOCK));
        recipes.add(RecipeInfo.shaped(Items.IRON_HOE, "XX", " #", " #", '#', Items.STICK, 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_HELMET, "XXX", "X X", 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_CHESTPLATE, "X X", "XXX", "XXX", 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_BOOTS, "X X", "X X", 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Blocks.IRON_BLOCK, "###", "###", "###", '#', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.IRON_AXE, "XX", "X#", " #", '#', Items.STICK, 'X', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_SWORD, "X", "X", "#", '#', Items.STICK, 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_SHOVEL, "X", "#", "#", '#', Items.STICK, 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_PICKAXE, "XXX", " # ", " # ", '#', Items.STICK, 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_LEGGINGS, "XXX", "X X", "X X", 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_HOE, "XX", " #", " #", '#', Items.STICK, 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_HELMET, "XXX", "X X", 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_CHESTPLATE, "X X", "XXX", "XXX", 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_BOOTS, "X X", "X X", 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.GOLDEN_AXE, "XX", "X#", " #", '#', Items.STICK, 'X', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped("gold_ingot", 9, Items.GOLD_INGOT, "#", '#', Blocks.GOLD_BLOCK));
        recipes.add(RecipeInfo.shaped(Blocks.GOLD_BLOCK, "###", "###", "###", '#', Items.GOLD_INGOT));
        recipes.add(RecipeInfo.shaped(Items.FURNACE_MINECART, "A", "B", 'A', Blocks.FURNACE, 'B', Items.MINECART));
        recipes.add(RecipeInfo.shaped(Blocks.FURNACE, "###", "# #", "###", '#', Blocks.COBBLESTONE));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_SWORD, "X", "X", "#", '#', Items.STICK, 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_SHOVEL, "X", "#", "#", '#', Items.STICK, 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_PICKAXE, "XXX", " # ", " # ", '#', Items.STICK, 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_LEGGINGS, "XXX", "X X", "X X", 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_HOE, "XX", " #", " #", '#', Items.STICK, 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_HELMET, "XXX", "X X", 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_CHESTPLATE, "X X", "XXX", "XXX", 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_BOOTS, "X X", "X X", 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Blocks.DIAMOND_BLOCK, "###", "###", "###", '#', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(Items.DIAMOND_AXE, "XX", "X#", " #", '#', Items.STICK, 'X', Items.DIAMOND));
        recipes.add(RecipeInfo.shaped(9, Items.DIAMOND, "#", '#', Blocks.DIAMOND_BLOCK));
        recipes.add(RecipeInfo.shaped(Blocks.CRAFTING_TABLE, "##", "##", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Blocks.CLAY, "##", "##", '#', Items.CLAY_BALL));
        recipes.add(RecipeInfo.shaped(Items.CHEST_MINECART, "A", "B", 'A', Blocks.CHEST, 'B', Items.MINECART));
        recipes.add(RecipeInfo.shaped(Blocks.CHEST, "###", "# #", "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.BUCKET, "# #", " # ", '#', Items.IRON_INGOT));
        recipes.add(RecipeInfo.shaped(Blocks.BRICKS, "##", "##", '#', Items.BRICK));
        recipes.add(RecipeInfo.shaped(Items.BREAD, "###", '#', Items.WHEAT));
        recipes.add(RecipeInfo.shaped(4, Items.BOWL, "# #", " # ", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        recipes.add(RecipeInfo.shaped(Items.BOW, " #X", "# X", " #X", '#', Items.STICK, 'X', Blocks.TRIPWIRE));
        recipes.add(RecipeInfo.shaped(Blocks.BOOKSHELF, "###", "XXX", "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'X', Items.BOOK));
        recipes.add(RecipeInfo.shaped(4, Items.ARROW, "X", "#", "Y", '#', Items.STICK, 'X', Items.FLINT, 'Y', Items.FEATHER));

        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_12)) {
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.YELLOW_CONCRETE_POWDER, Items.YELLOW_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.WHITE_CONCRETE_POWDER, Items.BONE_MEAL, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.RED_CONCRETE_POWDER, Items.RED_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.PURPLE_CONCRETE_POWDER, Items.PURPLE_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.PINK_CONCRETE_POWDER, Items.PINK_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.ORANGE_CONCRETE_POWDER, Items.ORANGE_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.LIME_CONCRETE_POWDER, Items.LIME_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.GREEN_CONCRETE_POWDER, Items.GREEN_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.GRAY_CONCRETE_POWDER, Items.GRAY_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.CYAN_CONCRETE_POWDER, Items.CYAN_DYE, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.BROWN_CONCRETE_POWDER, Blocks.COCOA, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.BLUE_CONCRETE_POWDER, Items.LAPIS_LAZULI, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("concrete_powder", 8, Blocks.BLACK_CONCRETE_POWDER, Items.INK_SAC, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.SAND, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL, Blocks.GRAVEL));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.YELLOW_BED, Blocks.WHITE_BED, Items.YELLOW_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.RED_BED, Blocks.WHITE_BED, Items.RED_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.PURPLE_BED, Blocks.WHITE_BED, Items.PURPLE_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.PINK_BED, Blocks.WHITE_BED, Items.PINK_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.ORANGE_BED, Blocks.WHITE_BED, Items.ORANGE_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.MAGENTA_BED, Blocks.WHITE_BED, Items.MAGENTA_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.LIME_BED, Blocks.WHITE_BED, Items.LIME_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.LIGHT_GRAY_BED, Blocks.WHITE_BED, Items.LIGHT_GRAY_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.LIGHT_BLUE_BED, Blocks.WHITE_BED, Items.LIGHT_BLUE_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.GREEN_BED, Blocks.WHITE_BED, Items.GREEN_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.GRAY_BED, Blocks.WHITE_BED, Items.GRAY_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.CYAN_BED, Blocks.WHITE_BED, Items.CYAN_DYE));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.BROWN_BED, Blocks.WHITE_BED, Blocks.COCOA));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.BLUE_BED, Blocks.WHITE_BED, Items.LAPIS_LAZULI));
            recipes.add(RecipeInfo.shapeless("dyed_bed", Blocks.BLACK_BED, Blocks.WHITE_BED, Items.INK_SAC));
            recipes.add(RecipeInfo.shaped("bed", Blocks.YELLOW_BED, "###", "XXX", '#', Blocks.YELLOW_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.WHITE_BED, "###", "XXX", '#', Blocks.WHITE_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.RED_BED, "###", "XXX", '#', Blocks.RED_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.PURPLE_BED, "###", "XXX", '#', Blocks.PURPLE_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.PINK_BED, "###", "XXX", '#', Blocks.PINK_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.ORANGE_BED, "###", "XXX", '#', Blocks.ORANGE_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.MAGENTA_BED, "###", "XXX", '#', Blocks.MAGENTA_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.LIME_BED, "###", "XXX", '#', Blocks.LIME_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.LIGHT_GRAY_BED, "###", "XXX", '#', Blocks.LIGHT_GRAY_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.LIGHT_BLUE_BED, "###", "XXX", '#', Blocks.LIGHT_BLUE_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.GREEN_BED, "###", "XXX", '#', Blocks.GREEN_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.GRAY_BED, "###", "XXX", '#', Blocks.GRAY_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.CYAN_BED, "###", "XXX", '#', Blocks.CYAN_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.BROWN_BED, "###", "XXX", '#', Blocks.BROWN_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.BLUE_BED, "###", "XXX", '#', Blocks.BLUE_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("bed", Blocks.BLACK_BED, "###", "XXX", '#', Blocks.BLACK_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
        } else {
            recipes.add(RecipeInfo.shapeless(Blocks.WHITE_WOOL, Blocks.WHITE_WOOL, Items.BONE_MEAL));
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_3tob1_3_1)) {
                recipes.add(RecipeInfo.shaped("bed", Blocks.RED_BED, "###", "XXX", '#', Blocks.YELLOW_WOOL, Blocks.BLACK_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.CYAN_WOOL, Blocks.GRAY_WOOL, Blocks.GREEN_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.WHITE_WOOL, Blocks.RED_WOOL, Blocks.PURPLE_WOOL, Blocks.PINK_WOOL, Blocks.ORANGE_WOOL, Blocks.LIME_WOOL, Blocks.MAGENTA_WOOL, 'X', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            }
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_11_1to1_11_2)) {
            recipes.add(RecipeInfo.shaped(9, Items.IRON_NUGGET, "#", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped("iron_ingot", Items.IRON_INGOT, "###", "###", "###", '#', Items.IRON_NUGGET).distinguisher("iron_nugget_to_ingot"));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_11)) {
            recipes.add(RecipeInfo.shaped(Blocks.OBSERVER, "###", "RRQ", "###", 'Q', Items.QUARTZ, 'R', Blocks.REDSTONE_WIRE, '#', Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shaped(Blocks.PURPLE_SHULKER_BOX, "-", "#", "-", '#', Blocks.CHEST, '-', Items.SHULKER_SHELL));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_10)) {
            recipes.add(RecipeInfo.shapeless("bonemeal", 9, Items.BONE_MEAL, Blocks.BONE_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.BONE_BLOCK, "XXX", "XXX", "XXX", 'X', Items.BONE_MEAL));
            recipes.add(RecipeInfo.shaped(Blocks.MAGMA_BLOCK, "##", "##", '#', Items.MAGMA_CREAM));
            recipes.add(RecipeInfo.shaped(Blocks.NETHER_WART_BLOCK, "###", "###", "###", '#', Blocks.NETHER_WART));
            recipes.add(RecipeInfo.shaped(Blocks.RED_NETHER_BRICKS, "NW", "WN", 'W', Blocks.NETHER_WART, 'N', Items.NETHER_BRICK));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_9)) {
            recipes.add(RecipeInfo.shapeless(Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.TRIPWIRE_HOOK));
            recipes.add(RecipeInfo.shaped(Items.SHIELD, "WoW", "WWW", " W ", 'W', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'o', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(4, Blocks.PURPUR_BLOCK, "FF", "FF", 'F', Items.POPPED_CHORUS_FRUIT));
            recipes.add(RecipeInfo.shaped(6, Blocks.PURPUR_SLAB, "###", '#', Blocks.PURPUR_BLOCK));
            recipes.add(RecipeInfo.shaped(4, Blocks.PURPUR_STAIRS, "#  ", "## ", "###", '#', Blocks.PURPUR_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.PURPUR_PILLAR, "#", "#", '#', Blocks.PURPUR_SLAB));
            recipes.add(RecipeInfo.shaped(4, Blocks.END_STONE_BRICKS, "##", "##", '#', Blocks.END_STONE));
            recipes.add(RecipeInfo.shaped("boat", Items.SPRUCE_BOAT, "# #", "###", '#', Blocks.SPRUCE_PLANKS));
            recipes.add(RecipeInfo.shaped("boat", Items.JUNGLE_BOAT, "# #", "###", '#', Blocks.JUNGLE_PLANKS));
            recipes.add(RecipeInfo.shaped("boat", Items.OAK_BOAT, "# #", "###", '#', Blocks.OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("boat", Items.DARK_OAK_BOAT, "# #", "###", '#', Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("boat", Items.BIRCH_BOAT, "# #", "###", '#', Blocks.BIRCH_PLANKS));
            recipes.add(RecipeInfo.shaped("boat", Items.ACACIA_BOAT, "# #", "###", '#', Blocks.ACACIA_PLANKS));
            recipes.add(RecipeInfo.shaped(4, Blocks.END_ROD, "/", "#", '#', Items.POPPED_CHORUS_FRUIT, '/', Items.BLAZE_ROD));
            recipes.add(RecipeInfo.shaped(Items.END_CRYSTAL, "GGG", "GEG", "GTG", 'T', Items.GHAST_TEAR, 'E', Items.ENDER_EYE, 'G', Blocks.GLASS));
            recipes.add(RecipeInfo.shaped(2, Items.SPECTRAL_ARROW, " # ", "#X#", " # ", '#', Items.GLOWSTONE_DUST, 'X', Items.ARROW));
            recipes.add(RecipeInfo.shapeless("red_dye", Items.RED_DYE, Items.BEETROOT));
            recipes.add(RecipeInfo.shaped(Items.BEETROOT_SOUP, "OOO", "OOO", " B ", 'B', Items.BOWL, 'O', Items.BEETROOT));
        } else {
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_5tor1_5_1)) {
                recipes.add(RecipeInfo.shaped(Blocks.TRAPPED_CHEST, "#-", '#', Blocks.CHEST, '-', Blocks.TRIPWIRE_HOOK));
            }
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_3_1tor1_3_2)) {
                recipes.add(RecipeInfo.shaped(Items.ENCHANTED_GOLDEN_APPLE, "###", "#X#", "###", '#', Items.GOLD_BLOCK, 'X', Items.APPLE));
            }
            recipes.add(RecipeInfo.shaped("boat", Items.OAK_BOAT, "# #", "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_8)) {
            recipes.add(RecipeInfo.shapeless(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE, Blocks.VINE));
            recipes.add(RecipeInfo.shapeless(Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.VINE));
            recipes.add(RecipeInfo.shaped(Blocks.CHISELED_STONE_BRICKS, "#", "#", '#', Blocks.STONE_BRICK_SLAB));
            recipes.add(RecipeInfo.shaped(4, Blocks.COARSE_DIRT, "DG", "GD", 'D', Blocks.DIRT, 'G', Blocks.GRAVEL));
            recipes.add(RecipeInfo.shaped("wooden_fence", 3, Blocks.SPRUCE_FENCE, "W#W", "W#W", '#', Items.STICK, 'W', Blocks.SPRUCE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence", 3, Blocks.JUNGLE_FENCE, "W#W", "W#W", '#', Items.STICK, 'W', Blocks.JUNGLE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence", 3, Blocks.OAK_FENCE, "W#W", "W#W", '#', Items.STICK, 'W', Blocks.OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence", 3, Blocks.DARK_OAK_FENCE, "W#W", "W#W", '#', Items.STICK, 'W', Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence", 3, Blocks.BIRCH_FENCE, "W#W", "W#W", '#', Items.STICK, 'W', Blocks.BIRCH_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence", 3, Blocks.ACACIA_FENCE, "W#W", "W#W", '#', Items.STICK, 'W', Blocks.ACACIA_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.SPRUCE_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.SPRUCE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.JUNGLE_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.JUNGLE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.OAK_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.DARK_OAK_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.BIRCH_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.BIRCH_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.ACACIA_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.ACACIA_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_door", 3, Blocks.OAK_DOOR, "##", "##", "##", '#', Blocks.OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_door", 3, Blocks.SPRUCE_DOOR, "##", "##", "##", '#', Blocks.SPRUCE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_door", 3, Blocks.JUNGLE_DOOR, "##", "##", "##", '#', Blocks.JUNGLE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_door", 3, Blocks.DARK_OAK_DOOR, "##", "##", "##", '#', Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_door", 3, Blocks.BIRCH_DOOR, "##", "##", "##", '#', Blocks.BIRCH_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_door", 3, Blocks.ACACIA_DOOR, "##", "##", "##", '#', Blocks.ACACIA_PLANKS));
            recipes.add(RecipeInfo.shaped("banner", Blocks.YELLOW_BANNER, "###", "###", " | ", '#', Blocks.YELLOW_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.RED_BANNER, "###", "###", " | ", '#', Blocks.RED_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.PURPLE_BANNER, "###", "###", " | ", '#', Blocks.PURPLE_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.PINK_BANNER, "###", "###", " | ", '#', Blocks.PINK_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.ORANGE_BANNER, "###", "###", " | ", '#', Blocks.ORANGE_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.MAGENTA_BANNER, "###", "###", " | ", '#', Blocks.MAGENTA_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.LIME_BANNER, "###", "###", " | ", '#', Blocks.LIME_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.LIGHT_GRAY_BANNER, "###", "###", " | ", '#', Blocks.LIGHT_GRAY_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.LIGHT_BLUE_BANNER, "###", "###", " | ", '#', Blocks.LIGHT_BLUE_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.GREEN_BANNER, "###", "###", " | ", '#', Blocks.GREEN_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.GRAY_BANNER, "###", "###", " | ", '#', Blocks.GRAY_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.CYAN_BANNER, "###", "###", " | ", '#', Blocks.CYAN_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.BROWN_BANNER, "###", "###", " | ", '#', Blocks.BROWN_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.BLUE_BANNER, "###", "###", " | ", '#', Blocks.BLUE_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.BLACK_BANNER, "###", "###", " | ", '#', Blocks.BLACK_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("banner", Blocks.WHITE_BANNER, "###", "###", " | ", '#', Blocks.WHITE_WOOL, '|', Items.STICK));
            recipes.add(RecipeInfo.shaped("rabbit_stew", Items.RABBIT_STEW, " R ", "CPD", " B ", 'P', Items.BAKED_POTATO, 'R', Items.COOKED_RABBIT, 'B', Items.BOWL, 'C', Blocks.CARROTS, 'D', Blocks.RED_MUSHROOM));
            recipes.add(RecipeInfo.shaped("rabbit_stew", Items.RABBIT_STEW, " R ", "CPM", " B ", 'P', Items.BAKED_POTATO, 'R', Items.COOKED_RABBIT, 'B', Items.BOWL, 'C', Blocks.CARROTS, 'M', Blocks.BROWN_MUSHROOM));
            recipes.add(RecipeInfo.shaped(3, Blocks.IRON_DOOR, "##", "##", "##", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.IRON_TRAPDOOR, "##", "##", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.RED_SANDSTONE, "##", "##", '#', Blocks.RED_SAND));
            recipes.add(RecipeInfo.shaped(4, Blocks.CUT_RED_SANDSTONE, "##", "##", '#', Blocks.RED_SANDSTONE));
            recipes.add(RecipeInfo.shaped(6, Blocks.RED_SANDSTONE_SLAB, "###", '#', Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE));
            recipes.add(RecipeInfo.shaped(4, Blocks.RED_SANDSTONE_STAIRS, "#  ", "## ", "###", '#', Blocks.RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE));
            recipes.add(RecipeInfo.shaped(Blocks.CHISELED_RED_SANDSTONE, "#", "#", '#', Blocks.RED_SANDSTONE_SLAB));
            recipes.add(RecipeInfo.shaped(Items.LEATHER, "##", "##", '#', Items.RABBIT_HIDE));
            recipes.add(RecipeInfo.shaped(Items.ARMOR_STAND, "///", " / ", "/_/", '/', Items.STICK, '_', Blocks.SMOOTH_STONE_SLAB));
            recipes.add(RecipeInfo.shaped(Blocks.SEA_LANTERN, "SCS", "CCC", "SCS", 'S', Items.PRISMARINE_SHARD, 'C', Items.PRISMARINE_CRYSTALS));
            recipes.add(RecipeInfo.shaped(Blocks.PRISMARINE_BRICKS, "SSS", "SSS", "SSS", 'S', Items.PRISMARINE_SHARD));
            recipes.add(RecipeInfo.shaped(Blocks.PRISMARINE, "SS", "SS", 'S', Items.PRISMARINE_SHARD));
            recipes.add(RecipeInfo.shaped(Blocks.DARK_PRISMARINE, "SSS", "SIS", "SSS", 'S', Items.PRISMARINE_SHARD, 'I', Items.INK_SAC));
            recipes.add(RecipeInfo.shaped(9, Items.SLIME_BALL, "#", '#', Blocks.SLIME_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.SLIME_BLOCK, "###", "###", "###", '#', Items.SLIME_BALL));
            recipes.add(RecipeInfo.shaped(2, Blocks.DIORITE, "CQ", "QC", 'Q', Items.QUARTZ, 'C', Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shapeless(2, Blocks.ANDESITE, Blocks.DIORITE, Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shapeless(Blocks.GRANITE, Blocks.DIORITE, Items.QUARTZ));
            recipes.add(RecipeInfo.shaped(4, Blocks.POLISHED_GRANITE, "SS", "SS", 'S', Blocks.GRANITE));
            recipes.add(RecipeInfo.shaped(4, Blocks.POLISHED_DIORITE, "SS", "SS", 'S', Blocks.DIORITE));
            recipes.add(RecipeInfo.shaped(4, Blocks.POLISHED_ANDESITE, "SS", "SS", 'S', Blocks.ANDESITE));
        } else {
            recipes.add(RecipeInfo.shaped("wooden_fence", 2, Blocks.OAK_FENCE, "###", "###", '#', Items.STICK));
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
                recipes.add(RecipeInfo.shaped("wooden_fence_gate", Blocks.OAK_FENCE_GATE, "#W#", "#W#", '#', Items.STICK, 'W', Blocks.OAK_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS));
            }
            recipes.add(RecipeInfo.shaped(Blocks.IRON_DOOR, "##", "##", "##", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped("wooden_door", Blocks.OAK_DOOR, "##", "##", "##", '#', Blocks.OAK_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.DARK_OAK_PLANKS, Blocks.BIRCH_PLANKS, Blocks.ACACIA_PLANKS));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_7_2tor1_7_5)) {
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.YELLOW_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.YELLOW_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.WHITE_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.BONE_MEAL));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.RED_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.RED_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.PURPLE_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.PURPLE_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.PINK_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.PINK_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.ORANGE_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.ORANGE_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.MAGENTA_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.MAGENTA_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.LIME_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.LIME_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.LIGHT_GRAY_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.LIGHT_GRAY_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.LIGHT_BLUE_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.LIGHT_BLUE_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.GREEN_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.GREEN_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.GRAY_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.GRAY_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.CYAN_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.CYAN_DYE));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.BROWN_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Blocks.COCOA));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.BLUE_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.LAPIS_LAZULI));
            recipes.add(RecipeInfo.shaped("stained_glass", 8, Blocks.BLACK_STAINED_GLASS, "###", "#X#", "###", '#', Blocks.GLASS, 'X', Items.INK_SAC));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.YELLOW_STAINED_GLASS_PANE, "###", "###", '#', Blocks.YELLOW_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.WHITE_STAINED_GLASS_PANE, "###", "###", '#', Blocks.WHITE_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.RED_STAINED_GLASS_PANE, "###", "###", '#', Blocks.RED_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.PURPLE_STAINED_GLASS_PANE, "###", "###", '#', Blocks.PURPLE_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.PINK_STAINED_GLASS_PANE, "###", "###", '#', Blocks.PINK_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.ORANGE_STAINED_GLASS_PANE, "###", "###", '#', Blocks.ORANGE_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.MAGENTA_STAINED_GLASS_PANE, "###", "###", '#', Blocks.MAGENTA_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.LIME_STAINED_GLASS_PANE, "###", "###", '#', Blocks.LIME_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, "###", "###", '#', Blocks.LIGHT_GRAY_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, "###", "###", '#', Blocks.LIGHT_BLUE_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.GREEN_STAINED_GLASS_PANE, "###", "###", '#', Blocks.GREEN_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.GRAY_STAINED_GLASS_PANE, "###", "###", '#', Blocks.GRAY_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.CYAN_STAINED_GLASS_PANE, "###", "###", '#', Blocks.CYAN_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.BROWN_STAINED_GLASS_PANE, "###", "###", '#', Blocks.BROWN_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.BLUE_STAINED_GLASS_PANE, "###", "###", '#', Blocks.BLUE_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("stained_glass_pane", 16, Blocks.BLACK_STAINED_GLASS_PANE, "###", "###", '#', Blocks.BLACK_STAINED_GLASS));
            recipes.add(RecipeInfo.shaped("planks", 4, Blocks.ACACIA_PLANKS, "#", '#', Blocks.ACACIA_LOG));
            recipes.add(RecipeInfo.shaped("planks", 4, Blocks.DARK_OAK_PLANKS, "#", '#', Blocks.DARK_OAK_LOG));
            recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.ACACIA_SLAB, "###", '#', Blocks.ACACIA_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_stairs", 4, Blocks.ACACIA_STAIRS, "#  ", "## ", "###", '#', Blocks.ACACIA_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.DARK_OAK_SLAB, "###", '#', Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_stairs", 4, Blocks.DARK_OAK_STAIRS, "#  ", "## ", "###", '#', Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped(Blocks.TNT, "X#X", "#X#", "X#X", '#', Blocks.SAND, Blocks.RED_SAND, 'X', Items.GUNPOWDER));
            recipes.add(RecipeInfo.shapeless("red_dye", Items.RED_DYE, Blocks.RED_TULIP));
            recipes.add(RecipeInfo.shapeless("orange_dye", Items.ORANGE_DYE, Blocks.ORANGE_TULIP));
            recipes.add(RecipeInfo.shapeless("light_gray_dye", Items.LIGHT_GRAY_DYE, Blocks.WHITE_TULIP));
            recipes.add(RecipeInfo.shapeless("pink_dye", Items.PINK_DYE, Blocks.PINK_TULIP));
            recipes.add(RecipeInfo.shapeless("light_blue_dye", Items.LIGHT_BLUE_DYE, Blocks.BLUE_ORCHID));
            recipes.add(RecipeInfo.shapeless("magenta_dye", Items.MAGENTA_DYE, Blocks.ALLIUM));
            recipes.add(RecipeInfo.shapeless("light_gray_dye", Items.LIGHT_GRAY_DYE, Blocks.AZURE_BLUET));
            recipes.add(RecipeInfo.shapeless("light_gray_dye", Items.LIGHT_GRAY_DYE, Blocks.OXEYE_DAISY));
            recipes.add(RecipeInfo.shapeless("yellow_dye", 2, Items.YELLOW_DYE, Blocks.SUNFLOWER));
            recipes.add(RecipeInfo.shapeless("pink_dye", 2, Items.PINK_DYE, Blocks.PEONY));
            recipes.add(RecipeInfo.shapeless("red_dye", 2, Items.RED_DYE, Blocks.ROSE_BUSH));
            recipes.add(RecipeInfo.shapeless("magenta_dye", 2, Items.MAGENTA_DYE, Blocks.LILAC));
            recipes.add(RecipeInfo.shapeless(Items.FLINT_AND_STEEL, Items.IRON_INGOT, Items.FLINT));
        } else {
            recipes.add(RecipeInfo.shaped(Items.FLINT_AND_STEEL, "A ", " B", 'A', Items.IRON_INGOT, 'B', Items.FLINT));
            recipes.add(RecipeInfo.shaped(Blocks.TNT, "X#X", "#X#", "X#X", '#', Blocks.SAND, 'X', Items.GUNPOWDER));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_6_1)) {
            recipes.add(RecipeInfo.shaped(Items.GOLDEN_APPLE, "###", "#X#", "###", '#', Items.GOLD_INGOT, 'X', Items.APPLE));
            recipes.add(RecipeInfo.shaped(Items.GLISTERING_MELON_SLICE, "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.MELON_SLICE));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.YELLOW_CARPET, "##", '#', Blocks.YELLOW_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.WHITE_CARPET, "##", '#', Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.RED_CARPET, "##", '#', Blocks.RED_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.PURPLE_CARPET, "##", '#', Blocks.PURPLE_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.PINK_CARPET, "##", '#', Blocks.PINK_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.ORANGE_CARPET, "##", '#', Blocks.ORANGE_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.MAGENTA_CARPET, "##", '#', Blocks.MAGENTA_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.LIME_CARPET, "##", '#', Blocks.LIME_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.LIGHT_GRAY_CARPET, "##", '#', Blocks.LIGHT_GRAY_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.LIGHT_BLUE_CARPET, "##", '#', Blocks.LIGHT_BLUE_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.GREEN_CARPET, "##", '#', Blocks.GREEN_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.GRAY_CARPET, "##", '#', Blocks.GRAY_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.CYAN_CARPET, "##", '#', Blocks.CYAN_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.BROWN_CARPET, "##", '#', Blocks.BROWN_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.BLUE_CARPET, "##", '#', Blocks.BLUE_WOOL));
            recipes.add(RecipeInfo.shaped("carpet", 3, Blocks.BLACK_CARPET, "##", '#', Blocks.BLACK_WOOL));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.YELLOW_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.YELLOW_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.WHITE_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.BONE_MEAL));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.RED_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.RED_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.PURPLE_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.PURPLE_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.PINK_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.PINK_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.ORANGE_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.ORANGE_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.MAGENTA_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.MAGENTA_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.LIME_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.LIME_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.LIGHT_GRAY_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.LIGHT_GRAY_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.LIGHT_BLUE_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.LIGHT_BLUE_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.GREEN_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.GREEN_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.GRAY_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.GRAY_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.CYAN_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.CYAN_DYE));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.BROWN_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Blocks.COCOA));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.BLUE_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.LAPIS_LAZULI));
            recipes.add(RecipeInfo.shaped("stained_hardened_clay", 8, Blocks.BLACK_TERRACOTTA, "###", "#X#", "###", '#', Blocks.TERRACOTTA, 'X', Items.INK_SAC));
            recipes.add(RecipeInfo.shaped(2, Items.LEAD, "~~ ", "~O ", "  ~", '~', Blocks.TRIPWIRE, 'O', Items.SLIME_BALL));
            recipes.add(RecipeInfo.shaped(Blocks.HAY_BLOCK, "###", "###", "###", '#', Items.WHEAT));
            recipes.add(RecipeInfo.shaped(9, Items.WHEAT, "#", '#', Blocks.HAY_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.COAL_BLOCK, "###", "###", "###", '#', Items.COAL));
            recipes.add(RecipeInfo.shaped(9, Items.COAL, "#", '#', Blocks.COAL_BLOCK));
        } else {
            recipes.add(RecipeInfo.shaped(Items.GOLDEN_APPLE, "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.APPLE));
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_0_0tor1_0_1)) {
                recipes.add(RecipeInfo.shapeless(Items.GLISTERING_MELON_SLICE, Items.GOLD_NUGGET, Items.MELON_SLICE));
            }
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_5tor1_5_1)) {
            recipes.add(RecipeInfo.shaped(6, Blocks.SNOW, "###", '#', Blocks.SNOW_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.QUARTZ_BLOCK, "##", "##", '#', Items.QUARTZ));
            recipes.add(RecipeInfo.shaped(2, Blocks.QUARTZ_PILLAR, "#", "#", '#', Blocks.QUARTZ_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.CHISELED_QUARTZ_BLOCK, "#", "#", '#', Blocks.QUARTZ_SLAB));
            recipes.add(RecipeInfo.shaped(6, Blocks.QUARTZ_SLAB, "###", '#', Blocks.QUARTZ_BLOCK, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR));
            recipes.add(RecipeInfo.shaped(6, Blocks.ACTIVATOR_RAIL, "XSX", "X#X", "XSX", '#', Blocks.REDSTONE_TORCH, 'S', Items.STICK, 'X', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Items.TNT_MINECART, "A", "B", 'A', Blocks.TNT, 'B', Items.MINECART));
            recipes.add(RecipeInfo.shaped(Items.HOPPER_MINECART, "A", "B", 'A', Blocks.HOPPER, 'B', Items.MINECART));
            recipes.add(RecipeInfo.shaped(4, Blocks.QUARTZ_STAIRS, "#  ", "## ", "###", '#', Blocks.QUARTZ_BLOCK, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_PILLAR));
            recipes.add(RecipeInfo.shaped(Blocks.COMPARATOR, " # ", "#X#", "III", '#', Blocks.REDSTONE_TORCH, 'X', Items.QUARTZ, 'I', Blocks.STONE));
            recipes.add(RecipeInfo.shaped(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, "##", '#', Items.GOLD_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, "##", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.DROPPER, "###", "# #", "#R#", 'R', Blocks.REDSTONE_WIRE, '#', Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shaped(Blocks.HOPPER, "I I", "ICI", " I ", 'C', Blocks.CHEST, 'I', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.NETHER_BRICKS, "NN", "NN", 'N', Items.NETHER_BRICK));
            recipes.add(RecipeInfo.shaped(Blocks.DAYLIGHT_DETECTOR, "GGG", "QQQ", "WWW", 'Q', Items.QUARTZ, 'G', Blocks.GLASS, 'W', Blocks.OAK_SLAB, Blocks.SPRUCE_SLAB, Blocks.BIRCH_SLAB, Blocks.JUNGLE_SLAB, Blocks.ACACIA_SLAB, Blocks.DARK_OAK_SLAB));
            recipes.add(RecipeInfo.shaped(Blocks.REDSTONE_BLOCK, "###", "###", "###", '#', Blocks.REDSTONE_WIRE));
            recipes.add(RecipeInfo.shaped(9, Blocks.REDSTONE_WIRE, "#", '#', Blocks.REDSTONE_BLOCK));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_4_6tor1_4_7)) {
            recipes.add(RecipeInfo.shaped(6, Blocks.NETHER_BRICK_SLAB, "###", '#', Blocks.NETHER_BRICKS));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_4_2)) {
            recipes.add(RecipeInfo.shaped(6, Blocks.COBBLESTONE_WALL, "###", "###", '#', Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shaped(6, Blocks.MOSSY_COBBLESTONE_WALL, "###", "###", '#', Blocks.MOSSY_COBBLESTONE));
            recipes.add(RecipeInfo.shaped(Blocks.FLOWER_POT, "# #", " # ", '#', Items.BRICK));
            recipes.add(RecipeInfo.shaped(Items.CARROT_ON_A_STICK, "# ", " X", '#', Items.FISHING_ROD, 'X', Blocks.CARROTS));
            recipes.add(RecipeInfo.shaped(Items.ITEM_FRAME, "###", "#X#", "###", '#', Items.STICK, 'X', Items.LEATHER));
            recipes.add(RecipeInfo.shaped(Items.GOLDEN_CARROT, "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Blocks.CARROTS));
            recipes.add(RecipeInfo.shaped(Blocks.OAK_BUTTON, "#", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped(Blocks.ANVIL, "III", " i ", "iii", 'I', Blocks.IRON_BLOCK, 'i', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.BEACON, "GGG", "GSG", "OOO", 'S', Items.NETHER_STAR, 'G', Blocks.GLASS, 'O', Blocks.OBSIDIAN));
            recipes.add(RecipeInfo.shapeless(Items.PUMPKIN_PIE, Blocks.CARVED_PUMPKIN, Items.SUGAR, Items.EGG));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_3_1tor1_3_2)) {
            recipes.add(RecipeInfo.shapeless(Items.WRITABLE_BOOK, Items.BOOK, Items.INK_SAC, Items.FEATHER));
            recipes.add(RecipeInfo.shapeless(Items.BOOK, Items.PAPER, Items.PAPER, Items.PAPER, Items.LEATHER));
            recipes.add(RecipeInfo.shaped(3, Blocks.OAK_SIGN, "###", "###", " X ", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'X', Items.STICK));
            recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.SPRUCE_SLAB, "###", '#', Blocks.SPRUCE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.BIRCH_SLAB, "###", '#', Blocks.BIRCH_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.JUNGLE_SLAB, "###", '#', Blocks.JUNGLE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.OAK_SLAB, "###", '#', Blocks.OAK_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_stairs", 4, Blocks.SPRUCE_STAIRS, "#  ", "## ", "###", '#', Blocks.SPRUCE_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_stairs", 4, Blocks.BIRCH_STAIRS, "#  ", "## ", "###", '#', Blocks.BIRCH_PLANKS));
            recipes.add(RecipeInfo.shaped("wooden_stairs", 4, Blocks.JUNGLE_STAIRS, "#  ", "## ", "###", '#', Blocks.JUNGLE_PLANKS));
            recipes.add(RecipeInfo.shaped(4, Blocks.SANDSTONE_STAIRS, "#  ", "## ", "###", '#', Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE));
            recipes.add(RecipeInfo.shaped(2, Blocks.TRIPWIRE_HOOK, "I", "S", "#", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'S', Items.STICK, 'I', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Blocks.ENDER_CHEST, "###", "#E#", "###", '#', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE));
            recipes.add(RecipeInfo.shaped(Blocks.EMERALD_BLOCK, "###", "###", "###", '#', Items.EMERALD));
            recipes.add(RecipeInfo.shaped(9, Items.EMERALD, "#", '#', Blocks.EMERALD_BLOCK));
        } else {
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_2_1tor1_2_3)) {
                recipes.add(RecipeInfo.shaped("wooden_slab", 6, Blocks.OAK_SLAB, "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            } else {
                if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_3tob1_3_1)) {
                    recipes.add(RecipeInfo.shaped("wooden_slab", 3, Blocks.OAK_SLAB, "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
                }
            }
            recipes.add(RecipeInfo.shaped(Items.BOOK, "#", "#", "#", '#', Items.PAPER));
            recipes.add(RecipeInfo.shaped(Blocks.OAK_SIGN, "###", "###", " X ", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'X', Items.STICK));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_2_4tor1_2_5)) {
            recipes.add(RecipeInfo.shaped("planks", 4, Blocks.BIRCH_PLANKS, "#", '#', Blocks.BIRCH_LOG));
            recipes.add(RecipeInfo.shaped("planks", 4, Blocks.SPRUCE_PLANKS, "#", '#', Blocks.SPRUCE_LOG));
            recipes.add(RecipeInfo.shaped("planks", 4, Blocks.JUNGLE_PLANKS, "#", '#', Blocks.JUNGLE_LOG));
            recipes.add(RecipeInfo.shaped(Blocks.CHISELED_SANDSTONE, "#", "#", '#', Blocks.SANDSTONE_SLAB));
            recipes.add(RecipeInfo.shaped(4, Blocks.CUT_SANDSTONE, "##", "##", '#', Blocks.SANDSTONE));
        } else {
            recipes.add(RecipeInfo.shaped("planks", 4, Blocks.OAK_PLANKS, "#", '#', Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.JUNGLE_LOG));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_2_1tor1_2_3)) {
            recipes.add(RecipeInfo.shaped(3, Blocks.LADDER, "# #", "###", "# #", '#', Items.STICK));
            recipes.add(RecipeInfo.shaped(6, Blocks.SMOOTH_STONE_SLAB, "###", '#', Blocks.STONE));
            recipes.add(RecipeInfo.shaped(6, Blocks.SANDSTONE_SLAB, "###", '#', Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE));
            recipes.add(RecipeInfo.shaped(6, Blocks.COBBLESTONE_SLAB, "###", '#', Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shaped(6, Blocks.BRICK_SLAB, "###", '#', Blocks.BRICKS));
            recipes.add(RecipeInfo.shaped(6, Blocks.STONE_BRICK_SLAB, "###", '#', Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS));
            recipes.add(RecipeInfo.shaped(Blocks.REDSTONE_LAMP, " R ", "RGR", " R ", 'R', Blocks.REDSTONE_WIRE, 'G', Blocks.GLOWSTONE));
            recipes.add(RecipeInfo.shapeless(3, Items.FIRE_CHARGE, new ItemConvertible[]{Items.GUNPOWDER}, new ItemConvertible[]{Items.BLAZE_POWDER}, new ItemConvertible[]{Items.COAL, Items.CHARCOAL}));
        } else {
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_5tob1_5_2)) {
                recipes.add(RecipeInfo.shaped(2, Blocks.LADDER, "# #", "###", "# #", '#', Items.STICK));
            } else {
                recipes.add(RecipeInfo.shaped(1, Blocks.LADDER, "# #", "###", "# #", '#', Items.STICK));
            }
            recipes.add(RecipeInfo.shaped(3, Blocks.SMOOTH_STONE_SLAB, "###", '#', Blocks.STONE));
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_3tob1_3_1)) {
                recipes.add(RecipeInfo.shaped(3, Blocks.SANDSTONE_SLAB, "###", '#', Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE));
                recipes.add(RecipeInfo.shaped(3, Blocks.COBBLESTONE_SLAB, "###", '#', Blocks.COBBLESTONE));
            }
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
                recipes.add(RecipeInfo.shaped(3, Blocks.BRICK_SLAB, "###", '#', Blocks.BRICKS));
                recipes.add(RecipeInfo.shaped(3, Blocks.STONE_BRICK_SLAB, "###", '#', Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS));
            }
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_0_0tor1_0_1)) {
            recipes.add(RecipeInfo.shapeless(Items.MUSHROOM_STEW, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Items.BOWL));
            recipes.add(RecipeInfo.shaped(4, Blocks.NETHER_BRICK_STAIRS, "#  ", "## ", "###", '#', Blocks.NETHER_BRICKS));
            recipes.add(RecipeInfo.shaped(6, Blocks.NETHER_BRICK_FENCE, "###", "###", '#', Blocks.NETHER_BRICKS));
            recipes.add(RecipeInfo.shaped(3, Items.GLASS_BOTTLE, "# #", " # ", '#', Blocks.GLASS));
            recipes.add(RecipeInfo.shaped(Blocks.BREWING_STAND, " B ", "###", 'B', Items.BLAZE_ROD, '#', Blocks.COBBLESTONE));
            recipes.add(RecipeInfo.shaped(Blocks.CAULDRON, "# #", "# #", "###", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shapeless(Items.ENDER_EYE, Items.ENDER_PEARL, Items.BLAZE_POWDER));
            recipes.add(RecipeInfo.shaped(Blocks.ENCHANTING_TABLE, " B ", "D#D", "###", 'B', Items.BOOK, '#', Blocks.OBSIDIAN, 'D', Items.DIAMOND));
            recipes.add(RecipeInfo.shaped(4, Blocks.PUMPKIN_STEM, "M", 'M', Blocks.CARVED_PUMPKIN));
            recipes.add(RecipeInfo.shapeless(Items.FERMENTED_SPIDER_EYE, Items.SPIDER_EYE, Blocks.BROWN_MUSHROOM, Items.SUGAR));
            recipes.add(RecipeInfo.shapeless(2, Items.BLAZE_POWDER, Items.BLAZE_ROD));
            recipes.add(RecipeInfo.shapeless(Items.MAGMA_CREAM, Items.BLAZE_POWDER, Items.SLIME_BALL));
            recipes.add(RecipeInfo.shaped(9, Items.GOLD_NUGGET, "#", '#', Items.GOLD_INGOT));
            recipes.add(RecipeInfo.shaped("gold_ingot", Items.GOLD_INGOT, "###", "###", "###", '#', Items.GOLD_NUGGET));
        } else {
            recipes.add(RecipeInfo.shaped(Items.MUSHROOM_STEW, "Y", "X", "#", 'X', Blocks.BROWN_MUSHROOM, 'Y', Blocks.RED_MUSHROOM, '#', Items.BOWL));
            recipes.add(RecipeInfo.shaped(Items.MUSHROOM_STEW, "Y", "X", "#", 'X', Blocks.RED_MUSHROOM, 'Y', Blocks.BROWN_MUSHROOM, '#', Items.BOWL));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            recipes.add(RecipeInfo.shaped(4, Blocks.BRICK_STAIRS, "#  ", "## ", "###", '#', Blocks.BRICKS));
            recipes.add(RecipeInfo.shaped(4, Blocks.STONE_BRICK_STAIRS, "#  ", "## ", "###", '#', Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS));
            recipes.add(RecipeInfo.shaped(4, Blocks.STONE_BRICKS, "##", "##", '#', Blocks.STONE));
            recipes.add(RecipeInfo.shaped(16, Blocks.IRON_BARS, "###", "###", '#', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(16, Blocks.GLASS_PANE, "###", "###", '#', Blocks.GLASS));
            recipes.add(RecipeInfo.shaped(Blocks.MELON, "MMM", "MMM", "MMM", 'M', Items.MELON_SLICE));
            recipes.add(RecipeInfo.shaped(Blocks.MELON_STEM, "M", 'M', Items.MELON_SLICE));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_7tob1_7_3)) {
            recipes.add(RecipeInfo.shaped(Blocks.STICKY_PISTON, "S", "P", 'P', Blocks.PISTON, 'S', Items.SLIME_BALL));
            recipes.add(RecipeInfo.shaped(Blocks.PISTON, "TTT", "#X#", "#R#", 'R', Blocks.REDSTONE_WIRE, '#', Blocks.COBBLESTONE, 'T', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'X', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(Items.SHEARS, " #", "# ", '#', Items.IRON_INGOT));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_6tob1_6_6)) {
            recipes.add(RecipeInfo.shaped(Blocks.GLOWSTONE, "##", "##", '#', Items.GLOWSTONE_DUST));
            recipes.add(RecipeInfo.shaped(Blocks.WHITE_WOOL, "##", "##", '#', Blocks.TRIPWIRE));
            recipes.add(RecipeInfo.shaped(2, Blocks.OAK_TRAPDOOR, "###", "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped(Items.MAP, "###", "#X#", "###", '#', Items.PAPER, 'X', Items.COMPASS));
        } else {
            if (targetVersion.isNewerThanOrEqualTo(VersionEnum.a1_2_0toa1_2_1_1)) {
                recipes.add(RecipeInfo.shaped(Blocks.GLOWSTONE, "###", "###", "###", '#', Items.GLOWSTONE_DUST));
            }
            recipes.add(RecipeInfo.shaped(Blocks.WHITE_WOOL, "###", "###", "###", '#', Blocks.TRIPWIRE));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_5tob1_5_2)) {
            recipes.add(RecipeInfo.shaped(6, Blocks.DETECTOR_RAIL, "X X", "X#X", "XRX", 'R', Blocks.REDSTONE_WIRE, '#', Blocks.STONE_PRESSURE_PLATE, 'X', Items.IRON_INGOT));
            recipes.add(RecipeInfo.shaped(6, Blocks.POWERED_RAIL, "X X", "X#X", "XRX", 'R', Blocks.REDSTONE_WIRE, '#', Items.STICK, 'X', Items.GOLD_INGOT));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_4tob1_4_1)) {
            recipes.add(RecipeInfo.shaped(8, Items.COOKIE, "#X#", '#', Items.WHEAT, 'X', Blocks.COCOA));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_3tob1_3_1)) {
            recipes.add(RecipeInfo.shaped(Blocks.REPEATER, "#X#", "III", '#', Blocks.REDSTONE_TORCH, 'X', Blocks.REDSTONE_WIRE, 'I', Blocks.STONE));
            recipes.add(RecipeInfo.shaped(Blocks.OAK_PRESSURE_PLATE, "##", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped(Blocks.STONE_PRESSURE_PLATE, "##", '#', Blocks.STONE));
        } else {
            recipes.add(RecipeInfo.shaped(Blocks.OAK_PRESSURE_PLATE, "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS));
            recipes.add(RecipeInfo.shaped(Blocks.STONE_PRESSURE_PLATE, "###", '#', Blocks.STONE));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_2_0tob1_2_2)) {
            recipes.add(RecipeInfo.shaped(Blocks.NOTE_BLOCK, "###", "#X#", "###", '#', Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS, 'X', Blocks.REDSTONE_WIRE));
            recipes.add(RecipeInfo.shaped(Blocks.CAKE, "AAA", "BEB", "CCC", 'A', Items.MILK_BUCKET, 'B', Items.SUGAR, 'C', Items.WHEAT, 'E', Items.EGG));
            recipes.add(RecipeInfo.shaped(Items.SUGAR, "#", '#', Blocks.SUGAR_CANE));
            recipes.add(RecipeInfo.shaped(4, Blocks.TORCH, "X", "#", '#', Items.STICK, 'X', Items.COAL, Items.CHARCOAL));
            recipes.add(RecipeInfo.shaped(Blocks.DISPENSER, "###", "#X#", "#R#", 'R', Blocks.REDSTONE_WIRE, '#', Blocks.COBBLESTONE, 'X', Items.BOW));
            recipes.add(RecipeInfo.shaped(Blocks.SANDSTONE, "##", "##", '#', Blocks.SAND));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.YELLOW_WOOL, Items.YELLOW_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("yellow_dye", Items.YELLOW_DYE, Blocks.DANDELION));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.RED_WOOL, Items.RED_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("red_dye", Items.RED_DYE, Blocks.POPPY));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.PURPLE_WOOL, Items.PURPLE_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless(2, Items.PURPLE_DYE, Items.LAPIS_LAZULI, Items.RED_DYE));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.PINK_WOOL, Items.PINK_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("pink_dye", 2, Items.PINK_DYE, Items.RED_DYE, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.ORANGE_WOOL, Items.ORANGE_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("orange_dye", 2, Items.ORANGE_DYE, Items.RED_DYE, Items.YELLOW_DYE));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.MAGENTA_WOOL, Items.MAGENTA_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("magenta_dye", 2, Items.MAGENTA_DYE, Items.PURPLE_DYE, Items.PINK_DYE));
            recipes.add(RecipeInfo.shapeless("magenta_dye", 3, Items.MAGENTA_DYE, Items.LAPIS_LAZULI, Items.RED_DYE, Items.PINK_DYE));
            recipes.add(RecipeInfo.shapeless("magenta_dye", 4, Items.MAGENTA_DYE, Items.LAPIS_LAZULI, Items.RED_DYE, Items.RED_DYE, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.LIME_WOOL, Items.LIME_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless(2, Items.LIME_DYE, Items.GREEN_DYE, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("light_gray_dye", 3, Items.LIGHT_GRAY_DYE, Items.INK_SAC, Items.BONE_MEAL, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("light_gray_dye", 2, Items.LIGHT_GRAY_DYE, Items.GRAY_DYE, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("light_blue_dye", 2, Items.LIGHT_BLUE_DYE, Items.LAPIS_LAZULI, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.GREEN_WOOL, Items.GREEN_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.GRAY_WOOL, Items.GRAY_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless(2, Items.GRAY_DYE, Items.INK_SAC, Items.BONE_MEAL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.CYAN_WOOL, Items.CYAN_DYE, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless(2, Items.CYAN_DYE, Items.LAPIS_LAZULI, Items.GREEN_DYE));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.BLUE_WOOL, Items.LAPIS_LAZULI, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.BLACK_WOOL, Items.INK_SAC, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("wool", Blocks.BROWN_WOOL, Blocks.COCOA, Blocks.WHITE_WOOL));
            recipes.add(RecipeInfo.shapeless("bonemeal", 3, Items.BONE_MEAL, Items.BONE));
            recipes.add(RecipeInfo.shaped(9, Items.LAPIS_LAZULI, "#", '#', Blocks.LAPIS_BLOCK));
            recipes.add(RecipeInfo.shaped(Blocks.LAPIS_BLOCK, "###", "###", "###", '#', Items.LAPIS_LAZULI));
        } else {
            recipes.add(RecipeInfo.shaped(4, Blocks.TORCH, "X", "#", '#', Items.STICK, 'X', Items.COAL));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.a1_2_0toa1_2_1_1)) {
            recipes.add(RecipeInfo.shaped(Blocks.JACK_O_LANTERN, "A", "B", 'A', Blocks.CARVED_PUMPKIN, 'B', Blocks.TORCH));
            recipes.add(RecipeInfo.shaped(Items.CLOCK, " # ", "#X#", " # ", '#', Items.GOLD_INGOT, 'X', Blocks.REDSTONE_WIRE));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.a1_1_0toa1_1_2_1)) {
            recipes.add(RecipeInfo.shaped(Items.FISHING_ROD, "  #", " #X", "# X", '#', Items.STICK, 'X', Blocks.TRIPWIRE));
            recipes.add(RecipeInfo.shaped(Items.COMPASS, " # ", "#X#", " # ", '#', Items.IRON_INGOT, 'X', Blocks.REDSTONE_WIRE));
        }

        recipes.add(RecipeInfo.smelting(Items.IRON_INGOT, Items.IRON_ORE, 0.7F));
        recipes.add(RecipeInfo.smelting(Items.GOLD_INGOT, Items.GOLD_ORE, 1.0F));
        recipes.add(RecipeInfo.smelting(Items.DIAMOND, Items.DIAMOND_ORE, 1.0F));
        recipes.add(RecipeInfo.smelting(Items.GLASS, Ingredient.fromTag(ItemTags.SAND), 0.1F));
        recipes.add(RecipeInfo.smelting(Items.COOKED_PORKCHOP, Items.PORKCHOP, 0.35F));
        recipes.add(RecipeInfo.smelting(Items.STONE, Items.COBBLESTONE, 0.1F));
        recipes.add(RecipeInfo.smelting(Items.BRICK, Items.CLAY_BALL, 0.3F));

        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_12)) {
            recipes.add(RecipeInfo.smelting(Items.WHITE_GLAZED_TERRACOTTA, Items.WHITE_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.ORANGE_GLAZED_TERRACOTTA, Items.ORANGE_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.MAGENTA_GLAZED_TERRACOTTA, Items.MAGENTA_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.LIGHT_BLUE_GLAZED_TERRACOTTA, Items.LIGHT_BLUE_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.YELLOW_GLAZED_TERRACOTTA, Items.YELLOW_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.LIME_GLAZED_TERRACOTTA, Items.LIME_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.PINK_GLAZED_TERRACOTTA, Items.PINK_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.GRAY_GLAZED_TERRACOTTA, Items.GRAY_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.LIGHT_GRAY_GLAZED_TERRACOTTA, Items.LIGHT_GRAY_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.CYAN_GLAZED_TERRACOTTA, Items.CYAN_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.PURPLE_GLAZED_TERRACOTTA, Items.PURPLE_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.BLUE_GLAZED_TERRACOTTA, Items.BLUE_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.BROWN_GLAZED_TERRACOTTA, Items.BROWN_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.GREEN_GLAZED_TERRACOTTA, Items.GREEN_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.RED_GLAZED_TERRACOTTA, Items.RED_TERRACOTTA, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.BLACK_GLAZED_TERRACOTTA, Items.BLACK_TERRACOTTA, 0.1F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_11_1to1_11_2)) {
            recipes.add(RecipeInfo.smelting(Items.IRON_NUGGET, Ingredient.ofItems(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_HORSE_ARMOR), 0.1F));
            recipes.add(RecipeInfo.smelting(Items.GOLD_NUGGET, Ingredient.ofItems(Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_SWORD, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR), 0.1F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_9)) {
            recipes.add(RecipeInfo.smelting(Items.POPPED_CHORUS_FRUIT, Items.CHORUS_FRUIT, 0.1F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_7_2tor1_7_5)) {
            recipes.add(RecipeInfo.smelting(Items.COOKED_RABBIT, Items.RABBIT, 0.35F));
            recipes.add(RecipeInfo.smelting(Items.COOKED_MUTTON, Items.MUTTON, 0.35F));
            recipes.add(RecipeInfo.smelting(Items.CRACKED_STONE_BRICKS, Items.STONE_BRICKS, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.SPONGE, Items.WET_SPONGE, 0.15F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_6_1)) {
            recipes.add(RecipeInfo.smelting(Items.TERRACOTTA, Items.CLAY, 0.35F));
            recipes.add(RecipeInfo.smelting(Items.COOKED_SALMON, Items.SALMON, 0.35F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_5tor1_5_1)) {
            recipes.add(RecipeInfo.smelting(Items.NETHER_BRICK, Items.NETHERRACK, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.QUARTZ, Items.NETHER_QUARTZ_ORE, 0.2F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_4_2)) {
            recipes.add(RecipeInfo.smelting(Items.BAKED_POTATO, Items.POTATO, 0.35F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_3_1tor1_3_2)) {
            recipes.add(RecipeInfo.smelting(Items.EMERALD, Items.EMERALD_ORE, 1.0F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.r1_0_0tor1_0_1)) {
            recipes.add(RecipeInfo.smelting(Items.COAL, Items.COAL_ORE, 0.1F));
            recipes.add(RecipeInfo.smelting(Items.REDSTONE, Items.REDSTONE_ORE, 0.7F));
            recipes.add(RecipeInfo.smelting(Items.LAPIS_LAZULI, Items.LAPIS_ORE, 0.2F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            recipes.add(RecipeInfo.smelting(Items.COOKED_CHICKEN, Items.CHICKEN, 0.35F));
            recipes.add(RecipeInfo.smelting(Items.COOKED_BEEF, Items.BEEF, 0.35F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.b1_2_0tob1_2_2)) {
            recipes.add(RecipeInfo.smelting(Items.CHARCOAL, Ingredient.fromTag(ItemTags.LOGS), 0.15F));
            recipes.add(RecipeInfo.smelting(Items.GREEN_DYE, Items.CACTUS, 0.2F));
        }
        if (targetVersion.isNewerThanOrEqualTo(VersionEnum.a1_2_0toa1_2_1_1)) {
            recipes.add(RecipeInfo.smelting(Items.COOKED_COD, Items.COD, 0.35F));
        }

        return recipes;
    }

    /**
     * Sets the result slot of a crafting screen handler to the correct item stack. In MC <= 1.11.2 the result slot
     * is not updated when the input slots change, so we need to update it manually, Spigot and Paper re-syncs the slot,
     * so we don't notice this bug on servers that use Spigot or Paper
     *
     * @param syncId        The sync id of the screen handler
     * @param screenHandler The screen handler
     * @param inventory     The inventory of the screen handler
     */
    public static void setCraftingResultSlot(final int syncId, final ScreenHandler screenHandler, final RecipeInputInventory inventory) {
        final var network = MinecraftClient.getInstance().getNetworkHandler();
        final var world = MinecraftClient.getInstance().world;

        final var result = network.getRecipeManager()
                .getFirstMatch(RecipeType.CRAFTING, inventory, world) // Get the first matching recipe
                .map(recipe -> recipe.value().craft(inventory, network.getRegistryManager())) // Craft the recipe to get the result
                .orElse(ItemStack.EMPTY); // If there is no recipe, set the result to air

        // Update the result slot
        network.onScreenHandlerSlotUpdate(new ScreenHandlerSlotUpdateS2CPacket(syncId, screenHandler.getRevision(), 0, result));
    }

}
