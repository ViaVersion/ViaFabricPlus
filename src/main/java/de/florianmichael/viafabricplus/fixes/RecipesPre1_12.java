/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes;

import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.GlazedTerracottaBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandler;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.List;

/**
 * Handles all recipe related stuff for versions older than 1.12.
 */
public class RecipesPre1_12 {

    /**
     * Removes recipes that are not supported in 1.11 and older versions.
     *
     * @param recipes List of recipes
     * @param version Version of the client
     */
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
        if (network == null) return;

        final var world = MinecraftClient.getInstance().world;

        final var result = network.getRecipeManager().
                getFirstMatch(RecipeType.CRAFTING, inventory, world). // Get the first matching recipe
                        map(recipe -> recipe.value().craft(inventory, world.getRegistryManager())). // Craft the recipe to get the result
                        orElse(ItemStack.EMPTY); // If there is no recipe, set the result to air

        // Update the result slot
        network.onScreenHandlerSlotUpdate(new ScreenHandlerSlotUpdateS2CPacket(syncId, screenHandler.getRevision(), 0, result));
    }
}
