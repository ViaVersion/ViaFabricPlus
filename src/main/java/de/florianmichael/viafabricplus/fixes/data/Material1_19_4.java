/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.fixes.data;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;

import java.util.HashMap;
import java.util.Map;

/**
 * Data dump of Minecraft's material registry in version 1.19.4. This is used for some internal clientside fixes.
 */
public enum Material1_19_4 {

    AIR(false, false, false, false, true, false),
    STRUCTURE_VOID(false, false, false, false, true, false),
    PORTAL(false, false, false, false, false, false),
    CARPET(false, true, false, false, false, false),
    PLANT(false, false, false, false, false, false),
    UNDERWATER_PLANT(false, false, false, false, false, false),
    REPLACEABLE_PLANT(false, true, false, false, true, false),
    NETHER_SHOOTS(false, false, false, false, true, false),
    REPLACEABLE_UNDERWATER_PLANT(false, false, false, false, true, false),
    WATER(false, false, true, false, true, false),
    BUBBLE_COLUMN(false, false, true, false, true, false),
    LAVA(false, false, true, false, true, false),
    SNOW_LAYER(false, false, false, false, true, false),
    FIRE(false, false, false, false, true, false),
    DECORATION(false, false, false, false, false, false),
    COBWEB(false, false, false, false, false, true),
    SCULK(true, false, false, true, false, true),
    REDSTONE_LAMP(true, false, false, true, false, true),
    ORGANIC_PRODUCT(true, false, false, true, false, true),
    SOIL(true, false, false, true, false, true),
    SOLID_ORGANIC(true, false, false, true, false, true),
    DENSE_ICE(true, false, false, true, false, true),
    AGGREGATE(true, false, false, true, false, true),
    SPONGE(true, false, false, true, false, true),
    SHULKER_BOX(true, false, false, true, false, true),
    WOOD(true, true, false, true, false, true),
    NETHER_WOOD(true, false, false, true, false, true),
    BAMBOO_SAPLING(false, true, false, true, false, true),
    BAMBOO(true, true, false, true, false, true),
    WOOL(true, true, false, true, false, true),
    TNT(true, true, false, false, false, true),
    LEAVES(true, true, false, false, false, true),
    GLASS(true, false, false, false, false, true),
    ICE(true, false, false, false, false, true),
    CACTUS(true, false, false, false, false, true),
    STONE(true, false, false, true, false, true),
    METAL(true, false, false, true, false, true),
    SNOW_BLOCK(true, false, false, true, false, true),
    REPAIR_STATION(true, false, false, true, false, true),
    BARRIER(true, false, false, true, false, true),
    PISTON(true, false, false, true, false, true),
    MOSS_BLOCK(true, false, false, true, false, true),
    GOURD(true, false, false, true, false, true),
    EGG(true, false, false, true, false, true),
    CAKE(true, false, false, true, false, true),
    AMETHYST(true, false, false, true, false, true),
    POWDER_SNOW(false, false, false, true, false, false),
    FROGSPAWN(false, false, false, false, false, false),
    FROGLIGHT(true, false, false, true, false, true),
    DECORATED_POT(true, false, false, true, false, true);

    private final boolean blocksMovement;
    private final boolean burnable;
    private final boolean liquid;
    private final boolean blocksLight;
    private final boolean replaceable;
    private final boolean solid;

    Material1_19_4(final boolean blocksMovement, final boolean burnable, final boolean liquid, final boolean blocksLight, final boolean replaceable, final boolean solid) {
        this.blocksMovement = blocksMovement;
        this.burnable = burnable;
        this.liquid = liquid;
        this.blocksLight = blocksLight;
        this.replaceable = replaceable;
        this.solid = solid;
    }

    private static final Map<Block, Material1_19_4> MATERIALS = new HashMap<>();

