/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.alpha.a1_2_3_5_1_2_6tob1_0_1_1_1.provider.AlphaInventoryProvider;

import java.util.List;

import static net.raphimc.vialegacy.protocol.alpha.a1_2_3_5_1_2_6tob1_0_1_1_1.Protocola1_2_3_5_1_2_6Tob1_0_1_1_1.copyItems;

public final class ViaFabricPlusAlphaInventoryProvider extends AlphaInventoryProvider {

    @Override
    public boolean usesInventoryTracker() {
        return false;
    }

    protected Item[] getMinecraftContainerItems(final List<ItemStack> trackingItems) {
        final Item[] items = new Item[trackingItems.size()];

        for (int i = 0; i < items.length; i++) {
            final ItemStack alphaItem = trackingItems.get(i);
            if (alphaItem.isEmpty()) continue;

            items[i] = ItemTranslator.mcToVia(alphaItem, LegacyProtocolVersion.b1_8tob1_8_1);
        }
        return copyItems(items);
    }

    @Override
    public Item[] getMainInventoryItems(UserConnection user) {
        if (getPlayer() == null) {
            return new Item[37];
        } else {
            return getMinecraftContainerItems(getPlayer().getInventory().main);
        }
    }

    @Override
    public Item[] getCraftingInventoryItems(UserConnection user) {
        if (getPlayer() == null) {
            return new Item[4];
        } else {
            return getMinecraftContainerItems(getPlayer().playerScreenHandler.getCraftingInput().getHeldStacks());
        }
    }

    @Override
    public Item[] getArmorInventoryItems(UserConnection user) {
        if (getPlayer() == null) {
            return new Item[4];
        } else {
            return getMinecraftContainerItems(getPlayer().getInventory().armor);
        }
    }

    @Override
    public Item[] getContainerItems(UserConnection user) {
        if (getPlayer() == null) {
            return new Item[37];
        } else {
            return getMinecraftContainerItems(getPlayer().currentScreenHandler.getStacks());
        }
    }

    @Override
    public void addToInventory(UserConnection user, Item item) {
        getPlayer().getInventory().insertStack(ItemTranslator.viaToMc(item, LegacyProtocolVersion.b1_8tob1_8_1));
    }

    protected ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

}
