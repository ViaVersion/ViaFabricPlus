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

package de.florianmichael.viafabricplus.protocolhack.provider.vialegacy;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import de.florianmichael.viafabricplus.protocolhack.translator.ItemTranslator;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.raphimc.vialegacy.protocols.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.providers.AlphaInventoryProvider;
import net.raphimc.vialegacy.protocols.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.providers.TrackingAlphaInventoryProvider;
import net.raphimc.vialegacy.protocols.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.AlphaInventoryTracker;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.List;

import static net.raphimc.vialegacy.protocols.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.Protocolb1_0_1_1_1toa1_2_3_5_1_2_6.copyItems;

public class ViaFabricPlusAlphaInventoryProvider extends AlphaInventoryProvider {
    private final TrackingAlphaInventoryProvider trackingAlphaInventoryProvider;

    public ViaFabricPlusAlphaInventoryProvider(final TrackingAlphaInventoryProvider trackingAlphaInventoryProvider) {
        this.trackingAlphaInventoryProvider = trackingAlphaInventoryProvider;
    }

    @Override
    public boolean usesInventoryTracker() {
        return !GeneralSettings.global().emulateInventoryActionsInLegacyVersions.getValue();
    }

    protected Item[] getMinecraftContainerItems(final List<ItemStack> trackingItems) {
        final var items = new Item[trackingItems.size()];

        for (int i = 0; i < items.length; i++) {
            final var alphaItem = trackingItems.get(i);
            if (alphaItem.isEmpty()) continue;

            items[i] = ItemTranslator.mcToVia(alphaItem, VersionEnum.b1_8tob1_8_1);
        }
        return copyItems(items);
    }

    @Override
    public Item[] getMainInventoryItems(UserConnection user) {
        if (usesInventoryTracker()) {
            return trackingAlphaInventoryProvider.getMainInventoryItems(user);
        } else if (getPlayer() == null) {
            return new Item[37];
        }

        return getMinecraftContainerItems(getPlayer().getInventory().main);
    }

    @Override
    public Item[] getCraftingInventoryItems(UserConnection user) {
        if (usesInventoryTracker()) {
            return trackingAlphaInventoryProvider.getCraftingInventoryItems(user);
        } else if (getPlayer() == null) {
            return new Item[4];
        }

        return getMinecraftContainerItems(getPlayer().playerScreenHandler.getCraftingInput().getInputStacks());
    }

    @Override
    public Item[] getArmorInventoryItems(UserConnection user) {
        if (usesInventoryTracker()) {
            return trackingAlphaInventoryProvider.getArmorInventoryItems(user);
        } else if (getPlayer() == null) {
            return new Item[4];
        }

        return getMinecraftContainerItems(getPlayer().getInventory().armor);
    }

    @Override
    public Item[] getContainerItems(UserConnection user) {
        if (usesInventoryTracker()) {
            return trackingAlphaInventoryProvider.getContainerItems(user);
        } else if (getPlayer() == null) {
            return new Item[37];
        }

        return getMinecraftContainerItems(getPlayer().currentScreenHandler.getStacks());
    }

    @Override
    public void addToInventory(UserConnection user, Item item) {
        if (usesInventoryTracker()) {
            trackingAlphaInventoryProvider.addToInventory(user, item);
        } else {
            getPlayer().getInventory().insertStack(ItemTranslator.viaB1_8toMc(item));
        }
    }

    protected ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

}
