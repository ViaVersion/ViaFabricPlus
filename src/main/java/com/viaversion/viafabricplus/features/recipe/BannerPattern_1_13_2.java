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

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;

public enum BannerPattern_1_13_2 {

    BASE(BannerPatterns.BASE),
    SQUARE_BOTTOM_LEFT(BannerPatterns.SQUARE_BOTTOM_LEFT, "   ", "   ", "#  "),
    SQUARE_BOTTOM_RIGHT(BannerPatterns.SQUARE_BOTTOM_RIGHT, "   ", "   ", "  #"),
    SQUARE_TOP_LEFT(BannerPatterns.SQUARE_TOP_LEFT, "#  ", "   ", "   "),
    SQUARE_TOP_RIGHT(BannerPatterns.SQUARE_TOP_RIGHT, "  #", "   ", "   "),
    STRIPE_BOTTOM(BannerPatterns.STRIPE_BOTTOM, "   ", "   ", "###"),
    STRIPE_TOP(BannerPatterns.STRIPE_TOP, "###", "   ", "   "),
    STRIPE_LEFT(BannerPatterns.STRIPE_LEFT, "#  ", "#  ", "#  "),
    STRIPE_RIGHT(BannerPatterns.STRIPE_RIGHT, "  #", "  #", "  #"),
    STRIPE_CENTER(BannerPatterns.STRIPE_CENTER, " # ", " # ", " # "),
    STRIPE_MIDDLE(BannerPatterns.STRIPE_MIDDLE, "   ", "###", "   "),
    STRIPE_DOWNRIGHT(BannerPatterns.STRIPE_DOWNRIGHT, "#  ", " # ", "  #"),
    STRIPE_DOWNLEFT(BannerPatterns.STRIPE_DOWNLEFT, "  #", " # ", "#  "),
    STRIPE_SMALL(BannerPatterns.STRIPE_SMALL, "# #", "# #", "   "),
    CROSS(BannerPatterns.CROSS, "# #", " # ", "# #"),
    STRAIGHT_CROSS(BannerPatterns.STRAIGHT_CROSS, " # ", "###", " # "),
    TRIANGLE_BOTTOM(BannerPatterns.TRIANGLE_BOTTOM, "   ", " # ", "# #"),
    TRIANGLE_TOP(BannerPatterns.TRIANGLE_TOP, "# #", " # ", "   "),
    TRIANGLES_BOTTOM(BannerPatterns.TRIANGLES_BOTTOM, "   ", "# #", " # "),
    TRIANGLES_TOP(BannerPatterns.TRIANGLES_TOP, " # ", "# #", "   "),
    DIAGONAL_LEFT(BannerPatterns.DIAGONAL_LEFT, "## ", "#  ", "   "),
    DIAGONAL_RIGHT(BannerPatterns.DIAGONAL_RIGHT_MIRROR, "   ", "  #", " ##"),
    DIAGONAL_LEFT_MIRROR(BannerPatterns.DIAGONAL_LEFT_MIRROR, "   ", "#  ", "## "),
    DIAGONAL_RIGHT_MIRROR(BannerPatterns.DIAGONAL_RIGHT, " ##", "  #", "   "),
    CIRCLE_MIDDLE(BannerPatterns.CIRCLE_MIDDLE, "   ", " # ", "   "),
    RHOMBUS_MIDDLE(BannerPatterns.RHOMBUS_MIDDLE, " # ", "# #", " # "),
    HALF_VERTICAL(BannerPatterns.HALF_VERTICAL, "## ", "## ", "## "),
    HALF_HORIZONTAL(BannerPatterns.HALF_HORIZONTAL, "###", "###", "   "),
    HALF_VERTICAL_MIRROR(BannerPatterns.HALF_VERTICAL_MIRROR, " ##", " ##", " ##"),
    HALF_HORIZONTAL_MIRROR(BannerPatterns.HALF_HORIZONTAL_MIRROR, "   ", "###", "###"),
    BORDER(BannerPatterns.BORDER, "###", "# #", "###"),
    CURLY_BORDER(BannerPatterns.CURLY_BORDER, new ItemStack(Blocks.VINE)),
    GRADIENT(BannerPatterns.GRADIENT, "# #", " # ", " # "),
    GRADIENT_UP(BannerPatterns.GRADIENT_UP, " # ", " # ", "# #"),
    BRICKS(BannerPatterns.BRICKS, new ItemStack(Blocks.BRICKS)),
    GLOBE(BannerPatterns.GLOBE),
    CREEPER(BannerPatterns.CREEPER, new ItemStack(Items.CREEPER_HEAD)),
    SKULL(BannerPatterns.SKULL, new ItemStack(Items.WITHER_SKELETON_SKULL)),
    FLOWER(BannerPatterns.FLOWER, new ItemStack(Blocks.OXEYE_DAISY)),
    MOJANG(BannerPatterns.MOJANG, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

    private final ResourceKey<BannerPattern> pattern;
    private final String[] recipePattern;
    private ItemStack baseStack;

    BannerPattern_1_13_2(final ResourceKey<BannerPattern> pattern) {
        this.recipePattern = new String[3];
        this.baseStack = ItemStack.EMPTY;
        this.pattern = pattern;
    }

    BannerPattern_1_13_2(final ResourceKey<BannerPattern> pattern, final ItemStack baseStack) {
        this(pattern);
        this.baseStack = baseStack;
    }

    BannerPattern_1_13_2(final ResourceKey<BannerPattern> pattern, final String recipe1, final String recipe2, final String recipe3) {
        this(pattern);
        this.recipePattern[0] = recipe1;
        this.recipePattern[1] = recipe2;
        this.recipePattern[2] = recipe3;
    }

    public ResourceKey<BannerPattern> getKey() {
        return this.pattern;
    }

    public boolean isCraftable() {
        return !this.baseStack.isEmpty() || this.recipePattern[0] != null;
    }

    public boolean hasBaseStack() {
        return !this.baseStack.isEmpty();
    }

    public ItemStack getBaseStack() {
        return this.baseStack;
    }

    public String[] getRecipePattern() {
        return this.recipePattern;
    }

}
