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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.base.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.vialoadingbase.model.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.model.ProtocolRange;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.*;

public class ItemReleaseVersionMappings {
    public static ItemReleaseVersionMappings INSTANCE;

    public static void create() {
        INSTANCE = new ItemReleaseVersionMappings();
        INSTANCE.load();
        INSTANCE.update(ProtocolHack.getTargetVersion());

        ChangeProtocolVersionCallback.EVENT.register(protocolVersion -> INSTANCE.update(protocolVersion));
    }

    private final Map<Item, ProtocolRange[]> itemMap = new HashMap<>();
    private final List<Item> currentMap = new ArrayList<>();

    public void update(final ComparableProtocolVersion protocolVersion) {
        INSTANCE.currentMap.clear();
        INSTANCE.currentMap.addAll(Registries.ITEM.stream().filter(item -> INSTANCE.contains(item, protocolVersion)).toList());
    }

    public boolean contains(final Item item, final ComparableProtocolVersion protocolVersion) {
        if (!itemMap.containsKey(item)) return true;

        return Arrays.stream(itemMap.get(item)).anyMatch(protocolRange -> protocolRange.contains(protocolVersion));
    }

    public void load() {
        register(Items.BRUSH, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_HANGING_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_LEAVES, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_PLANKS, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_SAPLING, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.CHERRY_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.DECORATED_POT, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.PINK_PETALS, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.ARCHER_POTTERY_SHERD, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.ARMS_UP_POTTERY_SHERD, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.PRIZE_POTTERY_SHERD, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.SKULL_POTTERY_SHERD, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.SNIFFER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.STRIPPED_CHERRY_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.STRIPPED_CHERRY_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.SUSPICIOUS_SAND, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.TORCHFLOWER, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.TORCHFLOWER_SEEDS, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));
        register(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19_4));

        register(Items.IRON_GOLEM_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19_3));
        register(Items.SNOW_GOLEM_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19_3));

        register(Items.ACACIA_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.ALLAY_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.BIRCH_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.DARK_OAK_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.DISC_FRAGMENT_5, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.ECHO_SHARD, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.FROG_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.FROGSPAWN, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.GOAT_HORN, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.JUNGLE_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_LEAVES, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_PLANKS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_PROPAGULE, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_ROOTS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MANGROVE_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUD, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUD_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUD_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUD_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUD_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUDDY_MANGROVE_ROOTS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.MUSIC_DISC_5, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.OAK_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.OCHRE_FROGLIGHT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.PACKED_MUD, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.PEARLESCENT_FROGLIGHT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.RECOVERY_COMPASS, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.REINFORCED_DEEPSLATE, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.SCULK, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.SCULK_CATALYST, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.SCULK_SHRIEKER, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.SCULK_VEIN, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.SPRUCE_CHEST_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.STRIPPED_MANGROVE_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.STRIPPED_MANGROVE_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.TADPOLE_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.TADPOLE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.VERDANT_FROGLIGHT, ProtocolRange.andNewer(ProtocolVersion.v1_19));
        register(Items.WARDEN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_19));

        register(Items.MUSIC_DISC_OTHERSIDE, ProtocolRange.andNewer(ProtocolVersion.v1_18));

        register(Items.AMETHYST_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.AMETHYST_CLUSTER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.AMETHYST_SHARD, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.AXOLOTL_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.AXOLOTL_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.AZALEA, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.AZALEA_LEAVES, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.BIG_DRIPLEAF, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.BLACK_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.BLUE_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.BROWN_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.BUDDING_AMETHYST, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.BUNDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CALCITE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CHISELED_DEEPSLATE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COBBLED_DEEPSLATE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COBBLED_DEEPSLATE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COBBLED_DEEPSLATE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COBBLED_DEEPSLATE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COPPER_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COPPER_INGOT, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.COPPER_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CRACKED_DEEPSLATE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CRACKED_DEEPSLATE_TILES, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.CYAN_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_COAL_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_COPPER_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_DIAMOND_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_EMERALD_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_GOLD_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_IRON_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_LAPIS_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_REDSTONE_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_TILE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_TILE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_TILE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DEEPSLATE_TILES, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.DRIPSTONE_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.EXPOSED_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.EXPOSED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.EXPOSED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.EXPOSED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.FLOWERING_AZALEA, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.FLOWERING_AZALEA_LEAVES, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GLOW_BERRIES, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GLOW_INK_SAC, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GLOW_ITEM_FRAME, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GLOW_LICHEN, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GLOW_SQUID_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GOAT_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GRAY_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.GREEN_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.HANGING_ROOTS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.INFESTED_DEEPSLATE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.LARGE_AMETHYST_BUD, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.LIGHT, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.LIGHT_BLUE_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.LIGHT_GRAY_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.LIGHTNING_ROD, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.LIME_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.MAGENTA_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.MEDIUM_AMETHYST_BUD, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.MOSS_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.MOSS_CARPET, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.ORANGE_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.OXIDIZED_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.OXIDIZED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.OXIDIZED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.OXIDIZED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.PINK_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.POINTED_DRIPSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.POLISHED_DEEPSLATE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.POLISHED_DEEPSLATE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.POLISHED_DEEPSLATE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.POLISHED_DEEPSLATE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.POWDER_SNOW_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.PURPLE_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RAW_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RAW_COPPER_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RAW_GOLD, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RAW_GOLD_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RAW_IRON, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RAW_IRON_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.RED_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.ROOTED_DIRT, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.SCULK_SENSOR, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.SMALL_AMETHYST_BUD, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.SMALL_DRIPLEAF, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.SMOOTH_BASALT, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.SPORE_BLOSSOM, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.SPYGLASS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.TINTED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.TUFF, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_COPPER_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_EXPOSED_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_EXPOSED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_EXPOSED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_EXPOSED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_OXIDIZED_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_OXIDIZED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_WEATHERED_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_WEATHERED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_WEATHERED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WAXED_WEATHERED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WEATHERED_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WEATHERED_CUT_COPPER, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WEATHERED_CUT_COPPER_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WEATHERED_CUT_COPPER_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.WHITE_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));
        register(Items.YELLOW_CANDLE, ProtocolRange.andNewer(ProtocolVersion.v1_17));

        register(Items.ANCIENT_DEBRIS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.BASALT, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.BLACKSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.BLACKSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.BLACKSTONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.BLACKSTONE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CHAIN, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CHISELED_NETHER_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CHISELED_POLISHED_BLACKSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRACKED_NETHER_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_FUNGUS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_HYPHAE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_NYLIUM, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_PLANKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_ROOTS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_STEM, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRIMSON_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.CRYING_OBSIDIAN, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.GILDED_BLACKSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.HOGLIN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.LODESTONE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.MUSIC_DISC_PIGSTEP, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHER_GOLD_ORE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHER_SPROUTS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_AXE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_BOOTS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_CHESTPLATE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_HELMET, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_HOE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_INGOT, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_LEGGINGS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_PICKAXE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_SCRAP, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_SHOVEL, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.NETHERITE_SWORD, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.PIGLIN_BANNER_PATTERN, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.PIGLIN_BRUTE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.PIGLIN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BASALT, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.POLISHED_BLACKSTONE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.QUARTZ_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.RESPAWN_ANCHOR, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.SHROOMLIGHT, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.SOUL_CAMPFIRE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.SOUL_LANTERN, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.SOUL_SOIL, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.SOUL_TORCH, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.STRIDER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.STRIPPED_CRIMSON_HYPHAE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.STRIPPED_CRIMSON_STEM, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.STRIPPED_WARPED_HYPHAE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.STRIPPED_WARPED_STEM, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.TARGET, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.TWISTING_VINES, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_FUNGUS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_FUNGUS_ON_A_STICK, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_HYPHAE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_NYLIUM, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_PLANKS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_ROOTS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_STEM, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WARPED_WART_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.WEEPING_VINES, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.ZOGLIN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_16));
        register(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_16));

        register(Items.BEE_NEST, ProtocolRange.andNewer(ProtocolVersion.v1_15));
        register(Items.BEE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_15));
        register(Items.BEEHIVE, ProtocolRange.andNewer(ProtocolVersion.v1_15));
        register(Items.HONEY_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_15));
        register(Items.HONEY_BOTTLE, ProtocolRange.andNewer(ProtocolVersion.v1_15));
        register(Items.HONEYCOMB, ProtocolRange.andNewer(ProtocolVersion.v1_15));
        register(Items.HONEYCOMB_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_15));

        register(Items.ACACIA_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.ANDESITE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.ANDESITE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.ANDESITE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BAMBOO, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BARREL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BELL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BIRCH_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BLACK_DYE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BLAST_FURNACE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BLUE_DYE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.BROWN_DYE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CAMPFIRE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CARTOGRAPHY_TABLE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CAT_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.COMPOSTER, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CORNFLOWER, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CREEPER_BANNER_PATTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CROSSBOW, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CUT_RED_SANDSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.CUT_SANDSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.DARK_OAK_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.DIORITE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.DIORITE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.DIORITE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.END_STONE_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.END_STONE_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.END_STONE_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.FLETCHING_TABLE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.FLOWER_BANNER_PATTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.FOX_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.GLOBE_BANNER_PATTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.GRANITE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.GRANITE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.GRANITE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.GRINDSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.JIGSAW, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.JUNGLE_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.LANTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.LEATHER_HORSE_ARMOR, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.LECTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.LILY_OF_THE_VALLEY, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.LOOM, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.MOJANG_BANNER_PATTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.MOSSY_COBBLESTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.MOSSY_COBBLESTONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.MOSSY_STONE_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.MOSSY_STONE_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.MOSSY_STONE_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.NETHER_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.PANDA_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.PILLAGER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.POLISHED_ANDESITE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.POLISHED_ANDESITE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.POLISHED_DIORITE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.POLISHED_DIORITE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.POLISHED_GRANITE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.POLISHED_GRANITE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.PRISMARINE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.RAVAGER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.RED_NETHER_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.RED_NETHER_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.RED_NETHER_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.RED_SANDSTONE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SANDSTONE_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SCAFFOLDING, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SKULL_BANNER_PATTERN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMITHING_TABLE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOKER, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOOTH_QUARTZ_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOOTH_QUARTZ_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOOTH_RED_SANDSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOOTH_RED_SANDSTONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOOTH_SANDSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SMOOTH_SANDSTONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SPRUCE_SIGN, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.STONE_BRICK_WALL, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.STONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.STONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.STONECUTTER, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SUSPICIOUS_STEW, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.SWEET_BERRIES, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.TRADER_LLAMA_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.WANDERING_TRADER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.WHITE_DYE, ProtocolRange.andNewer(ProtocolVersion.v1_14));
        register(Items.WITHER_ROSE, ProtocolRange.andNewer(ProtocolVersion.v1_14));

        register(Items.ACACIA_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.ACACIA_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.ACACIA_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BIRCH_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BIRCH_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BIRCH_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BLUE_ICE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BRAIN_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BRAIN_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BRAIN_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BUBBLE_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BUBBLE_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.BUBBLE_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.CARVED_PUMPKIN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.CHIPPED_ANVIL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.COD_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.COD_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.CONDUIT, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DAMAGED_ANVIL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DARK_OAK_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DARK_OAK_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DARK_OAK_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DARK_PRISMARINE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DARK_PRISMARINE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_BRAIN_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_BRAIN_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_BRAIN_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_BUBBLE_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_BUBBLE_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_BUBBLE_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_FIRE_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_FIRE_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_FIRE_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_HORN_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_HORN_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_HORN_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_TUBE_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_TUBE_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEAD_TUBE_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DEBUG_STICK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DOLPHIN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DRIED_KELP, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DRIED_KELP_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.DROWNED_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.FIRE_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.FIRE_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.FIRE_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.HEART_OF_THE_SEA, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.HORN_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.HORN_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.HORN_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.JUNGLE_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.JUNGLE_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.JUNGLE_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.KELP, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.MUSHROOM_STEM, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.NAUTILUS_SHELL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.OAK_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        //register(Items.PETRIFIED_OAK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_13)); it's right, but the item exists in another form before, so it's actually wrong
        register(Items.PHANTOM_MEMBRANE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PHANTOM_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PRISMARINE_BRICK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PRISMARINE_BRICK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PRISMARINE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PRISMARINE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PUFFERFISH_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.PUFFERFISH_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SALMON_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SALMON_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SCUTE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SEA_PICKLE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SEAGRASS, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SMOOTH_QUARTZ, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SMOOTH_RED_SANDSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SMOOTH_SANDSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SMOOTH_STONE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SPRUCE_PRESSURE_PLATE, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SPRUCE_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.SPRUCE_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_ACACIA_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_ACACIA_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_BIRCH_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_BIRCH_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_DARK_OAK_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_DARK_OAK_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_JUNGLE_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_JUNGLE_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_OAK_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_OAK_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_SPRUCE_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.STRIPPED_SPRUCE_WOOD, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TRIDENT, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TROPICAL_FISH_BUCKET, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TROPICAL_FISH_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TUBE_CORAL, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TUBE_CORAL_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TUBE_CORAL_FAN, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TURTLE_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TURTLE_HELMET, ProtocolRange.andNewer(ProtocolVersion.v1_13));
        register(Items.TURTLE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_13));

        register(Items.BLACK_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLACK_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLACK_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLACK_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLUE_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLUE_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLUE_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BLUE_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BROWN_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BROWN_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BROWN_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.BROWN_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.CYAN_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.CYAN_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.CYAN_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.CYAN_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GRAY_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GRAY_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GRAY_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GRAY_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GREEN_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GREEN_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GREEN_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.GREEN_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.KNOWLEDGE_BOOK, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_BLUE_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_BLUE_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_BLUE_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_BLUE_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_GRAY_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_GRAY_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_GRAY_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIGHT_GRAY_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIME_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIME_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIME_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.LIME_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.MAGENTA_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.MAGENTA_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.MAGENTA_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.MAGENTA_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.ORANGE_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.ORANGE_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.ORANGE_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.ORANGE_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PARROT_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PINK_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PINK_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PINK_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PINK_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PURPLE_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PURPLE_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PURPLE_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.PURPLE_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.RED_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.RED_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.RED_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.WHITE_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.WHITE_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.WHITE_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.WHITE_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.YELLOW_BED, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.YELLOW_CONCRETE, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.YELLOW_CONCRETE_POWDER, ProtocolRange.andNewer(ProtocolVersion.v1_12));
        register(Items.YELLOW_GLAZED_TERRACOTTA, ProtocolRange.andNewer(ProtocolVersion.v1_12));

        register(Items.BLACK_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.BLUE_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.BROWN_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.CYAN_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.DONKEY_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.EVOKER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.FILLED_MAP, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.GRAY_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.GREEN_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.HUSK_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.IRON_NUGGET, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.LIGHT_BLUE_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.LIGHT_GRAY_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.LIME_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.LLAMA_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.MAGENTA_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.MULE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.OBSERVER, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.ORANGE_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.PINK_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.PURPLE_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.RED_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.SHULKER_SHELL, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.SKELETON_HORSE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.STRAY_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.TOTEM_OF_UNDYING, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.VEX_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.VINDICATOR_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.WHITE_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.WITHER_SKELETON_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.YELLOW_SHULKER_BOX, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.ZOMBIE_HORSE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));
        register(Items.ZOMBIE_VILLAGER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_11));

        register(Items.BONE_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_10));
        register(Items.MAGMA_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_10), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.NETHER_WART_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_10));
        register(Items.POLAR_BEAR_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_10));
        register(Items.RED_NETHER_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_10));
        register(Items.STRUCTURE_VOID, ProtocolRange.andNewer(ProtocolVersion.v1_10));

        register(Items.ACACIA_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.BEETROOT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.BEETROOT_SEEDS, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.BEETROOT_SOUP, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.BIRCH_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.CHAIN_COMMAND_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.CHORUS_FLOWER, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.CHORUS_FRUIT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.CHORUS_PLANT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.DARK_OAK_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.DIRT_PATH, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.DRAGON_BREATH, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.DRAGON_HEAD, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.ELYTRA, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.END_CRYSTAL, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.END_ROD, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.END_STONE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.JUNGLE_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.LINGERING_POTION, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.POPPED_CHORUS_FRUIT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.PURPUR_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.PURPUR_PILLAR, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.PURPUR_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.PURPUR_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.REPEATING_COMMAND_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.SHIELD, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.SHULKER_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.SPECTRAL_ARROW, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.SPRUCE_BOAT, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.STRUCTURE_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_9));
        register(Items.TIPPED_ARROW, ProtocolRange.andNewer(ProtocolVersion.v1_9));

        register(Items.ACACIA_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ACACIA_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ACACIA_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ANDESITE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ARMOR_STAND, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BARRIER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BIRCH_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BIRCH_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BIRCH_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BLACK_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BLUE_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.BROWN_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.CHISELED_RED_SANDSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.COARSE_DIRT, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.COOKED_MUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.COOKED_RABBIT, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.CREEPER_HEAD, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.CUT_RED_SANDSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.CYAN_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.DARK_OAK_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.DARK_OAK_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.DARK_OAK_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.DARK_PRISMARINE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.DIORITE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ELDER_GUARDIAN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ENDERMITE_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.GRANITE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.GRAY_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.GREEN_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.GUARDIAN_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.IRON_TRAPDOOR, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.JUNGLE_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.JUNGLE_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.JUNGLE_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.LIGHT_BLUE_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.LIGHT_GRAY_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.LIME_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.MAGENTA_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.MUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ORANGE_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PINK_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PLAYER_HEAD, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.POLISHED_ANDESITE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.POLISHED_DIORITE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.POLISHED_GRANITE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PRISMARINE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PRISMARINE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PRISMARINE_CRYSTALS, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PRISMARINE_SHARD, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.PURPLE_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RABBIT, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RABBIT_FOOT, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RABBIT_HIDE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RABBIT_SPAWN_EGG, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RABBIT_STEW, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RED_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RED_SANDSTONE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RED_SANDSTONE_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.RED_SANDSTONE_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SEA_LANTERN, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SKELETON_SKULL, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SLIME_BLOCK, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SPONGE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SPRUCE_DOOR, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SPRUCE_FENCE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.SPRUCE_FENCE_GATE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.WET_SPONGE, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.WHITE_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.WITHER_SKELETON_SKULL, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.YELLOW_BANNER, ProtocolRange.andNewer(ProtocolVersion.v1_8));
        register(Items.ZOMBIE_HEAD, ProtocolRange.andNewer(ProtocolVersion.v1_8));

        register(Items.ACACIA_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ACACIA_LEAVES, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ACACIA_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ACACIA_PLANKS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ACACIA_SAPLING, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ACACIA_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ACACIA_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ALLIUM, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.AZURE_BLUET, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BIRCH_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BLACK_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BLACK_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BLUE_ORCHID, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BLUE_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BLUE_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BROWN_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.BROWN_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.COMMAND_BLOCK_MINECART, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.COOKED_SALMON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.CYAN_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.CYAN_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_LEAVES, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_LOG, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_PLANKS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_SAPLING, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_SLAB, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.DARK_OAK_STAIRS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.GRAY_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.GRAY_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.GREEN_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.GREEN_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.INFESTED_CHISELED_STONE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.INFESTED_CRACKED_STONE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.INFESTED_MOSSY_STONE_BRICKS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.JUNGLE_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LARGE_FERN, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LIGHT_BLUE_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LIGHT_BLUE_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LIGHT_GRAY_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LIGHT_GRAY_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LILAC, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LIME_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.LIME_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.MAGENTA_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.MAGENTA_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ORANGE_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ORANGE_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ORANGE_TULIP, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.OXEYE_DAISY, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PACKED_ICE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PEONY, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PINK_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PINK_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PINK_TULIP, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PODZOL, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PUFFERFISH, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PURPLE_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.PURPLE_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.RED_SAND, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.RED_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.RED_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.RED_TULIP, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.ROSE_BUSH, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.SALMON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.SPRUCE_BUTTON, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.SUNFLOWER, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.TALL_GRASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.TROPICAL_FISH, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.WHITE_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.WHITE_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.WHITE_TULIP, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.YELLOW_STAINED_GLASS, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));
        register(Items.YELLOW_STAINED_GLASS_PANE, ProtocolRange.andNewer(ProtocolVersion.v1_7_1));

        register(Items.BLACK_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.BLACK_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.BLUE_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.BLUE_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.BROWN_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.BROWN_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.COAL_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.CYAN_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.CYAN_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.DIAMOND_HORSE_ARMOR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.GOLDEN_HORSE_ARMOR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.GRAY_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.GRAY_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.GREEN_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.GREEN_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.HAY_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.HORSE_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.IRON_HORSE_ARMOR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LEAD, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LIGHT_BLUE_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LIGHT_BLUE_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LIGHT_GRAY_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LIGHT_GRAY_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LIME_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.LIME_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.MAGENTA_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.MAGENTA_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.NAME_TAG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.ORANGE_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.ORANGE_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.PINK_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.PINK_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.PURPLE_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.PURPLE_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.RED_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.RED_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.WHITE_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.WHITE_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.YELLOW_CARPET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));
        register(Items.YELLOW_TERRACOTTA, ProtocolRange.andNewer(LegacyProtocolVersion.r1_6_1));

        register(Items.ACTIVATOR_RAIL, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.CHISELED_QUARTZ_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.COMPARATOR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.DAYLIGHT_DETECTOR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.DROPPER, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.HOPPER, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.HOPPER_MINECART, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.LIGHT_WEIGHTED_PRESSURE_PLATE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.NETHER_BRICK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.NETHER_QUARTZ_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.QUARTZ, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.QUARTZ_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.QUARTZ_PILLAR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.QUARTZ_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.QUARTZ_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.REDSTONE_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.TNT_MINECART, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));
        register(Items.TRAPPED_CHEST, ProtocolRange.andNewer(LegacyProtocolVersion.r1_5tor1_5_1));

        register(Items.ANVIL, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.BAKED_POTATO, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.BAT_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.BEACON, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.CARROT, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.CARROT_ON_A_STICK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.COBBLESTONE_WALL, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.COMMAND_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.ENCHANTED_BOOK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.FIREWORK_ROCKET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.FIREWORK_STAR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.FLOWER_POT, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.GOLDEN_CARROT, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.ITEM_FRAME, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.MAP, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.MOSSY_COBBLESTONE_WALL, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        // "[The disc was] made available in survival" Is this the release in which it was added or made available?
        register(Items.MUSIC_DISC_WAIT, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.NETHER_BRICK_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.NETHER_STAR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.OAK_BUTTON, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.POISONOUS_POTATO, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.POTATO, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.PUMPKIN_PIE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));
        register(Items.WITCH_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_4_2));

        register(Items.BIRCH_PLANKS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.BIRCH_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.BIRCH_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.COCOA_BEANS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.EMERALD, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.EMERALD_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.EMERALD_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.ENCHANTED_GOLDEN_APPLE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.ENDER_CHEST, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.JUNGLE_PLANKS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.JUNGLE_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.JUNGLE_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.SANDSTONE_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.SPRUCE_PLANKS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.SPRUCE_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.SPRUCE_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.TRIPWIRE_HOOK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.WRITABLE_BOOK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));
        register(Items.WRITTEN_BOOK, ProtocolRange.andNewer(LegacyProtocolVersion.r1_3_1tor1_3_2));

        register(Items.CHISELED_SANDSTONE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.CHISELED_STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.CUT_SANDSTONE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.EXPERIENCE_BOTTLE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.FIRE_CHARGE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.JUNGLE_LEAVES, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.JUNGLE_LOG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.JUNGLE_SAPLING, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.OCELOT_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));
        register(Items.REDSTONE_LAMP, ProtocolRange.andNewer(LegacyProtocolVersion.r1_2_1tor1_2_3));

        register(Items.BLAZE_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.CAVE_SPIDER_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.CHICKEN_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.COW_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.CREEPER_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.ENDERMAN_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.GHAST_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.MAGMA_CUBE_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.MOOSHROOM_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.PIG_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.SHEEP_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.SILVERFISH_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.SKELETON_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.SLIME_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.SPIDER_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.SQUID_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.VILLAGER_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.WOLF_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.ZOMBIE_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));
        register(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_1));

        register(Items.BLAZE_POWDER, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.BLAZE_ROD, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.BREWING_STAND, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.CAULDRON, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.DRAGON_EGG, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.ENCHANTING_TABLE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.END_PORTAL_FRAME, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.END_STONE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.ENDER_EYE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.FERMENTED_SPIDER_EYE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.GHAST_TEAR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.GLASS_BOTTLE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.GLISTERING_MELON_SLICE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.GOLD_NUGGET, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MAGMA_CREAM, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_11, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_BLOCKS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_CHIRP, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_FAR, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_MALL, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_MELLOHI, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_STAL, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_STRAD, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MUSIC_DISC_WARD, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.NETHER_BRICK_FENCE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.NETHER_BRICK_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.NETHER_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.NETHER_WART, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.POTION, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.SPIDER_EYE, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.SPLASH_POTION, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));

        // b1.9-pre1
        register(Items.LILY_PAD, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));
        register(Items.MYCELIUM, ProtocolRange.andNewer(LegacyProtocolVersion.r1_0_0tor1_0_1));

        register(Items.BEEF, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.BRICK_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.BRICK_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.BROWN_MUSHROOM_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.CHICKEN, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.COOKED_BEEF, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.COOKED_CHICKEN, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.CRACKED_STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.ENDER_PEARL, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.GLASS_PANE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.INFESTED_COBBLESTONE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.INFESTED_CRACKED_STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.INFESTED_MOSSY_STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.INFESTED_STONE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.INFESTED_STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.IRON_BARS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.MELON, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.MELON_SEEDS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.MELON_SLICE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.MOSSY_STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.MUSHROOM_STEM, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.OAK_FENCE_GATE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.PUMPKIN_SEEDS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.RED_MUSHROOM_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.ROTTEN_FLESH, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.STONE_BRICK_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.STONE_BRICK_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));
        register(Items.STONE_BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.VINE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_8tob1_8_1));

        register(Items.PISTON, ProtocolRange.andNewer(LegacyProtocolVersion.b1_7tob1_7_3));
        register(Items.SHEARS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_7tob1_7_3));
        register(Items.STICKY_PISTON, ProtocolRange.andNewer(LegacyProtocolVersion.b1_7tob1_7_3));

        register(Items.DEAD_BUSH, ProtocolRange.andNewer(LegacyProtocolVersion.b1_6tob1_6_6));
        register(Items.FERN, ProtocolRange.andNewer(LegacyProtocolVersion.b1_6tob1_6_6));
        register(Items.GRASS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_6tob1_6_6));
        register(Items.MAP, ProtocolRange.andNewer(LegacyProtocolVersion.b1_6tob1_6_6));
        register(Items.OAK_TRAPDOOR, ProtocolRange.andNewer(LegacyProtocolVersion.b1_6tob1_6_6));

        register(Items.BIRCH_SAPLING, ProtocolRange.andNewer(LegacyProtocolVersion.b1_5tob1_5_2));
        register(Items.COBWEB, ProtocolRange.andNewer(LegacyProtocolVersion.b1_5tob1_5_2));
        register(Items.DETECTOR_RAIL, ProtocolRange.andNewer(LegacyProtocolVersion.b1_5tob1_5_2));
        register(Items.POWERED_RAIL, ProtocolRange.andNewer(LegacyProtocolVersion.b1_5tob1_5_2));
        register(Items.SPRUCE_SAPLING, ProtocolRange.andNewer(LegacyProtocolVersion.b1_5tob1_5_2));

        register(Items.COOKIE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_4tob1_4_1));
        register(Items.TRAPPED_CHEST, ProtocolRange.andNewer(LegacyProtocolVersion.b1_4tob1_4_1));

        register(Items.COBBLESTONE_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.b1_3tob1_3_1), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.OAK_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.b1_3tob1_3_1));
        register(Items.RED_BED, ProtocolRange.andNewer(LegacyProtocolVersion.b1_3tob1_3_1));
        register(Items.REPEATER, ProtocolRange.andNewer(LegacyProtocolVersion.b1_3tob1_3_1));
        register(Items.SANDSTONE_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.b1_3tob1_3_1));

        register(Items.BIRCH_LEAVES, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.BIRCH_LOG, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.BLACK_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.BLUE_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.BONE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.BONE_MEAL, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.BROWN_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.CAKE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.CHARCOAL, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.COCOA_BEANS, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.CYAN_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.DISPENSER, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.GRAY_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.GREEN_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.INK_SAC, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.LAPIS_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.LAPIS_LAZULI, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.LAPIS_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.LIGHT_BLUE_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.LIGHT_GRAY_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.LIME_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.MAGENTA_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.NOTE_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.ORANGE_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.PINK_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.PURPLE_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.RED_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.SANDSTONE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.SPRUCE_LEAVES, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.SPRUCE_LOG, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.SUGAR, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.WHITE_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));
        register(Items.YELLOW_DYE, ProtocolRange.andNewer(LegacyProtocolVersion.b1_2_0tob1_2_2));

        register(Items.CARVED_PUMPKIN, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.CLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.COD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.COOKED_COD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.GLOWSTONE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.GLOWSTONE_DUST, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.JACK_O_LANTERN, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.NETHERRACK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.PUMPKIN, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));
        register(Items.SOUL_SAND, ProtocolRange.andNewer(LegacyProtocolVersion.a1_2_0toa1_2_1_1));

        register(Items.COMPASS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_1_0toa1_1_2_1));
        register(Items.FISHING_ROD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_1_0toa1_1_2_1));

        // Indev with former 20100223 (it's not known)
        register(Items.PAINTING, ProtocolRange.andNewer(LegacyProtocolVersion.a1_1_0toa1_1_2_1));

        register(Items.OAK_FENCE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_17toa1_0_17_4));

        // a1.0.14 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CHEST_MINECART, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.EGG, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.FURNACE_MINECART, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.JUKEBOX, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.MUSIC_DISC_13, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.MUSIC_DISC_CAT, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // a1.0.11 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BOOK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.BRICK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.CLAY, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.CLAY_BALL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.PAPER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.SLIME_BALL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.SUGAR_CANE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // a1.0.8 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.LEATHER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.MILK_BUCKET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // a1.0.6 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CACTUS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.OAK_BOAT, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // a1.0.5 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.SNOW_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.SNOWBALL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // a1.0.4 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.ICE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.SNOW, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));

        // a1.0.1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.IRON_DOOR, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.LEVER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.OAK_PRESSURE_PLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.REDSTONE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.REDSTONE_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.REDSTONE_TORCH, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_BUTTON, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_PRESSURE_PLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100629 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.COBBLESTONE_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.OAK_STAIRS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100625-2 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.SADDLE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.SPAWNER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100624 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.MINECART, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.OAK_DOOR, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.RAIL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100615 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BUCKET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.LAVA_BUCKET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WATER_BUCKET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100607 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.LADDER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.OAK_SIGN, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100227-1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.GOLDEN_APPLE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100219 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.COOKED_PORKCHOP, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.FLINT, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.FURNACE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.PORKCHOP, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100212-1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CHAINMAIL_BOOTS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.CHAINMAIL_CHESTPLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.CHAINMAIL_HELMET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.CHAINMAIL_LEGGINGS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_BOOTS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_CHESTPLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_HELMET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_LEGGINGS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_BOOTS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_CHESTPLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_HELMET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_LEGGINGS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_BOOTS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_CHESTPLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_HELMET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_LEGGINGS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 20100206 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BREAD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_HOE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.FARMLAND, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_HOE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_HOE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_HOE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WHEAT, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WHEAT_SEEDS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WOODEN_HOE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 0.31 20100130 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BOWL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.CRAFTING_TABLE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.FEATHER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_AXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_PICKAXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_SHOVEL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLDEN_SWORD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GUNPOWDER, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.MUSHROOM_STEW, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STRING, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Infdev 0.31 20100129 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.STICK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Indev 0.31 20100128 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.COAL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_AXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_PICKAXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_SHOVEL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.DIAMOND_SWORD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GOLD_INGOT, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_INGOT, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_AXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_PICKAXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_SHOVEL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.STONE_SWORD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WOODEN_AXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WOODEN_PICKAXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WOODEN_SHOVEL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.WOODEN_SWORD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Indev 0.31 20091231-2 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.APPLE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_AXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_BOOTS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_CHESTPLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_HELMET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_LEGGINGS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_PICKAXE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_SHOVEL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.IRON_SWORD, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.LEATHER_BOOTS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.LEATHER_CHESTPLATE, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.LEATHER_HELMET, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.LEATHER_LEGGINGS, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Indev 0.31 20100124-1 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.CHEST, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Indev 0.31 20100122 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.ARROW, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.BOW, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Indev 0.31 20100110 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.FLINT_AND_STEEL, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Indev 0.31 20091223-2 (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.STONE_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.TORCH, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        // Cave game tech test? I literally have no idea
        register(Items.BEDROCK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));
        register(Items.GRASS_BLOCK, ProtocolRange.andNewer(LegacyProtocolVersion.a1_0_15));

        register(Items.OBSIDIAN, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));
        register(Items.TNT, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));

        // 0.26 SURVIVAL TEST (doesn't have multiplayer, so we assign it to the next multiplayer version)
        register(Items.BOOKSHELF, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));
        register(Items.BRICKS, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));
        register(Items.COAL_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));
        register(Items.GOLD_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));
        register(Items.IRON_ORE, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));
        register(Items.MOSSY_COBBLESTONE, ProtocolRange.andNewer(LegacyProtocolVersion.c0_28toc0_30));


        register(Items.BLACK_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.BLUE_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.BROWN_MUSHROOM, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.BROWN_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.CYAN_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.DANDELION, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.GRAY_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.GREEN_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.LIGHT_BLUE_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.LIGHT_GRAY_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.LIME_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.MAGENTA_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.ORANGE_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.PINK_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27), ProtocolRange.singleton(LegacyProtocolVersion.c0_30cpe));
        register(Items.POPPY, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.PURPLE_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.RED_MUSHROOM, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.RED_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.SMOOTH_STONE_SLAB, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.WHITE_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));
        register(Items.YELLOW_WOOL, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_20ac0_27));

        register(Items.SPONGE, ProtocolRange.andNewer(LegacyProtocolVersion.c0_0_19a_06));
    }

    private void register(final Item item, final ProtocolRange range) {
        itemMap.put(item, new ProtocolRange[]{range});
    }

    private void register(final Item item, final ProtocolRange... ranges) {
        itemMap.put(item, ranges);
    }

    public Map<Item, ProtocolRange[]> getItemMap() {
        return itemMap;
    }

    public List<Item> getCurrentMap() {
        return currentMap;
    }
}
