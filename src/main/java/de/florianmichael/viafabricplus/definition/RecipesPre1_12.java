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
package de.florianmichael.viafabricplus.definition;

import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.List;

public class RecipesPre1_12 {

    public static void editRecipes(final List<Recipe<?>> recipes, final VersionEnum version) {
        final var registryManager = MinecraftClient.getInstance().world.getRegistryManager();

        recipes.removeIf(recipe -> {
            if (recipe.getResult(registryManager).getItem() instanceof BlockItem block) {
                return block.getBlock() instanceof ConcretePowderBlock || block.getBlock() instanceof GlazedTerracottaBlock;
            }
            return false;
        });

        if (version.isOlderThanOrEqualTo(VersionEnum.r1_11)) {
            recipes.removeIf(recipe -> recipe.getResult(registryManager).getItem() == Items.IRON_NUGGET);

            if (version.isOlderThanOrEqualTo(VersionEnum.r1_10)) {
                recipes.removeIf(recipe -> {
                    Item item = recipe.getResult(registryManager).getItem();
                    if (item instanceof BlockItem blockItem) {
                        return blockItem.getBlock() instanceof ShulkerBoxBlock;
                    } else if (item == Items.OBSERVER || item == Items.IRON_NUGGET) {
                        return true;
                    } else if (item == Items.GOLD_NUGGET) {
                        return recipe.getSerializer() == RecipeSerializer.SMELTING;
                    } else {
                        return false;
                    }
                });
            }

            if (version.isOlderThanOrEqualTo(VersionEnum.r1_9_3tor1_9_4)) {
                recipes.removeIf(recipe -> recipe.getResult(registryManager).getItem() == Items.BONE_BLOCK);
            }
        }
    }
}
