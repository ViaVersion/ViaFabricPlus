/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy;

import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.alpha.a1_2_3_5_1_2_6tob1_0_1_1_1.provider.AlphaInventoryProvider;

public final class ViaFabricPlusAlphaInventoryProvider extends AlphaInventoryProvider {

    @Override
    public boolean usesInventoryTracker() {
        return false;
    }

    private Item[] convertItems(final List<ItemStack> stacks) {
        final Item[] items = new Item[stacks.size()];

        for (int i = 0; i < items.length; i++) {
            items[i] = convertItem(stacks.get(i));
        }
        return items;
    }

    private Item convertItem(final ItemStack stack) {
        if (!stack.isEmpty()) {
            final Item item = ItemTranslator.mcToVia(stack, LegacyProtocolVersion.b1_8tob1_8_1);
            if (item != null) {
                return item.copy();
            }
        }
        return null;
    }

    @Override
    public Item[] getMainInventoryItems(UserConnection connection) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return new Item[37];
        } else {
            return convertItems(player.getInventory().getMainStacks());
        }
    }

    @Override
    public Item[] getCraftingInventoryItems(UserConnection connection) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return new Item[4];
        } else {
            return convertItems(player.playerScreenHandler.getCraftingInput().getHeldStacks());
        }
    }

    @Override
    public Item[] getArmorInventoryItems(UserConnection connection) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        final Item[] items = new Item[4];
        if (player != null) {
            final PlayerInventory inventory = player.getInventory();
            items[0] = convertItem(inventory.equipment.get(EquipmentSlot.FEET));
            items[1] = convertItem(inventory.equipment.get(EquipmentSlot.LEGS));
            items[2] = convertItem(inventory.equipment.get(EquipmentSlot.CHEST));
            items[3] = convertItem(inventory.equipment.get(EquipmentSlot.HEAD));
        }

        return items;
    }

    @Override
    public Item[] getContainerItems(UserConnection connection) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return new Item[37];
        } else {
            return convertItems(player.currentScreenHandler.getStacks());
        }
    }

    @Override
    public void addToInventory(UserConnection connection, Item item) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        player.getInventory().insertStack(ItemTranslator.viaToMc(item, LegacyProtocolVersion.b1_8tob1_8_1));
    }

}
