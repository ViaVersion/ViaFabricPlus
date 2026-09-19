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

package com.viaversion.viafabricplus.features.v1_11;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import java.util.ArrayList;

public final class FurnaceFuels {

    private static FurnaceFuels FUELS_1_11 = null;
    private static FurnaceFuels FUELS_1_3_1 = null;
    private static FurnaceFuels FUELS_1_2_5 = null;

    private final ArrayList<Item> values;

    private FurnaceFuels(final ArrayList<Item> values) {
        this.values = values;
    }

    public boolean isFuel(final ItemStack itemStack) {
        return this.values.contains(itemStack.getItem());
    }

    public static FurnaceFuels getFuels_1_11() {
        if (FUELS_1_11 == null)
            FUELS_1_11 = new FurnaceFuels.Builder()
                .add(Items.LAVA_BUCKET)
                .add(Blocks.COAL_BLOCK)
                .add(Items.BLAZE_ROD)
                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .add(ItemTags.PLANKS)
                .add(ItemTags.WOODEN_STAIRS)
                .add(ItemTags.WOODEN_TRAPDOORS)
                .add(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ItemTags.WOODEN_FENCES)
                .add(ItemTags.FENCE_GATES)
                .add(Blocks.NOTE_BLOCK)
                .add(Blocks.BOOKSHELF)
                .add(Blocks.JUKEBOX)
                .add(Blocks.CHEST)
                .add(Blocks.TRAPPED_CHEST)
                .add(Blocks.CRAFTING_TABLE)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(ItemTags.BANNERS)
                .add(Items.BOW)
                .add(Items.FISHING_ROD)
                .add(Blocks.LADDER)
                .add(ItemTags.SIGNS)
                .add(Items.WOODEN_SHOVEL)
                .add(Items.WOODEN_SWORD)
                .add(Items.WOODEN_SPEAR)
                .add(Items.WOODEN_HOE)
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_PICKAXE)
                .add(ItemTags.WOODEN_DOORS)
                .add(ItemTags.BOATS)
                .add(ItemTags.WOOL)
                .add(ItemTags.WOODEN_BUTTONS)
                .add(Items.STICK)
                .add(ItemTags.SAPLINGS)
                .add(Items.BOWL)
                .add(ItemTags.WOOL_CARPETS)
                .remove(ItemTags.NON_FLAMMABLE_WOOD)
                .build();

        return FUELS_1_11;
    }

    public static FurnaceFuels getFuels_1_3_1() {
        if (FUELS_1_3_1 == null)
            FUELS_1_3_1 = new FurnaceFuels.Builder()
                .add(Items.LAVA_BUCKET)
                .add(Blocks.COAL_BLOCK)
                .add(Items.BLAZE_ROD)
                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .add(ItemTags.PLANKS)
                .add(ItemTags.WOODEN_STAIRS)
                .add(ItemTags.WOODEN_TRAPDOORS)
                .add(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ItemTags.WOODEN_FENCES)
                .add(ItemTags.FENCE_GATES)
                .add(Blocks.NOTE_BLOCK)
                .add(Blocks.BOOKSHELF)
                .add(Blocks.JUKEBOX)
                .add(Blocks.CHEST)
                .add(Blocks.TRAPPED_CHEST)
                .add(Blocks.CRAFTING_TABLE)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(ItemTags.BANNERS)
                .add(Items.BOW)
                .add(Items.FISHING_ROD)
                .add(Blocks.LADDER)
                .add(Items.WOODEN_SHOVEL)
                .add(Items.WOODEN_SWORD)
                .add(Items.WOODEN_SPEAR)
                .add(Items.WOODEN_HOE)
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_PICKAXE)
                .add(Items.STICK)
                .add(ItemTags.SAPLINGS)
                .add(Items.BOWL)
                .remove(ItemTags.NON_FLAMMABLE_WOOD)
                .build();

        return FUELS_1_3_1;
    }

    public static FurnaceFuels getFuels_1_2_5() {
        if (FUELS_1_2_5 == null)
            FUELS_1_2_5 = new FurnaceFuels.Builder()
                .add(Items.LAVA_BUCKET)
                .add(Blocks.COAL_BLOCK)
                .add(Items.BLAZE_ROD)
                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .add(ItemTags.PLANKS)
                .add(ItemTags.WOODEN_STAIRS)
                .add(ItemTags.WOODEN_TRAPDOORS)
                .add(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ItemTags.WOODEN_FENCES)
                .add(ItemTags.FENCE_GATES)
                .add(Blocks.NOTE_BLOCK)
                .add(Blocks.BOOKSHELF)
                .add(Blocks.JUKEBOX)
                .add(Blocks.CHEST)
                .add(Blocks.TRAPPED_CHEST)
                .add(Blocks.CRAFTING_TABLE)
                .add(Blocks.DAYLIGHT_DETECTOR)
                .add(ItemTags.BANNERS)
                .add(Items.BOW)
                .add(Items.FISHING_ROD)
                .add(Blocks.LADDER)
                .add(Items.STICK)
                .add(ItemTags.SAPLINGS)
                .add(Items.BOWL)
                .remove(ItemTags.NON_FLAMMABLE_WOOD)
                .build();

        return FUELS_1_2_5;
    }

    public static class Builder {

        private final ArrayList<Item> values = new ArrayList<>();

        public FurnaceFuels build() {
            return new FurnaceFuels(this.values);
        }

        public FurnaceFuels.Builder remove(final TagKey<Item> tag) {
            BuiltInRegistries.ITEM.get(tag).ifPresent(items -> {
                for (Holder<Item> item : items) {
                    this.values.remove(item.value());
                }
            });
            return this;
        }

        public FurnaceFuels.Builder add(final TagKey<Item> tag) {
            BuiltInRegistries.ITEM.get(tag).ifPresent(items -> {
                for (Holder<Item> item : items) {
                    this.values.add(item.value());
                }
            });
            return this;
        }

        public FurnaceFuels.Builder add(final ItemLike itemLike) {
            Item item = itemLike.asItem();
            this.values.add(item);
            return this;
        }

    }

}
