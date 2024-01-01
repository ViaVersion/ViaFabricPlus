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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

// safe from vanilla modification + removal of recipes
public enum BannerPattern_1_13_2 {

    BASE("b"),
    SQUARE_BOTTOM_LEFT("bl", "   ", "   ", "#  "),
    SQUARE_BOTTOM_RIGHT("br", "   ", "   ", "  #"),
    SQUARE_TOP_LEFT("tl", "#  ", "   ", "   "),
    SQUARE_TOP_RIGHT("tr", "  #", "   ", "   "),
    STRIPE_BOTTOM("bs", "   ", "   ", "###"),
    STRIPE_TOP("ts", "###", "   ", "   "),
    STRIPE_LEFT("ls", "#  ", "#  ", "#  "),
    STRIPE_RIGHT("rs", "  #", "  #", "  #"),
    STRIPE_CENTER("cs", " # ", " # ", " # "),
    STRIPE_MIDDLE("ms", "   ", "###", "   "),
    STRIPE_DOWNRIGHT("drs", "#  ", " # ", "  #"),
    STRIPE_DOWNLEFT("dls", "  #", " # ", "#  "),
    STRIPE_SMALL("ss", "# #", "# #", "   "),
    CROSS("cr", "# #", " # ", "# #"),
    STRAIGHT_CROSS("sc", " # ", "###", " # "),
    TRIANGLE_BOTTOM("bt", "   ", " # ", "# #"),
    TRIANGLE_TOP("tt", "# #", " # ", "   "),
    TRIANGLES_BOTTOM("bts", "   ", "# #", " # "),
    TRIANGLES_TOP("tts", " # ", "# #", "   "),
    DIAGONAL_LEFT("ld", "## ", "#  ", "   "),
    DIAGONAL_RIGHT("rd", "   ", "  #", " ##"),
    DIAGONAL_LEFT_MIRROR("lud", "   ", "#  ", "## "),
    DIAGONAL_RIGHT_MIRROR("rud", " ##", "  #", "   "),
    CIRCLE_MIDDLE("mc", "   ", " # ", "   "),
    RHOMBUS_MIDDLE("mr", " # ", "# #", " # "),
    HALF_VERTICAL("vh", "## ", "## ", "## "),
    HALF_HORIZONTAL("hh", "###", "###", "   "),
    HALF_VERTICAL_MIRROR("vhr", " ##", " ##", " ##"),
    HALF_HORIZONTAL_MIRROR("hhb", "   ", "###", "###"),
    BORDER("bo", "###", "# #", "###"),
    CURLY_BORDER("cbo", new ItemStack(Blocks.VINE)),
    GRADIENT("gra", "# #", " # ", " # "),
    GRADIENT_UP("gru", " # ", " # ", "# #"),
    BRICKS("bri", new ItemStack(Blocks.BRICKS)),
    GLOBE("glb"),
    CREEPER("cre", new ItemStack(Items.CREEPER_HEAD)),
    SKULL("sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
    FLOWER("flo", new ItemStack(Blocks.OXEYE_DAISY)),
    MOJANG("moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

    private final String id;
    private final String[] recipePattern;
    private ItemStack baseStack;

    BannerPattern_1_13_2(String id) {
        this.recipePattern = new String[3];
        this.baseStack = ItemStack.EMPTY;
        this.id = id;
    }

    BannerPattern_1_13_2(String id, ItemStack baseStack) {
        this(id);
        this.baseStack = baseStack;
    }

    BannerPattern_1_13_2(String id, String recipe1, String recipe2, String recipe3) {
        this(id);
        this.recipePattern[0] = recipe1;
        this.recipePattern[1] = recipe2;
        this.recipePattern[2] = recipe3;
    }

    public String getId() {
        return this.id;
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
