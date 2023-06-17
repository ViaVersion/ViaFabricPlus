/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.mappings;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.base.event.ChangeProtocolVersionCallback;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.raphimc.vialoader.util.VersionRange;

import java.util.*;

public class ItemReleaseVersionMappings {
    public static ItemReleaseVersionMappings INSTANCE;

    public static void create() {
        INSTANCE = new ItemReleaseVersionMappings();
        INSTANCE.load();
        INSTANCE.update(ProtocolHack.getTargetVersion());

        ChangeProtocolVersionCallback.EVENT.register(protocolVersion -> INSTANCE.update(protocolVersion));
    }

    private final Map<Item, VersionRange[]> itemMap = new HashMap<>();
    private final List<Item> currentMap = new ArrayList<>();

    public void update(final VersionEnum versionEnum) {
        INSTANCE.currentMap.clear();
        INSTANCE.currentMap.addAll(Registries.ITEM.stream().filter(item -> INSTANCE.contains(item, versionEnum)).toList());
    }

    public boolean contains(final Item item, final VersionEnum versionEnum) {
        if (!itemMap.containsKey(item)) return true;

        return Arrays.stream(itemMap.get(item)).anyMatch(versionRange -> versionRange.contains(versionEnum));
    }

    public void load() {
        register(Items.CALIBRATED_SCULK_SENSOR, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.PITCHER_PLANT, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SNIFFER_EGG, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SUSPICIOUS_GRAVEL, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.PITCHER_POD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.ANGLER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.BLADE_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.BREWER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.BURN_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.DANGER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.EXPLORER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.FRIEND_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.HEART_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.HEARTBREAK_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.HOWL_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.MINER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.MOURNER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.PLENTY_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SHEAF_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SHELTER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.SNORT_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));
        register(Items.MUSIC_DISC_RELIC, VersionRange.andNewer(VersionEnum.r1_20tor1_20_1));

