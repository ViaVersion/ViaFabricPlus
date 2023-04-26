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
package de.florianmichael.viafabricplus.definition.v1_19_4;

import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;

public enum Material1_19_4 {

    AIR(MapColor.CLEAR, PistonBehavior.NORMAL, false, false, false, false, true, false),
    STRUCTURE_VOID(MapColor.CLEAR, PistonBehavior.NORMAL, false, false, false, false, true, false),
    PORTAL(MapColor.CLEAR, PistonBehavior.BLOCK, false, false, false, false, false, false),
    CARPET(MapColor.WHITE_GRAY, PistonBehavior.NORMAL, false, true, false, false, false, false),
    PLANT(MapColor.DARK_GREEN, PistonBehavior.DESTROY, false, false, false, false, false, false),
    UNDERWATER_PLANT(MapColor.WATER_BLUE, PistonBehavior.DESTROY, false, false, false, false, false, false),
    REPLACEABLE_PLANT(MapColor.DARK_GREEN, PistonBehavior.DESTROY, false, true, false, false, true, false),
    NETHER_SHOOTS(MapColor.DARK_GREEN, PistonBehavior.DESTROY, false, false, false, false, true, false),
    REPLACEABLE_UNDERWATER_PLANT(MapColor.WATER_BLUE, PistonBehavior.DESTROY, false, false, false, false, true, false),
    WATER(MapColor.WATER_BLUE, PistonBehavior.DESTROY, false, false, true, false, true, false),
    BUBBLE_COLUMN(MapColor.WATER_BLUE, PistonBehavior.DESTROY, false, false, true, false, true, false),
    LAVA(MapColor.BRIGHT_RED, PistonBehavior.DESTROY, false, false, true, false, true, false),
    SNOW_LAYER(MapColor.WHITE, PistonBehavior.DESTROY, false, false, false, false, true, false),
    FIRE(MapColor.CLEAR, PistonBehavior.DESTROY, false, false, false, false, true, false),
    DECORATION(MapColor.CLEAR, PistonBehavior.DESTROY, false, false, false, false, false, false),
    COBWEB(MapColor.WHITE_GRAY, PistonBehavior.DESTROY, false, false, false, false, false, true),
    SCULK(MapColor.BLACK, PistonBehavior.NORMAL, true, false, false, true, false, true),
    REDSTONE_LAMP(MapColor.CLEAR, PistonBehavior.NORMAL, true, false, false, true, false, true),
    ORGANIC_PRODUCT(MapColor.LIGHT_BLUE_GRAY, PistonBehavior.NORMAL, true, false, false, true, false, true),
    SOIL(MapColor.DIRT_BROWN, PistonBehavior.NORMAL, true, false, false, true, false, true),
    SOLID_ORGANIC(MapColor.PALE_GREEN, PistonBehavior.NORMAL, true, false, false, true, false, true),
    DENSE_ICE(MapColor.PALE_PURPLE, PistonBehavior.NORMAL, true, false, false, true, false, true),
    AGGREGATE(MapColor.PALE_YELLOW, PistonBehavior.NORMAL, true, false, false, true, false, true),
    SPONGE(MapColor.YELLOW, PistonBehavior.NORMAL, true, false, false, true, false, true),
    SHULKER_BOX(MapColor.PURPLE, PistonBehavior.NORMAL, true, false, false, true, false, true),
    WOOD(MapColor.OAK_TAN, PistonBehavior.NORMAL, true, true, false, true, false, true),
    NETHER_WOOD(MapColor.OAK_TAN, PistonBehavior.NORMAL, true, false, false, true, false, true),
    BAMBOO_SAPLING(MapColor.OAK_TAN, PistonBehavior.DESTROY, false, true, false, true, false, true),
    BAMBOO(MapColor.OAK_TAN, PistonBehavior.DESTROY, true, true, false, true, false, true),
    WOOL(MapColor.WHITE_GRAY, PistonBehavior.NORMAL, true, true, false, true, false, true),
    TNT(MapColor.BRIGHT_RED, PistonBehavior.NORMAL, true, true, false, false, false, true),
    LEAVES(MapColor.DARK_GREEN, PistonBehavior.DESTROY, true, true, false, false, false, true),
    GLASS(MapColor.CLEAR, PistonBehavior.NORMAL, true, false, false, false, false, true),
    ICE(MapColor.PALE_PURPLE, PistonBehavior.NORMAL, true, false, false, false, false, true),
    CACTUS(MapColor.DARK_GREEN, PistonBehavior.DESTROY, true, false, false, false, false, true),
    STONE(MapColor.STONE_GRAY, PistonBehavior.NORMAL, true, false, false, true, false, true),
    METAL(MapColor.IRON_GRAY, PistonBehavior.NORMAL, true, false, false, true, false, true),
    SNOW_BLOCK(MapColor.WHITE, PistonBehavior.NORMAL, true, false, false, true, false, true),
    REPAIR_STATION(MapColor.IRON_GRAY, PistonBehavior.BLOCK, true, false, false, true, false, true),
    BARRIER(MapColor.CLEAR, PistonBehavior.BLOCK, true, false, false, true, false, true),
    PISTON(MapColor.STONE_GRAY, PistonBehavior.BLOCK, true, false, false, true, false, true),
    MOSS_BLOCK(MapColor.DARK_GREEN, PistonBehavior.DESTROY, true, false, false, true, false, true),
    GOURD(MapColor.DARK_GREEN, PistonBehavior.DESTROY, true, false, false, true, false, true),
    EGG(MapColor.DARK_GREEN, PistonBehavior.DESTROY, true, false, false, true, false, true),
    CAKE(MapColor.CLEAR, PistonBehavior.DESTROY, true, false, false, true, false, true),
    AMETHYST(MapColor.PURPLE, PistonBehavior.NORMAL, true, false, false, true, false, true),
    POWDER_SNOW(MapColor.WHITE, PistonBehavior.NORMAL, false, false, false, true, false, false),
    FROGSPAWN(MapColor.WATER_BLUE, PistonBehavior.DESTROY, false, false, false, false, false, false),
    FROGLIGHT(MapColor.CLEAR, PistonBehavior.NORMAL, true, false, false, true, false, true),
    DECORATED_POT(MapColor.TERRACOTTA_RED, PistonBehavior.DESTROY, true, false, false, true, false, true);

    public final MapColor color;
    public final PistonBehavior pistonBehavior;
    public final boolean blocksMovement;
    public final boolean burnable;
    public final boolean liquid;
    public final boolean blocksLight;
    public final boolean replaceable;
    public final boolean solid;

    Material1_19_4(MapColor color, PistonBehavior pistonBehavior, boolean blocksMovement, boolean burnable, boolean liquid, boolean blocksLight, boolean replaceable, boolean solid) {
        this.color = color;
        this.pistonBehavior = pistonBehavior;
        this.blocksMovement = blocksMovement;
        this.burnable = burnable;
        this.liquid = liquid;
        this.blocksLight = blocksLight;
        this.replaceable = replaceable;
        this.solid = solid;
    }
}