    static {
        MATERIALS.put(Blocks.AIR, AIR);
        MATERIALS.put(Blocks.STONE, STONE);
        MATERIALS.put(Blocks.GRANITE, STONE);
        MATERIALS.put(Blocks.POLISHED_GRANITE, STONE);
        MATERIALS.put(Blocks.DIORITE, STONE);
        MATERIALS.put(Blocks.POLISHED_DIORITE, STONE);
        MATERIALS.put(Blocks.ANDESITE, STONE);
        MATERIALS.put(Blocks.POLISHED_ANDESITE, STONE);
        MATERIALS.put(Blocks.GRASS_BLOCK, SOLID_ORGANIC);
        MATERIALS.put(Blocks.DIRT, SOIL);
        MATERIALS.put(Blocks.COARSE_DIRT, SOIL);
        MATERIALS.put(Blocks.PODZOL, SOIL);
        MATERIALS.put(Blocks.COBBLESTONE, STONE);
        MATERIALS.put(Blocks.OAK_PLANKS, WOOD);
        MATERIALS.put(Blocks.SPRUCE_PLANKS, WOOD);
        MATERIALS.put(Blocks.BIRCH_PLANKS, WOOD);
        MATERIALS.put(Blocks.JUNGLE_PLANKS, WOOD);
        MATERIALS.put(Blocks.ACACIA_PLANKS, WOOD);
        MATERIALS.put(Blocks.CHERRY_PLANKS, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_PLANKS, WOOD);
        MATERIALS.put(Blocks.MANGROVE_PLANKS, WOOD);
        MATERIALS.put(Blocks.BAMBOO_PLANKS, WOOD);
        MATERIALS.put(Blocks.BAMBOO_MOSAIC, WOOD);
        MATERIALS.put(Blocks.OAK_SAPLING, PLANT);
        MATERIALS.put(Blocks.SPRUCE_SAPLING, PLANT);
        MATERIALS.put(Blocks.BIRCH_SAPLING, PLANT);
        MATERIALS.put(Blocks.JUNGLE_SAPLING, PLANT);
        MATERIALS.put(Blocks.ACACIA_SAPLING, PLANT);
        MATERIALS.put(Blocks.CHERRY_SAPLING, PLANT);
        MATERIALS.put(Blocks.DARK_OAK_SAPLING, PLANT);
        MATERIALS.put(Blocks.MANGROVE_PROPAGULE, PLANT);
        MATERIALS.put(Blocks.BEDROCK, STONE);
        MATERIALS.put(Blocks.WATER, WATER);
        MATERIALS.put(Blocks.LAVA, LAVA);
        MATERIALS.put(Blocks.SAND, AGGREGATE);
        MATERIALS.put(Blocks.SUSPICIOUS_SAND, AGGREGATE);
        MATERIALS.put(Blocks.RED_SAND, AGGREGATE);
        MATERIALS.put(Blocks.GRAVEL, AGGREGATE);
        MATERIALS.put(Blocks.GOLD_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_GOLD_ORE, STONE);
        MATERIALS.put(Blocks.IRON_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_IRON_ORE, STONE);
        MATERIALS.put(Blocks.COAL_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_COAL_ORE, STONE);
        MATERIALS.put(Blocks.NETHER_GOLD_ORE, STONE);
        MATERIALS.put(Blocks.OAK_LOG, WOOD);
        MATERIALS.put(Blocks.SPRUCE_LOG, WOOD);
        MATERIALS.put(Blocks.BIRCH_LOG, WOOD);
        MATERIALS.put(Blocks.JUNGLE_LOG, WOOD);
        MATERIALS.put(Blocks.ACACIA_LOG, WOOD);
        MATERIALS.put(Blocks.CHERRY_LOG, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_LOG, WOOD);
        MATERIALS.put(Blocks.MANGROVE_LOG, WOOD);
        MATERIALS.put(Blocks.MANGROVE_ROOTS, WOOD);
        MATERIALS.put(Blocks.MUDDY_MANGROVE_ROOTS, SOIL);
        MATERIALS.put(Blocks.BAMBOO_BLOCK, WOOD);
        MATERIALS.put(Blocks.STRIPPED_SPRUCE_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_BIRCH_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_JUNGLE_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_ACACIA_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_CHERRY_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_DARK_OAK_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_OAK_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_MANGROVE_LOG, WOOD);
        MATERIALS.put(Blocks.STRIPPED_BAMBOO_BLOCK, WOOD);
        MATERIALS.put(Blocks.OAK_WOOD, WOOD);
        MATERIALS.put(Blocks.SPRUCE_WOOD, WOOD);
        MATERIALS.put(Blocks.BIRCH_WOOD, WOOD);
        MATERIALS.put(Blocks.JUNGLE_WOOD, WOOD);
        MATERIALS.put(Blocks.ACACIA_WOOD, WOOD);
        MATERIALS.put(Blocks.CHERRY_WOOD, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_WOOD, WOOD);
        MATERIALS.put(Blocks.MANGROVE_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_OAK_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_SPRUCE_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_BIRCH_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_JUNGLE_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_ACACIA_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_CHERRY_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_DARK_OAK_WOOD, WOOD);
        MATERIALS.put(Blocks.STRIPPED_MANGROVE_WOOD, WOOD);
        MATERIALS.put(Blocks.OAK_LEAVES, LEAVES);
        MATERIALS.put(Blocks.SPRUCE_LEAVES, LEAVES);
        MATERIALS.put(Blocks.BIRCH_LEAVES, LEAVES);
        MATERIALS.put(Blocks.JUNGLE_LEAVES, LEAVES);
        MATERIALS.put(Blocks.ACACIA_LEAVES, LEAVES);
        MATERIALS.put(Blocks.CHERRY_LEAVES, LEAVES);
        MATERIALS.put(Blocks.DARK_OAK_LEAVES, LEAVES);
        MATERIALS.put(Blocks.MANGROVE_LEAVES, LEAVES);
        MATERIALS.put(Blocks.AZALEA_LEAVES, LEAVES);
        MATERIALS.put(Blocks.FLOWERING_AZALEA_LEAVES, LEAVES);
        MATERIALS.put(Blocks.SPONGE, SPONGE);
        MATERIALS.put(Blocks.WET_SPONGE, SPONGE);
        MATERIALS.put(Blocks.GLASS, GLASS);
        MATERIALS.put(Blocks.LAPIS_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_LAPIS_ORE, STONE);
        MATERIALS.put(Blocks.LAPIS_BLOCK, METAL);
        MATERIALS.put(Blocks.DISPENSER, STONE);
        MATERIALS.put(Blocks.SANDSTONE, STONE);
        MATERIALS.put(Blocks.CHISELED_SANDSTONE, STONE);
        MATERIALS.put(Blocks.CUT_SANDSTONE, STONE);
        MATERIALS.put(Blocks.NOTE_BLOCK, WOOD);
        MATERIALS.put(Blocks.WHITE_BED, WOOL);
        MATERIALS.put(Blocks.ORANGE_BED, WOOL);
        MATERIALS.put(Blocks.MAGENTA_BED, WOOL);
        MATERIALS.put(Blocks.LIGHT_BLUE_BED, WOOL);
        MATERIALS.put(Blocks.YELLOW_BED, WOOL);
        MATERIALS.put(Blocks.LIME_BED, WOOL);
        MATERIALS.put(Blocks.PINK_BED, WOOL);
        MATERIALS.put(Blocks.GRAY_BED, WOOL);
        MATERIALS.put(Blocks.LIGHT_GRAY_BED, WOOL);
        MATERIALS.put(Blocks.CYAN_BED, WOOL);
        MATERIALS.put(Blocks.PURPLE_BED, WOOL);
        MATERIALS.put(Blocks.BLUE_BED, WOOL);
        MATERIALS.put(Blocks.BROWN_BED, WOOL);
        MATERIALS.put(Blocks.GREEN_BED, WOOL);
        MATERIALS.put(Blocks.RED_BED, WOOL);
        MATERIALS.put(Blocks.BLACK_BED, WOOL);
        MATERIALS.put(Blocks.POWERED_RAIL, DECORATION);
        MATERIALS.put(Blocks.DETECTOR_RAIL, DECORATION);
        MATERIALS.put(Blocks.STICKY_PISTON, PISTON);
        MATERIALS.put(Blocks.COBWEB, COBWEB);
        MATERIALS.put(Blocks.SHORT_GRASS, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.FERN, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.DEAD_BUSH, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.SEAGRASS, REPLACEABLE_UNDERWATER_PLANT);
        MATERIALS.put(Blocks.TALL_SEAGRASS, REPLACEABLE_UNDERWATER_PLANT);
        MATERIALS.put(Blocks.PISTON, PISTON);
        MATERIALS.put(Blocks.PISTON_HEAD, PISTON);
        MATERIALS.put(Blocks.WHITE_WOOL, WOOL);
        MATERIALS.put(Blocks.ORANGE_WOOL, WOOL);
        MATERIALS.put(Blocks.MAGENTA_WOOL, WOOL);
        MATERIALS.put(Blocks.LIGHT_BLUE_WOOL, WOOL);
        MATERIALS.put(Blocks.YELLOW_WOOL, WOOL);
        MATERIALS.put(Blocks.LIME_WOOL, WOOL);
        MATERIALS.put(Blocks.PINK_WOOL, WOOL);
        MATERIALS.put(Blocks.GRAY_WOOL, WOOL);
        MATERIALS.put(Blocks.LIGHT_GRAY_WOOL, WOOL);
        MATERIALS.put(Blocks.CYAN_WOOL, WOOL);
        MATERIALS.put(Blocks.PURPLE_WOOL, WOOL);
        MATERIALS.put(Blocks.BLUE_WOOL, WOOL);
        MATERIALS.put(Blocks.BROWN_WOOL, WOOL);
        MATERIALS.put(Blocks.GREEN_WOOL, WOOL);
        MATERIALS.put(Blocks.RED_WOOL, WOOL);
        MATERIALS.put(Blocks.BLACK_WOOL, WOOL);
        MATERIALS.put(Blocks.MOVING_PISTON, PISTON);
        MATERIALS.put(Blocks.DANDELION, PLANT);
        MATERIALS.put(Blocks.TORCHFLOWER, PLANT);
        MATERIALS.put(Blocks.POPPY, PLANT);
        MATERIALS.put(Blocks.BLUE_ORCHID, PLANT);
        MATERIALS.put(Blocks.ALLIUM, PLANT);
        MATERIALS.put(Blocks.AZURE_BLUET, PLANT);
        MATERIALS.put(Blocks.RED_TULIP, PLANT);
        MATERIALS.put(Blocks.ORANGE_TULIP, PLANT);
        MATERIALS.put(Blocks.WHITE_TULIP, PLANT);
        MATERIALS.put(Blocks.PINK_TULIP, PLANT);
        MATERIALS.put(Blocks.OXEYE_DAISY, PLANT);
        MATERIALS.put(Blocks.CORNFLOWER, PLANT);
        MATERIALS.put(Blocks.WITHER_ROSE, PLANT);
        MATERIALS.put(Blocks.LILY_OF_THE_VALLEY, PLANT);
        MATERIALS.put(Blocks.BROWN_MUSHROOM, PLANT);
        MATERIALS.put(Blocks.RED_MUSHROOM, PLANT);
        MATERIALS.put(Blocks.GOLD_BLOCK, METAL);
        MATERIALS.put(Blocks.IRON_BLOCK, METAL);
        MATERIALS.put(Blocks.BRICKS, STONE);
        MATERIALS.put(Blocks.TNT, TNT);
        MATERIALS.put(Blocks.BOOKSHELF, WOOD);
        MATERIALS.put(Blocks.CHISELED_BOOKSHELF, WOOD);
        MATERIALS.put(Blocks.MOSSY_COBBLESTONE, STONE);
        MATERIALS.put(Blocks.OBSIDIAN, STONE);
        MATERIALS.put(Blocks.TORCH, DECORATION);
        MATERIALS.put(Blocks.WALL_TORCH, DECORATION);
        MATERIALS.put(Blocks.FIRE, FIRE);
        MATERIALS.put(Blocks.SOUL_FIRE, FIRE);
        MATERIALS.put(Blocks.SPAWNER, STONE);
        MATERIALS.put(Blocks.OAK_STAIRS, WOOD);
        MATERIALS.put(Blocks.CHEST, WOOD);
        MATERIALS.put(Blocks.REDSTONE_WIRE, DECORATION);
        MATERIALS.put(Blocks.DIAMOND_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_DIAMOND_ORE, STONE);
        MATERIALS.put(Blocks.DIAMOND_BLOCK, METAL);
        MATERIALS.put(Blocks.CRAFTING_TABLE, WOOD);
        MATERIALS.put(Blocks.WHEAT, PLANT);
        MATERIALS.put(Blocks.FARMLAND, SOIL);
        MATERIALS.put(Blocks.FURNACE, STONE);
        MATERIALS.put(Blocks.OAK_SIGN, WOOD);
        MATERIALS.put(Blocks.SPRUCE_SIGN, WOOD);
        MATERIALS.put(Blocks.BIRCH_SIGN, WOOD);
        MATERIALS.put(Blocks.ACACIA_SIGN, WOOD);
        MATERIALS.put(Blocks.CHERRY_SIGN, WOOD);
        MATERIALS.put(Blocks.JUNGLE_SIGN, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_SIGN, WOOD);
        MATERIALS.put(Blocks.MANGROVE_SIGN, WOOD);
        MATERIALS.put(Blocks.BAMBOO_SIGN, WOOD);
        MATERIALS.put(Blocks.OAK_DOOR, WOOD);
        MATERIALS.put(Blocks.LADDER, DECORATION);
        MATERIALS.put(Blocks.RAIL, DECORATION);
        MATERIALS.put(Blocks.COBBLESTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.OAK_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.SPRUCE_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.BIRCH_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.ACACIA_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.CHERRY_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.JUNGLE_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.MANGROVE_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.BAMBOO_WALL_SIGN, WOOD);
        MATERIALS.put(Blocks.OAK_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.SPRUCE_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.BIRCH_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.ACACIA_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.CHERRY_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.JUNGLE_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.CRIMSON_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.WARPED_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.MANGROVE_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.BAMBOO_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.OAK_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.SPRUCE_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.BIRCH_WALL_HANGING_SIGN, AGGREGATE);
        MATERIALS.put(Blocks.ACACIA_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.CHERRY_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.JUNGLE_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.MANGROVE_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.CRIMSON_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.WARPED_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.BAMBOO_WALL_HANGING_SIGN, WOOD);
        MATERIALS.put(Blocks.LEVER, DECORATION);
        MATERIALS.put(Blocks.STONE_PRESSURE_PLATE, STONE);
        MATERIALS.put(Blocks.IRON_DOOR, METAL);
        MATERIALS.put(Blocks.OAK_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.SPRUCE_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.BIRCH_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.JUNGLE_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.ACACIA_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.CHERRY_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.MANGROVE_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.BAMBOO_PRESSURE_PLATE, WOOD);
        MATERIALS.put(Blocks.REDSTONE_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_REDSTONE_ORE, STONE);
        MATERIALS.put(Blocks.REDSTONE_TORCH, DECORATION);
        MATERIALS.put(Blocks.REDSTONE_WALL_TORCH, DECORATION);
        MATERIALS.put(Blocks.STONE_BUTTON, DECORATION);
        MATERIALS.put(Blocks.SNOW, SNOW_LAYER);
        MATERIALS.put(Blocks.ICE, ICE);
        MATERIALS.put(Blocks.SNOW_BLOCK, SNOW_BLOCK);
        MATERIALS.put(Blocks.CACTUS, CACTUS);
        MATERIALS.put(Blocks.CLAY, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.SUGAR_CANE, PLANT);
        MATERIALS.put(Blocks.JUKEBOX, WOOD);
        MATERIALS.put(Blocks.OAK_FENCE, WOOD);
        MATERIALS.put(Blocks.PUMPKIN, GOURD);
        MATERIALS.put(Blocks.NETHERRACK, STONE);
        MATERIALS.put(Blocks.SOUL_SAND, AGGREGATE);
        MATERIALS.put(Blocks.SOUL_SOIL, SOIL);
        MATERIALS.put(Blocks.BASALT, STONE);
        MATERIALS.put(Blocks.POLISHED_BASALT, STONE);
        MATERIALS.put(Blocks.SOUL_TORCH, DECORATION);
        MATERIALS.put(Blocks.SOUL_WALL_TORCH, DECORATION);
        MATERIALS.put(Blocks.GLOWSTONE, GLASS);
        MATERIALS.put(Blocks.NETHER_PORTAL, PORTAL);
        MATERIALS.put(Blocks.CARVED_PUMPKIN, GOURD);
        MATERIALS.put(Blocks.JACK_O_LANTERN, GOURD);
        MATERIALS.put(Blocks.CAKE, CAKE);
        MATERIALS.put(Blocks.REPEATER, DECORATION);
        MATERIALS.put(Blocks.WHITE_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.ORANGE_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.MAGENTA_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.LIGHT_BLUE_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.YELLOW_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.LIME_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.PINK_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.GRAY_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.LIGHT_GRAY_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.CYAN_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.PURPLE_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.BLUE_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.BROWN_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.GREEN_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.RED_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.BLACK_STAINED_GLASS, GLASS);
        MATERIALS.put(Blocks.OAK_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.SPRUCE_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.BIRCH_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.JUNGLE_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.ACACIA_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.CHERRY_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.MANGROVE_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.BAMBOO_TRAPDOOR, WOOD);
        MATERIALS.put(Blocks.STONE_BRICKS, STONE);
        MATERIALS.put(Blocks.MOSSY_STONE_BRICKS, STONE);
        MATERIALS.put(Blocks.CRACKED_STONE_BRICKS, STONE);
        MATERIALS.put(Blocks.CHISELED_STONE_BRICKS, STONE);
        MATERIALS.put(Blocks.PACKED_MUD, SOIL);
        MATERIALS.put(Blocks.MUD_BRICKS, STONE);
        MATERIALS.put(Blocks.INFESTED_STONE, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.INFESTED_COBBLESTONE, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.INFESTED_STONE_BRICKS, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.INFESTED_MOSSY_STONE_BRICKS, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.INFESTED_CRACKED_STONE_BRICKS, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.INFESTED_CHISELED_STONE_BRICKS, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.BROWN_MUSHROOM_BLOCK, WOOD);
        MATERIALS.put(Blocks.RED_MUSHROOM_BLOCK, WOOD);
        MATERIALS.put(Blocks.MUSHROOM_STEM, WOOD);
        MATERIALS.put(Blocks.IRON_BARS, METAL);
        MATERIALS.put(Blocks.CHAIN, METAL);
        MATERIALS.put(Blocks.GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.MELON, GOURD);
        MATERIALS.put(Blocks.ATTACHED_PUMPKIN_STEM, PLANT);
        MATERIALS.put(Blocks.ATTACHED_MELON_STEM, PLANT);
        MATERIALS.put(Blocks.PUMPKIN_STEM, PLANT);
        MATERIALS.put(Blocks.MELON_STEM, PLANT);
        MATERIALS.put(Blocks.VINE, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.GLOW_LICHEN, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.OAK_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.STONE_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.MUD_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.MYCELIUM, SOLID_ORGANIC);
        MATERIALS.put(Blocks.LILY_PAD, PLANT);
        MATERIALS.put(Blocks.NETHER_BRICKS, STONE);
        MATERIALS.put(Blocks.NETHER_BRICK_FENCE, STONE);
        MATERIALS.put(Blocks.NETHER_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.NETHER_WART, PLANT);
        MATERIALS.put(Blocks.ENCHANTING_TABLE, STONE);
        MATERIALS.put(Blocks.BREWING_STAND, METAL);
        MATERIALS.put(Blocks.CAULDRON, METAL);
        MATERIALS.put(Blocks.WATER_CAULDRON, METAL);
        MATERIALS.put(Blocks.LAVA_CAULDRON, METAL);
        MATERIALS.put(Blocks.POWDER_SNOW_CAULDRON, METAL);
        MATERIALS.put(Blocks.END_PORTAL, PORTAL);
        MATERIALS.put(Blocks.END_PORTAL_FRAME, STONE);
        MATERIALS.put(Blocks.END_STONE, STONE);
        MATERIALS.put(Blocks.DRAGON_EGG, EGG);
        MATERIALS.put(Blocks.REDSTONE_LAMP, REDSTONE_LAMP);
        MATERIALS.put(Blocks.COCOA, PLANT);
        MATERIALS.put(Blocks.SANDSTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.EMERALD_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_EMERALD_ORE, STONE);
        MATERIALS.put(Blocks.ENDER_CHEST, STONE);
        MATERIALS.put(Blocks.TRIPWIRE_HOOK, DECORATION);
        MATERIALS.put(Blocks.TRIPWIRE, DECORATION);
        MATERIALS.put(Blocks.EMERALD_BLOCK, METAL);
        MATERIALS.put(Blocks.SPRUCE_STAIRS, WOOD);
        MATERIALS.put(Blocks.BIRCH_STAIRS, WOOD);
        MATERIALS.put(Blocks.JUNGLE_STAIRS, WOOD);
        MATERIALS.put(Blocks.COMMAND_BLOCK, METAL);
        MATERIALS.put(Blocks.BEACON, GLASS);
        MATERIALS.put(Blocks.COBBLESTONE_WALL, STONE);
        MATERIALS.put(Blocks.MOSSY_COBBLESTONE_WALL, STONE);
        MATERIALS.put(Blocks.FLOWER_POT, DECORATION);
        MATERIALS.put(Blocks.POTTED_TORCHFLOWER, DECORATION);
        MATERIALS.put(Blocks.POTTED_OAK_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_SPRUCE_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_BIRCH_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_JUNGLE_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_ACACIA_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_CHERRY_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_DARK_OAK_SAPLING, DECORATION);
        MATERIALS.put(Blocks.POTTED_MANGROVE_PROPAGULE, DECORATION);
        MATERIALS.put(Blocks.POTTED_FERN, DECORATION);
        MATERIALS.put(Blocks.POTTED_DANDELION, DECORATION);
        MATERIALS.put(Blocks.POTTED_POPPY, DECORATION);
        MATERIALS.put(Blocks.POTTED_BLUE_ORCHID, DECORATION);
        MATERIALS.put(Blocks.POTTED_ALLIUM, DECORATION);
        MATERIALS.put(Blocks.POTTED_AZURE_BLUET, DECORATION);
        MATERIALS.put(Blocks.POTTED_RED_TULIP, DECORATION);
        MATERIALS.put(Blocks.POTTED_ORANGE_TULIP, DECORATION);
        MATERIALS.put(Blocks.POTTED_WHITE_TULIP, DECORATION);
        MATERIALS.put(Blocks.POTTED_PINK_TULIP, DECORATION);
        MATERIALS.put(Blocks.POTTED_OXEYE_DAISY, DECORATION);
        MATERIALS.put(Blocks.POTTED_CORNFLOWER, DECORATION);
        MATERIALS.put(Blocks.POTTED_LILY_OF_THE_VALLEY, DECORATION);
        MATERIALS.put(Blocks.POTTED_WITHER_ROSE, DECORATION);
        MATERIALS.put(Blocks.POTTED_RED_MUSHROOM, DECORATION);
        MATERIALS.put(Blocks.POTTED_BROWN_MUSHROOM, DECORATION);
        MATERIALS.put(Blocks.POTTED_DEAD_BUSH, DECORATION);
        MATERIALS.put(Blocks.POTTED_CACTUS, DECORATION);
        MATERIALS.put(Blocks.CARROTS, PLANT);
        MATERIALS.put(Blocks.POTATOES, PLANT);
        MATERIALS.put(Blocks.OAK_BUTTON, DECORATION);
        MATERIALS.put(Blocks.SPRUCE_BUTTON, DECORATION);
        MATERIALS.put(Blocks.BIRCH_BUTTON, DECORATION);
        MATERIALS.put(Blocks.JUNGLE_BUTTON, DECORATION);
        MATERIALS.put(Blocks.ACACIA_BUTTON, DECORATION);
        MATERIALS.put(Blocks.CHERRY_BUTTON, DECORATION);
        MATERIALS.put(Blocks.DARK_OAK_BUTTON, DECORATION);
        MATERIALS.put(Blocks.MANGROVE_BUTTON, DECORATION);
        MATERIALS.put(Blocks.BAMBOO_BUTTON, DECORATION);
        MATERIALS.put(Blocks.SKELETON_SKULL, DECORATION);
        MATERIALS.put(Blocks.SKELETON_WALL_SKULL, DECORATION);
        MATERIALS.put(Blocks.WITHER_SKELETON_SKULL, DECORATION);
        MATERIALS.put(Blocks.WITHER_SKELETON_WALL_SKULL, DECORATION);
        MATERIALS.put(Blocks.ZOMBIE_HEAD, DECORATION);
        MATERIALS.put(Blocks.ZOMBIE_WALL_HEAD, DECORATION);
        MATERIALS.put(Blocks.PLAYER_HEAD, DECORATION);
        MATERIALS.put(Blocks.PLAYER_WALL_HEAD, DECORATION);
        MATERIALS.put(Blocks.CREEPER_HEAD, DECORATION);
        MATERIALS.put(Blocks.CREEPER_WALL_HEAD, DECORATION);
        MATERIALS.put(Blocks.DRAGON_HEAD, DECORATION);
        MATERIALS.put(Blocks.DRAGON_WALL_HEAD, DECORATION);
        MATERIALS.put(Blocks.PIGLIN_HEAD, DECORATION);
        MATERIALS.put(Blocks.PIGLIN_WALL_HEAD, DECORATION);
        MATERIALS.put(Blocks.ANVIL, REPAIR_STATION);
        MATERIALS.put(Blocks.CHIPPED_ANVIL, REPAIR_STATION);
        MATERIALS.put(Blocks.DAMAGED_ANVIL, REPAIR_STATION);
        MATERIALS.put(Blocks.TRAPPED_CHEST, WOOD);
        MATERIALS.put(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, METAL);
        MATERIALS.put(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, METAL);
        MATERIALS.put(Blocks.COMPARATOR, DECORATION);
        MATERIALS.put(Blocks.DAYLIGHT_DETECTOR, WOOD);
        MATERIALS.put(Blocks.REDSTONE_BLOCK, METAL);
        MATERIALS.put(Blocks.NETHER_QUARTZ_ORE, STONE);
        MATERIALS.put(Blocks.HOPPER, METAL);
        MATERIALS.put(Blocks.QUARTZ_BLOCK, STONE);
        MATERIALS.put(Blocks.CHISELED_QUARTZ_BLOCK, STONE);
        MATERIALS.put(Blocks.QUARTZ_PILLAR, STONE);
        MATERIALS.put(Blocks.QUARTZ_STAIRS, STONE);
        MATERIALS.put(Blocks.ACTIVATOR_RAIL, DECORATION);
        MATERIALS.put(Blocks.DROPPER, STONE);
        MATERIALS.put(Blocks.WHITE_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.ORANGE_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.MAGENTA_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.LIGHT_BLUE_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.YELLOW_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.LIME_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.PINK_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.GRAY_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.LIGHT_GRAY_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.CYAN_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.PURPLE_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.BLUE_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.BROWN_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.GREEN_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.RED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.BLACK_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.WHITE_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.ORANGE_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.MAGENTA_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.YELLOW_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.LIME_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.PINK_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.GRAY_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.CYAN_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.PURPLE_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.BLUE_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.BROWN_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.GREEN_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.RED_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.BLACK_STAINED_GLASS_PANE, GLASS);
        MATERIALS.put(Blocks.ACACIA_STAIRS, WOOD);
        MATERIALS.put(Blocks.CHERRY_STAIRS, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_STAIRS, WOOD);
        MATERIALS.put(Blocks.MANGROVE_STAIRS, WOOD);
        MATERIALS.put(Blocks.BAMBOO_STAIRS, WOOD);
        MATERIALS.put(Blocks.BAMBOO_MOSAIC_STAIRS, WOOD);
        MATERIALS.put(Blocks.SLIME_BLOCK, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.BARRIER, BARRIER);
        MATERIALS.put(Blocks.LIGHT, AIR);
        MATERIALS.put(Blocks.IRON_TRAPDOOR, METAL);
        MATERIALS.put(Blocks.PRISMARINE, STONE);
        MATERIALS.put(Blocks.PRISMARINE_BRICKS, STONE);
        MATERIALS.put(Blocks.DARK_PRISMARINE, STONE);
        MATERIALS.put(Blocks.PRISMARINE_STAIRS, STONE);
        MATERIALS.put(Blocks.PRISMARINE_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.DARK_PRISMARINE_STAIRS, STONE);
        MATERIALS.put(Blocks.PRISMARINE_SLAB, STONE);
        MATERIALS.put(Blocks.PRISMARINE_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.DARK_PRISMARINE_SLAB, STONE);
        MATERIALS.put(Blocks.SEA_LANTERN, GLASS);
        MATERIALS.put(Blocks.HAY_BLOCK, SOLID_ORGANIC);
        MATERIALS.put(Blocks.WHITE_CARPET, CARPET);
        MATERIALS.put(Blocks.ORANGE_CARPET, CARPET);
        MATERIALS.put(Blocks.MAGENTA_CARPET, CARPET);
        MATERIALS.put(Blocks.LIGHT_BLUE_CARPET, CARPET);
        MATERIALS.put(Blocks.YELLOW_CARPET, CARPET);
        MATERIALS.put(Blocks.LIME_CARPET, CARPET);
        MATERIALS.put(Blocks.PINK_CARPET, CARPET);
        MATERIALS.put(Blocks.GRAY_CARPET, CARPET);
        MATERIALS.put(Blocks.LIGHT_GRAY_CARPET, CARPET);
        MATERIALS.put(Blocks.CYAN_CARPET, CARPET);
        MATERIALS.put(Blocks.PURPLE_CARPET, CARPET);
        MATERIALS.put(Blocks.BLUE_CARPET, CARPET);
        MATERIALS.put(Blocks.BROWN_CARPET, CARPET);
        MATERIALS.put(Blocks.GREEN_CARPET, CARPET);
        MATERIALS.put(Blocks.RED_CARPET, CARPET);
        MATERIALS.put(Blocks.BLACK_CARPET, CARPET);
        MATERIALS.put(Blocks.TERRACOTTA, STONE);
        MATERIALS.put(Blocks.COAL_BLOCK, STONE);
        MATERIALS.put(Blocks.PACKED_ICE, DENSE_ICE);
        MATERIALS.put(Blocks.SUNFLOWER, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.LILAC, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.ROSE_BUSH, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.PEONY, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.TALL_GRASS, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.LARGE_FERN, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.WHITE_BANNER, WOOD);
        MATERIALS.put(Blocks.ORANGE_BANNER, WOOD);
        MATERIALS.put(Blocks.MAGENTA_BANNER, WOOD);
        MATERIALS.put(Blocks.LIGHT_BLUE_BANNER, WOOD);
        MATERIALS.put(Blocks.YELLOW_BANNER, WOOD);
        MATERIALS.put(Blocks.LIME_BANNER, WOOD);
        MATERIALS.put(Blocks.PINK_BANNER, WOOD);
        MATERIALS.put(Blocks.GRAY_BANNER, WOOD);
        MATERIALS.put(Blocks.LIGHT_GRAY_BANNER, WOOD);
        MATERIALS.put(Blocks.CYAN_BANNER, WOOD);
        MATERIALS.put(Blocks.PURPLE_BANNER, WOOD);
        MATERIALS.put(Blocks.BLUE_BANNER, WOOD);
        MATERIALS.put(Blocks.BROWN_BANNER, WOOD);
        MATERIALS.put(Blocks.GREEN_BANNER, WOOD);
        MATERIALS.put(Blocks.RED_BANNER, WOOD);
        MATERIALS.put(Blocks.BLACK_BANNER, WOOD);
        MATERIALS.put(Blocks.WHITE_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.ORANGE_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.MAGENTA_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.LIGHT_BLUE_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.YELLOW_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.LIME_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.PINK_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.GRAY_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.LIGHT_GRAY_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.CYAN_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.PURPLE_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.BLUE_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.BROWN_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.GREEN_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.RED_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.BLACK_WALL_BANNER, WOOD);
        MATERIALS.put(Blocks.RED_SANDSTONE, STONE);
        MATERIALS.put(Blocks.CHISELED_RED_SANDSTONE, STONE);
        MATERIALS.put(Blocks.CUT_RED_SANDSTONE, STONE);
        MATERIALS.put(Blocks.RED_SANDSTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.OAK_SLAB, WOOD);
        MATERIALS.put(Blocks.SPRUCE_SLAB, WOOD);
        MATERIALS.put(Blocks.BIRCH_SLAB, WOOD);
        MATERIALS.put(Blocks.JUNGLE_SLAB, WOOD);
        MATERIALS.put(Blocks.ACACIA_SLAB, WOOD);
        MATERIALS.put(Blocks.CHERRY_SLAB, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_SLAB, WOOD);
        MATERIALS.put(Blocks.MANGROVE_SLAB, WOOD);
        MATERIALS.put(Blocks.BAMBOO_SLAB, WOOD);
        MATERIALS.put(Blocks.BAMBOO_MOSAIC_SLAB, WOOD);
        MATERIALS.put(Blocks.STONE_SLAB, STONE);
        MATERIALS.put(Blocks.SMOOTH_STONE_SLAB, STONE);
        MATERIALS.put(Blocks.SANDSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.CUT_SANDSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.PETRIFIED_OAK_SLAB, STONE);
        MATERIALS.put(Blocks.COBBLESTONE_SLAB, STONE);
        MATERIALS.put(Blocks.BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.STONE_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.MUD_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.NETHER_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.QUARTZ_SLAB, STONE);
        MATERIALS.put(Blocks.RED_SANDSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.CUT_RED_SANDSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.PURPUR_SLAB, STONE);
        MATERIALS.put(Blocks.SMOOTH_STONE, STONE);
        MATERIALS.put(Blocks.SMOOTH_SANDSTONE, STONE);
        MATERIALS.put(Blocks.SMOOTH_QUARTZ, STONE);
        MATERIALS.put(Blocks.SMOOTH_RED_SANDSTONE, STONE);
        MATERIALS.put(Blocks.SPRUCE_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.BIRCH_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.JUNGLE_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.ACACIA_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.CHERRY_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.MANGROVE_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.BAMBOO_FENCE_GATE, WOOD);
        MATERIALS.put(Blocks.SPRUCE_FENCE, WOOD);
        MATERIALS.put(Blocks.BIRCH_FENCE, WOOD);
        MATERIALS.put(Blocks.JUNGLE_FENCE, WOOD);
        MATERIALS.put(Blocks.ACACIA_FENCE, WOOD);
        MATERIALS.put(Blocks.CHERRY_FENCE, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_FENCE, WOOD);
        MATERIALS.put(Blocks.MANGROVE_FENCE, WOOD);
        MATERIALS.put(Blocks.BAMBOO_FENCE, WOOD);
        MATERIALS.put(Blocks.SPRUCE_DOOR, WOOD);
        MATERIALS.put(Blocks.BIRCH_DOOR, WOOD);
        MATERIALS.put(Blocks.JUNGLE_DOOR, WOOD);
        MATERIALS.put(Blocks.ACACIA_DOOR, WOOD);
        MATERIALS.put(Blocks.CHERRY_DOOR, WOOD);
        MATERIALS.put(Blocks.DARK_OAK_DOOR, WOOD);
        MATERIALS.put(Blocks.MANGROVE_DOOR, WOOD);
        MATERIALS.put(Blocks.BAMBOO_DOOR, WOOD);
        MATERIALS.put(Blocks.END_ROD, DECORATION);
        MATERIALS.put(Blocks.CHORUS_PLANT, PLANT);
        MATERIALS.put(Blocks.CHORUS_FLOWER, PLANT);
        MATERIALS.put(Blocks.PURPUR_BLOCK, STONE);
        MATERIALS.put(Blocks.PURPUR_PILLAR, STONE);
        MATERIALS.put(Blocks.PURPUR_STAIRS, STONE);
        MATERIALS.put(Blocks.END_STONE_BRICKS, STONE);
        MATERIALS.put(Blocks.TORCHFLOWER_CROP, PLANT);
        MATERIALS.put(Blocks.BEETROOTS, PLANT);
        MATERIALS.put(Blocks.DIRT_PATH, SOIL);
        MATERIALS.put(Blocks.END_GATEWAY, PORTAL);
        MATERIALS.put(Blocks.REPEATING_COMMAND_BLOCK, METAL);
        MATERIALS.put(Blocks.CHAIN_COMMAND_BLOCK, METAL);
        MATERIALS.put(Blocks.FROSTED_ICE, ICE);
        MATERIALS.put(Blocks.MAGMA_BLOCK, STONE);
        MATERIALS.put(Blocks.NETHER_WART_BLOCK, SOLID_ORGANIC);
        MATERIALS.put(Blocks.RED_NETHER_BRICKS, STONE);
        MATERIALS.put(Blocks.BONE_BLOCK, STONE);
        MATERIALS.put(Blocks.STRUCTURE_VOID, STRUCTURE_VOID);
        MATERIALS.put(Blocks.OBSERVER, STONE);
        MATERIALS.put(Blocks.SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.WHITE_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.ORANGE_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.MAGENTA_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.LIGHT_BLUE_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.YELLOW_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.LIME_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.PINK_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.GRAY_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.LIGHT_GRAY_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.CYAN_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.PURPLE_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.BLUE_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.BROWN_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.GREEN_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.RED_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.BLACK_SHULKER_BOX, SHULKER_BOX);
        MATERIALS.put(Blocks.WHITE_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.ORANGE_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.MAGENTA_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.YELLOW_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.LIME_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.PINK_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.GRAY_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.CYAN_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.PURPLE_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.BLUE_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.BROWN_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.GREEN_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.RED_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.BLACK_GLAZED_TERRACOTTA, STONE);
        MATERIALS.put(Blocks.WHITE_CONCRETE, STONE);
        MATERIALS.put(Blocks.ORANGE_CONCRETE, STONE);
        MATERIALS.put(Blocks.MAGENTA_CONCRETE, STONE);
        MATERIALS.put(Blocks.LIGHT_BLUE_CONCRETE, STONE);
        MATERIALS.put(Blocks.YELLOW_CONCRETE, STONE);
        MATERIALS.put(Blocks.LIME_CONCRETE, STONE);
        MATERIALS.put(Blocks.PINK_CONCRETE, STONE);
        MATERIALS.put(Blocks.GRAY_CONCRETE, STONE);
        MATERIALS.put(Blocks.LIGHT_GRAY_CONCRETE, STONE);
        MATERIALS.put(Blocks.CYAN_CONCRETE, STONE);
        MATERIALS.put(Blocks.PURPLE_CONCRETE, STONE);
        MATERIALS.put(Blocks.BLUE_CONCRETE, STONE);
        MATERIALS.put(Blocks.BROWN_CONCRETE, STONE);
        MATERIALS.put(Blocks.GREEN_CONCRETE, STONE);
        MATERIALS.put(Blocks.RED_CONCRETE, STONE);
        MATERIALS.put(Blocks.BLACK_CONCRETE, STONE);
        MATERIALS.put(Blocks.WHITE_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.ORANGE_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.MAGENTA_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.LIGHT_BLUE_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.YELLOW_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.LIME_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.PINK_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.GRAY_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.LIGHT_GRAY_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.CYAN_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.PURPLE_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.BLUE_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.BROWN_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.GREEN_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.RED_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.BLACK_CONCRETE_POWDER, AGGREGATE);
        MATERIALS.put(Blocks.KELP, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.KELP_PLANT, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.DRIED_KELP_BLOCK, SOLID_ORGANIC);
        MATERIALS.put(Blocks.TURTLE_EGG, EGG);
        MATERIALS.put(Blocks.DEAD_TUBE_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.DEAD_BRAIN_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.DEAD_BUBBLE_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.DEAD_FIRE_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.DEAD_HORN_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.TUBE_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.BRAIN_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.BUBBLE_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.FIRE_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.HORN_CORAL_BLOCK, STONE);
        MATERIALS.put(Blocks.DEAD_TUBE_CORAL, STONE);
        MATERIALS.put(Blocks.DEAD_BRAIN_CORAL, STONE);
        MATERIALS.put(Blocks.DEAD_BUBBLE_CORAL, STONE);
        MATERIALS.put(Blocks.DEAD_FIRE_CORAL, STONE);
        MATERIALS.put(Blocks.DEAD_HORN_CORAL, STONE);
        MATERIALS.put(Blocks.TUBE_CORAL, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BRAIN_CORAL, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BUBBLE_CORAL, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.FIRE_CORAL, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.HORN_CORAL, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.DEAD_TUBE_CORAL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_BRAIN_CORAL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_BUBBLE_CORAL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_FIRE_CORAL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_HORN_CORAL_FAN, STONE);
        MATERIALS.put(Blocks.TUBE_CORAL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BRAIN_CORAL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BUBBLE_CORAL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.FIRE_CORAL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.HORN_CORAL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, STONE);
        MATERIALS.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, STONE);
        MATERIALS.put(Blocks.TUBE_CORAL_WALL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BRAIN_CORAL_WALL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BUBBLE_CORAL_WALL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.FIRE_CORAL_WALL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.HORN_CORAL_WALL_FAN, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.SEA_PICKLE, UNDERWATER_PLANT);
        MATERIALS.put(Blocks.BLUE_ICE, DENSE_ICE);
        MATERIALS.put(Blocks.CONDUIT, GLASS);
        MATERIALS.put(Blocks.BAMBOO_SAPLING, BAMBOO_SAPLING);
        MATERIALS.put(Blocks.BAMBOO, BAMBOO);
        MATERIALS.put(Blocks.POTTED_BAMBOO, DECORATION);
        MATERIALS.put(Blocks.VOID_AIR, AIR);
        MATERIALS.put(Blocks.CAVE_AIR, AIR);
        MATERIALS.put(Blocks.BUBBLE_COLUMN, BUBBLE_COLUMN);
        MATERIALS.put(Blocks.POLISHED_GRANITE_STAIRS, STONE);
        MATERIALS.put(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.MOSSY_STONE_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.POLISHED_DIORITE_STAIRS, STONE);
        MATERIALS.put(Blocks.MOSSY_COBBLESTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.END_STONE_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.STONE_STAIRS, STONE);
        MATERIALS.put(Blocks.SMOOTH_SANDSTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.SMOOTH_QUARTZ_STAIRS, STONE);
        MATERIALS.put(Blocks.GRANITE_STAIRS, STONE);
        MATERIALS.put(Blocks.ANDESITE_STAIRS, STONE);
        MATERIALS.put(Blocks.RED_NETHER_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.POLISHED_ANDESITE_STAIRS, STONE);
        MATERIALS.put(Blocks.DIORITE_STAIRS, STONE);
        MATERIALS.put(Blocks.POLISHED_GRANITE_SLAB, STONE);
        MATERIALS.put(Blocks.SMOOTH_RED_SANDSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.MOSSY_STONE_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.POLISHED_DIORITE_SLAB, STONE);
        MATERIALS.put(Blocks.MOSSY_COBBLESTONE_SLAB, STONE);
        MATERIALS.put(Blocks.END_STONE_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.SMOOTH_SANDSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.SMOOTH_QUARTZ_SLAB, STONE);
        MATERIALS.put(Blocks.GRANITE_SLAB, STONE);
        MATERIALS.put(Blocks.ANDESITE_SLAB, STONE);
        MATERIALS.put(Blocks.RED_NETHER_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.POLISHED_ANDESITE_SLAB, STONE);
        MATERIALS.put(Blocks.DIORITE_SLAB, STONE);
        MATERIALS.put(Blocks.BRICK_WALL, STONE);
        MATERIALS.put(Blocks.PRISMARINE_WALL, STONE);
        MATERIALS.put(Blocks.RED_SANDSTONE_WALL, STONE);
        MATERIALS.put(Blocks.MOSSY_STONE_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.GRANITE_WALL, STONE);
        MATERIALS.put(Blocks.STONE_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.MUD_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.NETHER_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.ANDESITE_WALL, STONE);
        MATERIALS.put(Blocks.RED_NETHER_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.SANDSTONE_WALL, STONE);
        MATERIALS.put(Blocks.END_STONE_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.DIORITE_WALL, STONE);
        MATERIALS.put(Blocks.SCAFFOLDING, DECORATION);
        MATERIALS.put(Blocks.LOOM, WOOD);
        MATERIALS.put(Blocks.BARREL, WOOD);
        MATERIALS.put(Blocks.SMOKER, STONE);
        MATERIALS.put(Blocks.BLAST_FURNACE, STONE);
        MATERIALS.put(Blocks.CARTOGRAPHY_TABLE, WOOD);
        MATERIALS.put(Blocks.FLETCHING_TABLE, WOOD);
        MATERIALS.put(Blocks.GRINDSTONE, REPAIR_STATION);
        MATERIALS.put(Blocks.LECTERN, WOOD);
        MATERIALS.put(Blocks.SMITHING_TABLE, WOOD);
        MATERIALS.put(Blocks.STONECUTTER, STONE);
        MATERIALS.put(Blocks.BELL, METAL);
        MATERIALS.put(Blocks.LANTERN, METAL);
        MATERIALS.put(Blocks.SOUL_LANTERN, METAL);
        MATERIALS.put(Blocks.CAMPFIRE, WOOD);
        MATERIALS.put(Blocks.SOUL_CAMPFIRE, WOOD);
        MATERIALS.put(Blocks.SWEET_BERRY_BUSH, PLANT);
        MATERIALS.put(Blocks.WARPED_STEM, NETHER_WOOD);
        MATERIALS.put(Blocks.STRIPPED_WARPED_STEM, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_HYPHAE, NETHER_WOOD);
        MATERIALS.put(Blocks.STRIPPED_WARPED_HYPHAE, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_NYLIUM, STONE);
        MATERIALS.put(Blocks.WARPED_FUNGUS, PLANT);
        MATERIALS.put(Blocks.WARPED_WART_BLOCK, SOLID_ORGANIC);
        MATERIALS.put(Blocks.WARPED_ROOTS, NETHER_SHOOTS);
        MATERIALS.put(Blocks.NETHER_SPROUTS, NETHER_SHOOTS);
        MATERIALS.put(Blocks.CRIMSON_STEM, NETHER_WOOD);
        MATERIALS.put(Blocks.STRIPPED_CRIMSON_STEM, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_HYPHAE, NETHER_WOOD);
        MATERIALS.put(Blocks.STRIPPED_CRIMSON_HYPHAE, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_NYLIUM, STONE);
        MATERIALS.put(Blocks.CRIMSON_FUNGUS, PLANT);
        MATERIALS.put(Blocks.SHROOMLIGHT, SOLID_ORGANIC);
        MATERIALS.put(Blocks.WEEPING_VINES, PLANT);
        MATERIALS.put(Blocks.WEEPING_VINES_PLANT, PLANT);
        MATERIALS.put(Blocks.TWISTING_VINES, PLANT);
        MATERIALS.put(Blocks.TWISTING_VINES_PLANT, PLANT);
        MATERIALS.put(Blocks.CRIMSON_ROOTS, NETHER_SHOOTS);
        MATERIALS.put(Blocks.CRIMSON_PLANKS, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_PLANKS, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_SLAB, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_SLAB, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_PRESSURE_PLATE, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_PRESSURE_PLATE, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_FENCE, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_FENCE, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_TRAPDOOR, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_TRAPDOOR, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_FENCE_GATE, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_FENCE_GATE, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_STAIRS, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_STAIRS, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_BUTTON, DECORATION);
        MATERIALS.put(Blocks.WARPED_BUTTON, DECORATION);
        MATERIALS.put(Blocks.CRIMSON_DOOR, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_DOOR, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_SIGN, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_SIGN, NETHER_WOOD);
        MATERIALS.put(Blocks.CRIMSON_WALL_SIGN, NETHER_WOOD);
        MATERIALS.put(Blocks.WARPED_WALL_SIGN, NETHER_WOOD);
        MATERIALS.put(Blocks.STRUCTURE_BLOCK, METAL);
        MATERIALS.put(Blocks.JIGSAW, METAL);
        MATERIALS.put(Blocks.COMPOSTER, WOOD);
        MATERIALS.put(Blocks.TARGET, SOLID_ORGANIC);
        MATERIALS.put(Blocks.BEE_NEST, WOOD);
        MATERIALS.put(Blocks.BEEHIVE, WOOD);
        MATERIALS.put(Blocks.HONEY_BLOCK, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.HONEYCOMB_BLOCK, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.NETHERITE_BLOCK, METAL);
        MATERIALS.put(Blocks.ANCIENT_DEBRIS, METAL);
        MATERIALS.put(Blocks.CRYING_OBSIDIAN, STONE);
        MATERIALS.put(Blocks.RESPAWN_ANCHOR, STONE);
        MATERIALS.put(Blocks.POTTED_CRIMSON_FUNGUS, DECORATION);
        MATERIALS.put(Blocks.POTTED_WARPED_FUNGUS, DECORATION);
        MATERIALS.put(Blocks.POTTED_CRIMSON_ROOTS, DECORATION);
        MATERIALS.put(Blocks.POTTED_WARPED_ROOTS, DECORATION);
        MATERIALS.put(Blocks.LODESTONE, REPAIR_STATION);
        MATERIALS.put(Blocks.BLACKSTONE, STONE);
        MATERIALS.put(Blocks.BLACKSTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.BLACKSTONE_WALL, STONE);
        MATERIALS.put(Blocks.BLACKSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_BRICKS, STONE);
        MATERIALS.put(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, STONE);
        MATERIALS.put(Blocks.CHISELED_POLISHED_BLACKSTONE, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.GILDED_BLACKSTONE, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_STAIRS, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_SLAB, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, STONE);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_BUTTON, DECORATION);
        MATERIALS.put(Blocks.POLISHED_BLACKSTONE_WALL, STONE);
        MATERIALS.put(Blocks.CHISELED_NETHER_BRICKS, STONE);
        MATERIALS.put(Blocks.CRACKED_NETHER_BRICKS, STONE);
        MATERIALS.put(Blocks.QUARTZ_BRICKS, STONE);
        MATERIALS.put(Blocks.CANDLE, DECORATION);
        MATERIALS.put(Blocks.WHITE_CANDLE, DECORATION);
        MATERIALS.put(Blocks.ORANGE_CANDLE, DECORATION);
        MATERIALS.put(Blocks.MAGENTA_CANDLE, DECORATION);
        MATERIALS.put(Blocks.LIGHT_BLUE_CANDLE, DECORATION);
        MATERIALS.put(Blocks.YELLOW_CANDLE, DECORATION);
        MATERIALS.put(Blocks.LIME_CANDLE, DECORATION);
        MATERIALS.put(Blocks.PINK_CANDLE, DECORATION);
        MATERIALS.put(Blocks.GRAY_CANDLE, DECORATION);
        MATERIALS.put(Blocks.LIGHT_GRAY_CANDLE, DECORATION);
        MATERIALS.put(Blocks.CYAN_CANDLE, DECORATION);
        MATERIALS.put(Blocks.PURPLE_CANDLE, DECORATION);
        MATERIALS.put(Blocks.BLUE_CANDLE, DECORATION);
        MATERIALS.put(Blocks.BROWN_CANDLE, DECORATION);
        MATERIALS.put(Blocks.GREEN_CANDLE, DECORATION);
        MATERIALS.put(Blocks.RED_CANDLE, DECORATION);
        MATERIALS.put(Blocks.BLACK_CANDLE, DECORATION);
        MATERIALS.put(Blocks.CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.WHITE_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.ORANGE_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.MAGENTA_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.LIGHT_BLUE_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.YELLOW_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.LIME_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.PINK_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.GRAY_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.LIGHT_GRAY_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.CYAN_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.PURPLE_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.BLUE_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.BROWN_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.GREEN_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.RED_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.BLACK_CANDLE_CAKE, CAKE);
        MATERIALS.put(Blocks.AMETHYST_BLOCK, AMETHYST);
        MATERIALS.put(Blocks.BUDDING_AMETHYST, AMETHYST);
        MATERIALS.put(Blocks.AMETHYST_CLUSTER, AMETHYST);
        MATERIALS.put(Blocks.LARGE_AMETHYST_BUD, AMETHYST);
        MATERIALS.put(Blocks.MEDIUM_AMETHYST_BUD, AMETHYST);
        MATERIALS.put(Blocks.SMALL_AMETHYST_BUD, AMETHYST);
        MATERIALS.put(Blocks.TUFF, STONE);
        MATERIALS.put(Blocks.CALCITE, STONE);
        MATERIALS.put(Blocks.TINTED_GLASS, GLASS);
        MATERIALS.put(Blocks.POWDER_SNOW, POWDER_SNOW);
        MATERIALS.put(Blocks.SCULK_SENSOR, SCULK);
        MATERIALS.put(Blocks.SCULK, SCULK);
        MATERIALS.put(Blocks.SCULK_VEIN, SCULK);
        MATERIALS.put(Blocks.SCULK_CATALYST, SCULK);
        MATERIALS.put(Blocks.SCULK_SHRIEKER, SCULK);
        MATERIALS.put(Blocks.OXIDIZED_COPPER, METAL);
        MATERIALS.put(Blocks.WEATHERED_COPPER, METAL);
        MATERIALS.put(Blocks.EXPOSED_COPPER, METAL);
        MATERIALS.put(Blocks.COPPER_BLOCK, METAL);
        MATERIALS.put(Blocks.COPPER_ORE, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_COPPER_ORE, STONE);
        MATERIALS.put(Blocks.OXIDIZED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.WEATHERED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.EXPOSED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.CUT_COPPER, METAL);
        MATERIALS.put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.WEATHERED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.EXPOSED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.OXIDIZED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.WEATHERED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.EXPOSED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.WAXED_COPPER_BLOCK, METAL);
        MATERIALS.put(Blocks.WAXED_WEATHERED_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_EXPOSED_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_OXIDIZED_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_OXIDIZED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_WEATHERED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_EXPOSED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_CUT_COPPER, METAL);
        MATERIALS.put(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.WAXED_CUT_COPPER_STAIRS, METAL);
        MATERIALS.put(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.WAXED_CUT_COPPER_SLAB, METAL);
        MATERIALS.put(Blocks.LIGHTNING_ROD, METAL);
        MATERIALS.put(Blocks.POINTED_DRIPSTONE, STONE);
        MATERIALS.put(Blocks.DRIPSTONE_BLOCK, STONE);
        MATERIALS.put(Blocks.CAVE_VINES, PLANT);
        MATERIALS.put(Blocks.CAVE_VINES_PLANT, PLANT);
        MATERIALS.put(Blocks.SPORE_BLOSSOM, PLANT);
        MATERIALS.put(Blocks.AZALEA, PLANT);
        MATERIALS.put(Blocks.FLOWERING_AZALEA, PLANT);
        MATERIALS.put(Blocks.MOSS_CARPET, PLANT);
        MATERIALS.put(Blocks.PINK_PETALS, PLANT);
        MATERIALS.put(Blocks.MOSS_BLOCK, MOSS_BLOCK);
        MATERIALS.put(Blocks.BIG_DRIPLEAF, PLANT);
        MATERIALS.put(Blocks.BIG_DRIPLEAF_STEM, PLANT);
        MATERIALS.put(Blocks.SMALL_DRIPLEAF, PLANT);
        MATERIALS.put(Blocks.HANGING_ROOTS, REPLACEABLE_PLANT);
        MATERIALS.put(Blocks.ROOTED_DIRT, SOIL);
        MATERIALS.put(Blocks.MUD, SOIL);
        MATERIALS.put(Blocks.DEEPSLATE, STONE);
        MATERIALS.put(Blocks.COBBLED_DEEPSLATE, STONE);
        MATERIALS.put(Blocks.COBBLED_DEEPSLATE_STAIRS, STONE);
        MATERIALS.put(Blocks.COBBLED_DEEPSLATE_SLAB, STONE);
        MATERIALS.put(Blocks.COBBLED_DEEPSLATE_WALL, STONE);
        MATERIALS.put(Blocks.POLISHED_DEEPSLATE, STONE);
        MATERIALS.put(Blocks.POLISHED_DEEPSLATE_STAIRS, STONE);
        MATERIALS.put(Blocks.POLISHED_DEEPSLATE_SLAB, STONE);
        MATERIALS.put(Blocks.POLISHED_DEEPSLATE_WALL, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_TILES, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_TILE_STAIRS, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_TILE_SLAB, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_TILE_WALL, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_BRICKS, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_BRICK_STAIRS, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_BRICK_SLAB, STONE);
        MATERIALS.put(Blocks.DEEPSLATE_BRICK_WALL, STONE);
        MATERIALS.put(Blocks.CHISELED_DEEPSLATE, STONE);
        MATERIALS.put(Blocks.CRACKED_DEEPSLATE_BRICKS, STONE);
        MATERIALS.put(Blocks.CRACKED_DEEPSLATE_TILES, STONE);
        MATERIALS.put(Blocks.INFESTED_DEEPSLATE, ORGANIC_PRODUCT);
        MATERIALS.put(Blocks.SMOOTH_BASALT, STONE);
        MATERIALS.put(Blocks.RAW_IRON_BLOCK, STONE);
        MATERIALS.put(Blocks.RAW_COPPER_BLOCK, STONE);
        MATERIALS.put(Blocks.RAW_GOLD_BLOCK, STONE);
        MATERIALS.put(Blocks.POTTED_AZALEA_BUSH, DECORATION);
        MATERIALS.put(Blocks.POTTED_FLOWERING_AZALEA_BUSH, DECORATION);
        MATERIALS.put(Blocks.OCHRE_FROGLIGHT, FROGLIGHT);
        MATERIALS.put(Blocks.VERDANT_FROGLIGHT, FROGLIGHT);
        MATERIALS.put(Blocks.PEARLESCENT_FROGLIGHT, FROGLIGHT);
        MATERIALS.put(Blocks.FROGSPAWN, FROGSPAWN);
        MATERIALS.put(Blocks.REINFORCED_DEEPSLATE, STONE);
        MATERIALS.put(Blocks.DECORATED_POT, DECORATED_POT);
    }

    /**
     * @param block The block to get the material of
     * @return The material of the block for game version 1.19.4
     */
    public static Material1_19_4 getMaterial(final Block block) {
        if (block instanceof ShulkerBoxBlock && ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_14)) {
            return STONE;
        } else {
            return MATERIALS.get(block);
        }
    }

    /**
     * @param blockState The block state to get the material of
     * @return The material of the block state for game version 1.19.4
     */
    public static Material1_19_4 getMaterial(final BlockState blockState) {
        return getMaterial(blockState.getBlock());
    }

    public boolean blocksMovement() {
        return this.blocksMovement;
    }

    public boolean burnable() {
        return this.burnable;
    }

    public boolean liquid() {
        return this.liquid;
    }

    public boolean blocksLight() {
        return this.blocksLight;
    }

    public boolean replaceable() {
        return this.replaceable;
    }

    public boolean solid() {
        return this.solid;
    }

}