        register(Items.BRUSH, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_BOAT, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_BUTTON, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_DOOR, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_FENCE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_HANGING_SIGN, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_LEAVES, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_LOG, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_PLANKS, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_SAPLING, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_SIGN, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_SLAB, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_STAIRS, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.CHERRY_WOOD, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.DECORATED_POT, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.PINK_PETALS, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.ARCHER_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.ARMS_UP_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.PRIZE_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.SKULL_POTTERY_SHERD, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.SNIFFER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.STRIPPED_CHERRY_LOG, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.STRIPPED_CHERRY_WOOD, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.SUSPICIOUS_SAND, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.TORCHFLOWER, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.TORCHFLOWER_SEEDS, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));
        register(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, VersionRange.andNewer(VersionEnum.r1_19_4));

        register(Items.IRON_GOLEM_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19_3));
        register(Items.SNOW_GOLEM_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19_3));

        register(Items.ACACIA_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.ALLAY_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.BIRCH_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.DARK_OAK_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.DISC_FRAGMENT_5, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.ECHO_SHARD, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.FROG_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.FROGSPAWN, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.GOAT_HORN, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.JUNGLE_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_BUTTON, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_DOOR, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_FENCE, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_LEAVES, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_LOG, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_PLANKS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_PROPAGULE, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_ROOTS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_SIGN, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_SLAB, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_STAIRS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MANGROVE_WOOD, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUD, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUD_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUD_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUD_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUD_BRICKS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUDDY_MANGROVE_ROOTS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.MUSIC_DISC_5, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.OAK_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.OCHRE_FROGLIGHT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.PACKED_MUD, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.PEARLESCENT_FROGLIGHT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.RECOVERY_COMPASS, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.REINFORCED_DEEPSLATE, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.SCULK, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.SCULK_CATALYST, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.SCULK_SHRIEKER, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.SCULK_VEIN, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.SPRUCE_CHEST_BOAT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.STRIPPED_MANGROVE_LOG, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.STRIPPED_MANGROVE_WOOD, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.TADPOLE_BUCKET, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.TADPOLE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.VERDANT_FROGLIGHT, VersionRange.andNewer(VersionEnum.r1_19));
        register(Items.WARDEN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_19));

        register(Items.MUSIC_DISC_OTHERSIDE, VersionRange.andNewer(VersionEnum.r1_18tor1_18_1));

        register(Items.AMETHYST_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.AMETHYST_CLUSTER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.AMETHYST_SHARD, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.AXOLOTL_BUCKET, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.AXOLOTL_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.AZALEA, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.AZALEA_LEAVES, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.BIG_DRIPLEAF, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.BLACK_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.BLUE_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.BROWN_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.BUDDING_AMETHYST, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.BUNDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CALCITE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CHISELED_DEEPSLATE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COBBLED_DEEPSLATE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COBBLED_DEEPSLATE_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COBBLED_DEEPSLATE_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COBBLED_DEEPSLATE_WALL, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COPPER_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COPPER_INGOT, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.COPPER_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CRACKED_DEEPSLATE_BRICKS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CRACKED_DEEPSLATE_TILES, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.CYAN_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_BRICKS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_COAL_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_COPPER_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_DIAMOND_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_EMERALD_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_GOLD_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_IRON_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_LAPIS_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_REDSTONE_ORE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_TILE_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_TILE_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_TILE_WALL, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DEEPSLATE_TILES, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.DRIPSTONE_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.EXPOSED_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.EXPOSED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.EXPOSED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.EXPOSED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.FLOWERING_AZALEA, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.FLOWERING_AZALEA_LEAVES, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GLOW_BERRIES, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GLOW_INK_SAC, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GLOW_ITEM_FRAME, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GLOW_LICHEN, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GLOW_SQUID_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GOAT_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GRAY_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.GREEN_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.HANGING_ROOTS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.INFESTED_DEEPSLATE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.LARGE_AMETHYST_BUD, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.LIGHT, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.LIGHT_BLUE_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.LIGHT_GRAY_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.LIGHTNING_ROD, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.LIME_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.MAGENTA_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.MEDIUM_AMETHYST_BUD, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.MOSS_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.MOSS_CARPET, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.ORANGE_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.OXIDIZED_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.OXIDIZED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.OXIDIZED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.OXIDIZED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.PINK_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.POINTED_DRIPSTONE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.POLISHED_DEEPSLATE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.POLISHED_DEEPSLATE_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.POLISHED_DEEPSLATE_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.POLISHED_DEEPSLATE_WALL, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.POWDER_SNOW_BUCKET, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.PURPLE_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RAW_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RAW_COPPER_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RAW_GOLD, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RAW_GOLD_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RAW_IRON, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RAW_IRON_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.RED_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.ROOTED_DIRT, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.SCULK_SENSOR, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.SMALL_AMETHYST_BUD, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.SMALL_DRIPLEAF, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.SMOOTH_BASALT, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.SPORE_BLOSSOM, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.SPYGLASS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.TINTED_GLASS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.TUFF, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_COPPER_BLOCK, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_EXPOSED_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_EXPOSED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_EXPOSED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_EXPOSED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_OXIDIZED_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_OXIDIZED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_WEATHERED_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_WEATHERED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_WEATHERED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WAXED_WEATHERED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WEATHERED_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WEATHERED_CUT_COPPER, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WEATHERED_CUT_COPPER_SLAB, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WEATHERED_CUT_COPPER_STAIRS, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.WHITE_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));
        register(Items.YELLOW_CANDLE, VersionRange.andNewer(VersionEnum.r1_17));

        register(Items.ANCIENT_DEBRIS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.BASALT, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.BLACKSTONE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.BLACKSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.BLACKSTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.BLACKSTONE_WALL, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CHAIN, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CHISELED_NETHER_BRICKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CHISELED_POLISHED_BLACKSTONE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRACKED_NETHER_BRICKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_BUTTON, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_DOOR, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_FENCE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_FUNGUS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_HYPHAE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_NYLIUM, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_PLANKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_ROOTS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_SIGN, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_SLAB, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_STAIRS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_STEM, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRIMSON_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.CRYING_OBSIDIAN, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.GILDED_BLACKSTONE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.HOGLIN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.LODESTONE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.MUSIC_DISC_PIGSTEP, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHER_GOLD_ORE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHER_SPROUTS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_AXE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_BLOCK, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_BOOTS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_CHESTPLATE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_HELMET, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_HOE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_INGOT, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_LEGGINGS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_PICKAXE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_SCRAP, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_SHOVEL, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.NETHERITE_SWORD, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.PIGLIN_BANNER_PATTERN, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.PIGLIN_BRUTE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.PIGLIN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BASALT, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_BUTTON, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.POLISHED_BLACKSTONE_WALL, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.QUARTZ_BRICKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.RESPAWN_ANCHOR, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.SHROOMLIGHT, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.SOUL_CAMPFIRE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.SOUL_LANTERN, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.SOUL_SOIL, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.SOUL_TORCH, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.STRIDER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.STRIPPED_CRIMSON_HYPHAE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.STRIPPED_CRIMSON_STEM, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.STRIPPED_WARPED_HYPHAE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.STRIPPED_WARPED_STEM, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.TARGET, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.TWISTING_VINES, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_BUTTON, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_DOOR, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_FENCE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_FUNGUS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_FUNGUS_ON_A_STICK, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_HYPHAE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_NYLIUM, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_PLANKS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_ROOTS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_SIGN, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_SLAB, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_STAIRS, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_STEM, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WARPED_WART_BLOCK, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.WEEPING_VINES, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.ZOGLIN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_16));
        register(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_16));

        register(Items.BEE_NEST, VersionRange.andNewer(VersionEnum.r1_15));
        register(Items.BEE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_15));
        register(Items.BEEHIVE, VersionRange.andNewer(VersionEnum.r1_15));
        register(Items.HONEY_BLOCK, VersionRange.andNewer(VersionEnum.r1_15));
        register(Items.HONEY_BOTTLE, VersionRange.andNewer(VersionEnum.r1_15));
        register(Items.HONEYCOMB, VersionRange.andNewer(VersionEnum.r1_15));
        register(Items.HONEYCOMB_BLOCK, VersionRange.andNewer(VersionEnum.r1_15));

        register(Items.ACACIA_SIGN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.ANDESITE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.ANDESITE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.ANDESITE_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BAMBOO, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BARREL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BELL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BIRCH_SIGN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BLACK_DYE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BLAST_FURNACE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BLUE_DYE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.BROWN_DYE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CAMPFIRE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CARTOGRAPHY_TABLE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CAT_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.COMPOSTER, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CORNFLOWER, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CREEPER_BANNER_PATTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CROSSBOW, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CUT_RED_SANDSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.CUT_SANDSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.DARK_OAK_SIGN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.DIORITE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.DIORITE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.DIORITE_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.END_STONE_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.END_STONE_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.END_STONE_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.FLETCHING_TABLE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.FLOWER_BANNER_PATTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.FOX_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.GLOBE_BANNER_PATTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.GRANITE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.GRANITE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.GRANITE_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.GRINDSTONE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.JIGSAW, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.JUNGLE_SIGN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.LANTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.LEATHER_HORSE_ARMOR, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.LECTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.LILY_OF_THE_VALLEY, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.LOOM, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.MOJANG_BANNER_PATTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.MOSSY_COBBLESTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.MOSSY_COBBLESTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.MOSSY_STONE_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.MOSSY_STONE_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.MOSSY_STONE_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.NETHER_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.PANDA_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.PILLAGER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.POLISHED_ANDESITE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.POLISHED_ANDESITE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.POLISHED_DIORITE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.POLISHED_DIORITE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.POLISHED_GRANITE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.POLISHED_GRANITE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.PRISMARINE_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.RAVAGER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.RED_NETHER_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.RED_NETHER_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.RED_NETHER_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.RED_SANDSTONE_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SANDSTONE_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SCAFFOLDING, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SKULL_BANNER_PATTERN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMITHING_TABLE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOKER, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOOTH_QUARTZ_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOOTH_QUARTZ_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOOTH_RED_SANDSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOOTH_RED_SANDSTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOOTH_SANDSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SMOOTH_SANDSTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SPRUCE_SIGN, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.STONE_BRICK_WALL, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.STONE_SLAB, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.STONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.STONECUTTER, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SUSPICIOUS_STEW, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.SWEET_BERRIES, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.TRADER_LLAMA_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.WANDERING_TRADER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.WHITE_DYE, VersionRange.andNewer(VersionEnum.r1_14));
        register(Items.WITHER_ROSE, VersionRange.andNewer(VersionEnum.r1_14));

        register(Items.ACACIA_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.ACACIA_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.ACACIA_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BIRCH_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BIRCH_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BIRCH_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BLUE_ICE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BRAIN_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BRAIN_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BRAIN_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BUBBLE_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BUBBLE_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.BUBBLE_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.CARVED_PUMPKIN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.CHIPPED_ANVIL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.COD_BUCKET, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.COD_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.CONDUIT, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DAMAGED_ANVIL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DARK_OAK_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DARK_OAK_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DARK_OAK_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DARK_PRISMARINE_SLAB, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DARK_PRISMARINE_STAIRS, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_BRAIN_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_BRAIN_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_BRAIN_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_BUBBLE_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_BUBBLE_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_BUBBLE_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_FIRE_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_FIRE_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_FIRE_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_HORN_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_HORN_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_HORN_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_TUBE_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_TUBE_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEAD_TUBE_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DEBUG_STICK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DOLPHIN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DRIED_KELP, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DRIED_KELP_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.DROWNED_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.FIRE_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.FIRE_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.FIRE_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.HEART_OF_THE_SEA, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.HORN_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.HORN_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.HORN_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.JUNGLE_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.JUNGLE_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.JUNGLE_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.KELP, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.MUSHROOM_STEM, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.NAUTILUS_SHELL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.OAK_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        //register(Items.PETRIFIED_OAK_SLAB, VersionRange.andNewer(VersionEnum.r1_13)); it's right, but the item exists in another form before, so it's actually wrong
        register(Items.PHANTOM_MEMBRANE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PHANTOM_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PRISMARINE_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PRISMARINE_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PRISMARINE_SLAB, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PRISMARINE_STAIRS, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PUFFERFISH_BUCKET, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.PUFFERFISH_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SALMON_BUCKET, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SALMON_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SCUTE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SEA_PICKLE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SEAGRASS, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SMOOTH_QUARTZ, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SMOOTH_RED_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SMOOTH_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SMOOTH_STONE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SPRUCE_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SPRUCE_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.SPRUCE_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_ACACIA_LOG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_ACACIA_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_BIRCH_LOG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_BIRCH_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_DARK_OAK_LOG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_DARK_OAK_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_JUNGLE_LOG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_JUNGLE_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_OAK_LOG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_OAK_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_SPRUCE_LOG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.STRIPPED_SPRUCE_WOOD, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TRIDENT, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TROPICAL_FISH_BUCKET, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TROPICAL_FISH_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TUBE_CORAL, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TUBE_CORAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TUBE_CORAL_FAN, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TURTLE_EGG, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TURTLE_HELMET, VersionRange.andNewer(VersionEnum.r1_13));
        register(Items.TURTLE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_13));

        register(Items.BLACK_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLACK_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLACK_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLACK_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLUE_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLUE_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLUE_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BLUE_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BROWN_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BROWN_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BROWN_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.BROWN_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.CYAN_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.CYAN_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.CYAN_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.CYAN_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GRAY_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GRAY_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GRAY_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GRAY_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GREEN_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GREEN_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GREEN_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.GREEN_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.KNOWLEDGE_BOOK, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_BLUE_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_BLUE_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_BLUE_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_BLUE_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_GRAY_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_GRAY_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_GRAY_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIGHT_GRAY_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIME_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIME_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIME_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.LIME_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.MAGENTA_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.MAGENTA_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.MAGENTA_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.MAGENTA_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.ORANGE_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.ORANGE_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.ORANGE_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.ORANGE_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PARROT_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PINK_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PINK_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PINK_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PINK_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PURPLE_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PURPLE_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PURPLE_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.PURPLE_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.RED_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.RED_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.RED_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.WHITE_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.WHITE_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.WHITE_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.WHITE_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.YELLOW_BED, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.YELLOW_CONCRETE, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.YELLOW_CONCRETE_POWDER, VersionRange.andNewer(VersionEnum.r1_12));
        register(Items.YELLOW_GLAZED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_12));

        register(Items.BLACK_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.BLUE_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.BROWN_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.CYAN_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.DONKEY_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.EVOKER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.FILLED_MAP, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.GRAY_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.GREEN_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.HUSK_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.IRON_NUGGET, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.LIGHT_BLUE_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.LIGHT_GRAY_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.LIME_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.LLAMA_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.MAGENTA_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.MULE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.OBSERVER, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.ORANGE_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.PINK_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.PURPLE_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.RED_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.SHULKER_SHELL, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.SKELETON_HORSE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.STRAY_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.TOTEM_OF_UNDYING, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.VEX_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.VINDICATOR_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.WHITE_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.WITHER_SKELETON_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.YELLOW_SHULKER_BOX, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.ZOMBIE_HORSE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));
        register(Items.ZOMBIE_VILLAGER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_11));

        register(Items.BONE_BLOCK, VersionRange.andNewer(VersionEnum.r1_10));
        register(Items.MAGMA_BLOCK, VersionRange.andNewer(VersionEnum.r1_10), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.NETHER_WART_BLOCK, VersionRange.andNewer(VersionEnum.r1_10));
        register(Items.POLAR_BEAR_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_10));
        register(Items.RED_NETHER_BRICKS, VersionRange.andNewer(VersionEnum.r1_10));
        register(Items.STRUCTURE_VOID, VersionRange.andNewer(VersionEnum.r1_10));

        register(Items.ACACIA_BOAT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.BEETROOT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.BEETROOT_SEEDS, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.BEETROOT_SOUP, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.BIRCH_BOAT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.CHAIN_COMMAND_BLOCK, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.CHORUS_FLOWER, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.CHORUS_FRUIT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.CHORUS_PLANT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.DARK_OAK_BOAT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.DIRT_PATH, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.DRAGON_BREATH, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.DRAGON_HEAD, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.ELYTRA, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.END_CRYSTAL, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.END_ROD, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.END_STONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.JUNGLE_BOAT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.LINGERING_POTION, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.POPPED_CHORUS_FRUIT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.PURPUR_BLOCK, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.PURPUR_PILLAR, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.PURPUR_SLAB, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.PURPUR_STAIRS, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.REPEATING_COMMAND_BLOCK, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.SHIELD, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.SHULKER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.SPECTRAL_ARROW, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.SPRUCE_BOAT, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.STRUCTURE_BLOCK, VersionRange.andNewer(VersionEnum.r1_9));
        register(Items.TIPPED_ARROW, VersionRange.andNewer(VersionEnum.r1_9));

        register(Items.ACACIA_DOOR, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ACACIA_FENCE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ACACIA_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ANDESITE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ARMOR_STAND, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BARRIER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BIRCH_DOOR, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BIRCH_FENCE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BIRCH_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BLACK_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BLUE_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.BROWN_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.CHISELED_RED_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.COARSE_DIRT, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.COOKED_MUTTON, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.COOKED_RABBIT, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.CREEPER_HEAD, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.CUT_RED_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.CYAN_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.DARK_OAK_DOOR, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.DARK_OAK_FENCE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.DARK_OAK_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.DARK_PRISMARINE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.DIORITE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ELDER_GUARDIAN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ENDERMITE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.GRANITE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.GRAY_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.GREEN_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.GUARDIAN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.IRON_TRAPDOOR, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.JUNGLE_DOOR, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.JUNGLE_FENCE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.JUNGLE_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.LIGHT_BLUE_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.LIGHT_GRAY_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.LIME_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.MAGENTA_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.MUTTON, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ORANGE_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PINK_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PLAYER_HEAD, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.POLISHED_ANDESITE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.POLISHED_DIORITE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.POLISHED_GRANITE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PRISMARINE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PRISMARINE_BRICKS, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PRISMARINE_CRYSTALS, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PRISMARINE_SHARD, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.PURPLE_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RABBIT, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RABBIT_FOOT, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RABBIT_HIDE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RABBIT_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RABBIT_STEW, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RED_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RED_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RED_SANDSTONE_SLAB, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.RED_SANDSTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SEA_LANTERN, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SKELETON_SKULL, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SLIME_BLOCK, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SPONGE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SPRUCE_DOOR, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SPRUCE_FENCE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.SPRUCE_FENCE_GATE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.WET_SPONGE, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.WHITE_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.WITHER_SKELETON_SKULL, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.YELLOW_BANNER, VersionRange.andNewer(VersionEnum.r1_8));
        register(Items.ZOMBIE_HEAD, VersionRange.andNewer(VersionEnum.r1_8));

        register(Items.ACACIA_BUTTON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ACACIA_LEAVES, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ACACIA_LOG, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ACACIA_PLANKS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ACACIA_SAPLING, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ACACIA_SLAB, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ACACIA_STAIRS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ALLIUM, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.AZURE_BLUET, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BIRCH_BUTTON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BLACK_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BLACK_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BLUE_ORCHID, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BLUE_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BLUE_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BROWN_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.BROWN_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.COMMAND_BLOCK_MINECART, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.COOKED_SALMON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.CYAN_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.CYAN_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_BUTTON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_LEAVES, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_LOG, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_PLANKS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_SAPLING, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_SLAB, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.DARK_OAK_STAIRS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.GRAY_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.GRAY_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.GREEN_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.GREEN_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.INFESTED_CHISELED_STONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.INFESTED_CRACKED_STONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.INFESTED_MOSSY_STONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.JUNGLE_BUTTON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LARGE_FERN, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LIGHT_BLUE_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LIGHT_BLUE_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LIGHT_GRAY_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LIGHT_GRAY_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LILAC, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LIME_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.LIME_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.MAGENTA_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.MAGENTA_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ORANGE_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ORANGE_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ORANGE_TULIP, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.OXEYE_DAISY, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PACKED_ICE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PEONY, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PINK_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PINK_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PINK_TULIP, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PODZOL, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PUFFERFISH, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PURPLE_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.PURPLE_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.RED_SAND, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.RED_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.RED_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.RED_TULIP, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.ROSE_BUSH, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.SALMON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.SPRUCE_BUTTON, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.SUNFLOWER, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.TALL_GRASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.TROPICAL_FISH, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.WHITE_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.WHITE_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.WHITE_TULIP, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.YELLOW_STAINED_GLASS, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));
        register(Items.YELLOW_STAINED_GLASS_PANE, VersionRange.andNewer(VersionEnum.r1_7_2tor1_7_5));

        register(Items.BLACK_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.BLACK_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.BLUE_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.BLUE_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.BROWN_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.BROWN_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.COAL_BLOCK, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.CYAN_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.CYAN_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.DIAMOND_HORSE_ARMOR, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.GOLDEN_HORSE_ARMOR, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.GRAY_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.GRAY_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.GREEN_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.GREEN_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.HAY_BLOCK, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.HORSE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.IRON_HORSE_ARMOR, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LEAD, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LIGHT_BLUE_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LIGHT_BLUE_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LIGHT_GRAY_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LIGHT_GRAY_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LIME_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.LIME_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.MAGENTA_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.MAGENTA_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.NAME_TAG, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.ORANGE_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.ORANGE_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.PINK_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.PINK_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.PURPLE_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.PURPLE_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.RED_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.RED_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.WHITE_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.WHITE_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.YELLOW_CARPET, VersionRange.andNewer(VersionEnum.r1_6_1));
        register(Items.YELLOW_TERRACOTTA, VersionRange.andNewer(VersionEnum.r1_6_1));

        register(Items.ACTIVATOR_RAIL, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.CHISELED_QUARTZ_BLOCK, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.COMPARATOR, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.DAYLIGHT_DETECTOR, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.DROPPER, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.HOPPER, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.HOPPER_MINECART, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.LIGHT_WEIGHTED_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.NETHER_BRICK, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.NETHER_QUARTZ_ORE, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.QUARTZ, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.QUARTZ_BLOCK, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.QUARTZ_PILLAR, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.QUARTZ_SLAB, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.QUARTZ_STAIRS, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.REDSTONE_BLOCK, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.TNT_MINECART, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));
        register(Items.TRAPPED_CHEST, VersionRange.andNewer(VersionEnum.r1_5tor1_5_1));

        register(Items.ANVIL, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.BAKED_POTATO, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.BAT_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.BEACON, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.CARROT, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.CARROT_ON_A_STICK, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.COBBLESTONE_WALL, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.COMMAND_BLOCK, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.ENCHANTED_BOOK, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.FIREWORK_ROCKET, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.FIREWORK_STAR, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.FLOWER_POT, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.GOLDEN_CARROT, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.ITEM_FRAME, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.MAP, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.MOSSY_COBBLESTONE_WALL, VersionRange.andNewer(VersionEnum.r1_4_2));
        // "[The disc was] made available in survival" Is this the release in which it was added or made available?
        register(Items.MUSIC_DISC_WAIT, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.NETHER_BRICK_SLAB, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.NETHER_STAR, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.OAK_BUTTON, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.POISONOUS_POTATO, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.POTATO, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.PUMPKIN_PIE, VersionRange.andNewer(VersionEnum.r1_4_2));
        register(Items.WITCH_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_4_2));

        register(Items.BIRCH_PLANKS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.BIRCH_SLAB, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.BIRCH_STAIRS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.COCOA_BEANS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.EMERALD, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.EMERALD_BLOCK, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.EMERALD_ORE, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.ENCHANTED_GOLDEN_APPLE, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.ENDER_CHEST, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.JUNGLE_PLANKS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.JUNGLE_SLAB, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.JUNGLE_STAIRS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.SANDSTONE_STAIRS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.SPRUCE_PLANKS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.SPRUCE_SLAB, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.SPRUCE_STAIRS, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.TRIPWIRE_HOOK, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.WRITABLE_BOOK, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));
        register(Items.WRITTEN_BOOK, VersionRange.andNewer(VersionEnum.r1_3_1tor1_3_2));

        register(Items.CHISELED_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.CHISELED_STONE_BRICKS, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.CUT_SANDSTONE, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.EXPERIENCE_BOTTLE, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.FIRE_CHARGE, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.JUNGLE_LEAVES, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.JUNGLE_LOG, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.JUNGLE_SAPLING, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.OCELOT_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));
        register(Items.REDSTONE_LAMP, VersionRange.andNewer(VersionEnum.r1_2_1tor1_2_3));

        register(Items.BLAZE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.CAVE_SPIDER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.CHICKEN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.COW_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.CREEPER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.ENDERMAN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.GHAST_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.MAGMA_CUBE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.MOOSHROOM_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.PIG_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.SHEEP_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.SILVERFISH_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.SKELETON_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.SLIME_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.SPIDER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.SQUID_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.VILLAGER_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.WOLF_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.ZOMBIE_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));
        register(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, VersionRange.andNewer(VersionEnum.r1_1));

        register(Items.BLAZE_POWDER, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.BLAZE_ROD, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.BREWING_STAND, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.CAULDRON, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.DRAGON_EGG, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.ENCHANTING_TABLE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.END_PORTAL_FRAME, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.END_STONE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.ENDER_EYE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.FERMENTED_SPIDER_EYE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.GHAST_TEAR, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.GLASS_BOTTLE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.GLISTERING_MELON_SLICE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.GOLD_NUGGET, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MAGMA_CREAM, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_11, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_BLOCKS, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_CHIRP, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_FAR, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_MALL, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_MELLOHI, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_STAL, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_STRAD, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_WARD, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.NETHER_BRICK_FENCE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.NETHER_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.NETHER_BRICKS, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.NETHER_WART, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.POTION, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.SPIDER_EYE, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.SPLASH_POTION, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));

        // b1.9-pre1
        register(Items.LILY_PAD, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));
        register(Items.MYCELIUM, VersionRange.andNewer(VersionEnum.r1_0_0tor1_0_1));

        register(Items.BEEF, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.BRICK_SLAB, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.BRICK_STAIRS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.BROWN_MUSHROOM_BLOCK, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.CHICKEN, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.COOKED_BEEF, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.COOKED_CHICKEN, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.CRACKED_STONE_BRICKS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.ENDER_PEARL, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.GLASS_PANE, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.INFESTED_COBBLESTONE, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.INFESTED_CRACKED_STONE_BRICKS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.INFESTED_MOSSY_STONE_BRICKS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.INFESTED_STONE, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.INFESTED_STONE_BRICKS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.IRON_BARS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.MELON, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.MELON_SEEDS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.MELON_SLICE, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.MOSSY_STONE_BRICKS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.MUSHROOM_STEM, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.OAK_FENCE_GATE, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.PUMPKIN_SEEDS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.RED_MUSHROOM_BLOCK, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.ROTTEN_FLESH, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.STONE_BRICK_SLAB, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.STONE_BRICK_STAIRS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));
        register(Items.STONE_BRICKS, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.VINE, VersionRange.andNewer(VersionEnum.b1_8tob1_8_1));

        register(Items.PISTON, VersionRange.andNewer(VersionEnum.b1_7tob1_7_3));
        register(Items.SHEARS, VersionRange.andNewer(VersionEnum.b1_7tob1_7_3));
        register(Items.STICKY_PISTON, VersionRange.andNewer(VersionEnum.b1_7tob1_7_3));

        register(Items.DEAD_BUSH, VersionRange.andNewer(VersionEnum.b1_6tob1_6_6));
        register(Items.FERN, VersionRange.andNewer(VersionEnum.b1_6tob1_6_6));
        register(Items.GRASS, VersionRange.andNewer(VersionEnum.b1_6tob1_6_6));
        register(Items.MAP, VersionRange.andNewer(VersionEnum.b1_6tob1_6_6));
        register(Items.OAK_TRAPDOOR, VersionRange.andNewer(VersionEnum.b1_6tob1_6_6));

        register(Items.BIRCH_SAPLING, VersionRange.andNewer(VersionEnum.b1_5tob1_5_2));
        register(Items.COBWEB, VersionRange.andNewer(VersionEnum.b1_5tob1_5_2));
        register(Items.DETECTOR_RAIL, VersionRange.andNewer(VersionEnum.b1_5tob1_5_2));
        register(Items.POWERED_RAIL, VersionRange.andNewer(VersionEnum.b1_5tob1_5_2));
        register(Items.SPRUCE_SAPLING, VersionRange.andNewer(VersionEnum.b1_5tob1_5_2));

        register(Items.COOKIE, VersionRange.andNewer(VersionEnum.b1_4tob1_4_1));
        register(Items.TRAPPED_CHEST, VersionRange.andNewer(VersionEnum.b1_4tob1_4_1));

        register(Items.COBBLESTONE_SLAB, VersionRange.andNewer(VersionEnum.b1_3tob1_3_1), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.OAK_SLAB, VersionRange.andNewer(VersionEnum.b1_3tob1_3_1));
        register(Items.RED_BED, VersionRange.andNewer(VersionEnum.b1_3tob1_3_1));
        register(Items.REPEATER, VersionRange.andNewer(VersionEnum.b1_3tob1_3_1));
        register(Items.SANDSTONE_SLAB, VersionRange.andNewer(VersionEnum.b1_3tob1_3_1));

        register(Items.BIRCH_LEAVES, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.BIRCH_LOG, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.BLACK_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.BLUE_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.BONE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.BONE_MEAL, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.BROWN_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.CAKE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.CHARCOAL, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.COCOA_BEANS, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.CYAN_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.DISPENSER, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.GRAY_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.GREEN_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.INK_SAC, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.LAPIS_BLOCK, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.LAPIS_LAZULI, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.LAPIS_ORE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.LIGHT_BLUE_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.LIGHT_GRAY_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.LIME_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.MAGENTA_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.NOTE_BLOCK, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.ORANGE_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.PINK_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.PURPLE_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.RED_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.SANDSTONE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.SPRUCE_LEAVES, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.SPRUCE_LOG, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.SUGAR, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.WHITE_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));
        register(Items.YELLOW_DYE, VersionRange.andNewer(VersionEnum.b1_2_0tob1_2_2));

        register(Items.CARVED_PUMPKIN, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.CLOCK, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.COD, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.COOKED_COD, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.GLOWSTONE, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.GLOWSTONE_DUST, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.JACK_O_LANTERN, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.NETHERRACK, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.PUMPKIN, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));
        register(Items.SOUL_SAND, VersionRange.andNewer(VersionEnum.a1_2_0toa1_2_1_1));

        register(Items.COMPASS, VersionRange.andNewer(VersionEnum.a1_1_0toa1_1_2_1));
        register(Items.FISHING_ROD, VersionRange.andNewer(VersionEnum.a1_1_0toa1_1_2_1));

        // Indev with former 20100223 (it's not known)
        register(Items.PAINTING, VersionRange.andNewer(VersionEnum.a1_1_0toa1_1_2_1));

        register(Items.OAK_FENCE, VersionRange.andNewer(VersionEnum.a1_0_17toa1_0_17_4));

        // a1.0.14 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CHEST_MINECART, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.EGG, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.FURNACE_MINECART, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.JUKEBOX, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.MUSIC_DISC_13, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.MUSIC_DISC_CAT, VersionRange.andNewer(VersionEnum.a1_0_15));

        // a1.0.11 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BOOK, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.BRICK, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.CLAY, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.CLAY_BALL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.PAPER, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.SLIME_BALL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.SUGAR_CANE, VersionRange.andNewer(VersionEnum.a1_0_15));

        // a1.0.8 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.LEATHER, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.MILK_BUCKET, VersionRange.andNewer(VersionEnum.a1_0_15));

        // a1.0.6 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CACTUS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.OAK_BOAT, VersionRange.andNewer(VersionEnum.a1_0_15));

        // a1.0.5 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.SNOW_BLOCK, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.SNOWBALL, VersionRange.andNewer(VersionEnum.a1_0_15));

        // a1.0.4 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.ICE, VersionRange.andNewer(VersionEnum.a1_0_15), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.SNOW, VersionRange.andNewer(VersionEnum.a1_0_15), VersionRange.singleton(VersionEnum.c0_30cpe));

        // a1.0.1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.IRON_DOOR, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.LEVER, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.OAK_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.REDSTONE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.REDSTONE_ORE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.REDSTONE_TORCH, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_BUTTON, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_PRESSURE_PLATE, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100629 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.COBBLESTONE_STAIRS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.OAK_STAIRS, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100625-2 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.SADDLE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.SPAWNER, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100624 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.MINECART, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.OAK_DOOR, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.RAIL, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100615 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BUCKET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.LAVA_BUCKET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WATER_BUCKET, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100607 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.LADDER, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.OAK_SIGN, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100227-1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.GOLDEN_APPLE, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100219 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.COOKED_PORKCHOP, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.FLINT, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.FURNACE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.PORKCHOP, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100212-1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CHAINMAIL_BOOTS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.CHAINMAIL_CHESTPLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.CHAINMAIL_HELMET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.CHAINMAIL_LEGGINGS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_BOOTS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_CHESTPLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_HELMET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_LEGGINGS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_BOOTS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_CHESTPLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_HELMET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_LEGGINGS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_BOOTS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_CHESTPLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_HELMET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_LEGGINGS, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 20100206 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BREAD, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_HOE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.FARMLAND, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_HOE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_HOE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_HOE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WHEAT, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WHEAT_SEEDS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WOODEN_HOE, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 0.31 20100130 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BOWL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.CRAFTING_TABLE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.FEATHER, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_AXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_PICKAXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_SHOVEL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLDEN_SWORD, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GUNPOWDER, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.MUSHROOM_STEW, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STRING, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Infdev 0.31 20100129 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.STICK, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Indev 0.31 20100128 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.COAL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_AXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_BLOCK, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_ORE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_PICKAXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_SHOVEL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.DIAMOND_SWORD, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GOLD_INGOT, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_INGOT, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_AXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_PICKAXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_SHOVEL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.STONE_SWORD, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WOODEN_AXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WOODEN_PICKAXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WOODEN_SHOVEL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.WOODEN_SWORD, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Indev 0.31 20091231-2 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.APPLE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_AXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_BOOTS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_CHESTPLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_HELMET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_LEGGINGS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_PICKAXE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_SHOVEL, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.IRON_SWORD, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.LEATHER_BOOTS, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.LEATHER_CHESTPLATE, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.LEATHER_HELMET, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.LEATHER_LEGGINGS, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Indev 0.31 20100124-1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CHEST, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Indev 0.31 20100122 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.ARROW, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.BOW, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Indev 0.31 20100110 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.FLINT_AND_STEEL, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Indev 0.31 20091223-2 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.STONE_SLAB, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.TORCH, VersionRange.andNewer(VersionEnum.a1_0_15));

        // Cave game tech test? I literally have no idea
        register(Items.BEDROCK, VersionRange.andNewer(VersionEnum.a1_0_15));
        register(Items.GRASS_BLOCK, VersionRange.andNewer(VersionEnum.a1_0_15));

        register(Items.OBSIDIAN, VersionRange.andNewer(VersionEnum.c0_28toc0_30));
        register(Items.TNT, VersionRange.andNewer(VersionEnum.c0_28toc0_30));

        // 0.26 SURVIVAL TEST (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BOOKSHELF, VersionRange.andNewer(VersionEnum.c0_28toc0_30));
        register(Items.BRICKS, VersionRange.andNewer(VersionEnum.c0_28toc0_30));
        register(Items.COAL_ORE, VersionRange.andNewer(VersionEnum.c0_28toc0_30));
        register(Items.GOLD_ORE, VersionRange.andNewer(VersionEnum.c0_28toc0_30));
        register(Items.IRON_ORE, VersionRange.andNewer(VersionEnum.c0_28toc0_30));
        register(Items.MOSSY_COBBLESTONE, VersionRange.andNewer(VersionEnum.c0_28toc0_30));

        register(Items.BLACK_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.BLUE_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.BROWN_MUSHROOM, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.BROWN_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.CYAN_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.DANDELION, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.GRAY_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.GREEN_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.LIGHT_BLUE_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.LIGHT_GRAY_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.LIME_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.MAGENTA_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.ORANGE_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.PINK_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27), VersionRange.singleton(VersionEnum.c0_30cpe));
        register(Items.POPPY, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.PURPLE_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.RED_MUSHROOM, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.RED_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.SMOOTH_STONE_SLAB, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.WHITE_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));
        register(Items.YELLOW_WOOL, VersionRange.andNewer(VersionEnum.c0_0_20ac0_27));

        register(Items.SPONGE, VersionRange.andNewer(VersionEnum.c0_0_19a_06));
    }

    private void register(final Item item, final VersionRange range) {
        itemMap.put(item, new VersionRange[]{range});
    }

    private void register(final Item item, final VersionRange... ranges) {
        itemMap.put(item, ranges);
    }

    public Map<Item, VersionRange[]> getItemMap() {
        return itemMap;
    }

    public List<Item> getCurrentMap() {
        return currentMap;
    }
}
