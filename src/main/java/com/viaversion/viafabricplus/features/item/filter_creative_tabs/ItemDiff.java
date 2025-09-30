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

package com.viaversion.viafabricplus.features.item.filter_creative_tabs;

import com.viaversion.viafabricplus.injection.access.base.IClientConnection;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;

import static net.raphimc.vialegacy.api.LegacyProtocolVersion.c0_30cpe;

/**
 * Class file which contains the {@link VersionRange} for every item added in the game.
 */
public final class ItemDiff {

    public static final Reference2ObjectMap<Item, VersionRange> ITEM_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final List<Item> EXTENDED_CLASSIC_ITEMS = new ArrayList<>();

    static {
        final JsonObject itemDiff = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-version-diff.json");
        for (final String string : itemDiff.keySet()) {
            if (string.equals("extended_classic_items")) {
                final JsonArray items = itemDiff.getAsJsonArray(string);
                EXTENDED_CLASSIC_ITEMS.addAll(asJsonArray(items));
            } else {
                fill(itemDiff, string);
            }
        }
    }

    private static void fill(final JsonObject itemDiff, final String versions) {
        final VersionRange range = VersionRange.fromString(versions);
        final JsonArray items = itemDiff.getAsJsonArray(versions);
        for (final Item item : asJsonArray(items)) {
            ITEM_DIFF.put(item, range);
        }
    }

    private static Set<Item> asJsonArray(final JsonArray items) {
        final Set<Item> set = new HashSet<>();
        for (final JsonElement element : items) {
            final Item item = Registries.ITEM.get(Identifier.of(element.getAsString()));
            if (item == Items.AIR) {
                throw new IllegalStateException("Item " + element.getAsString() + " does not exist anymore!");
            }
            set.add(item);
        }

        return set;
    }

    public static boolean keepItem(final ItemStack stack) {
        if (!keepItem(stack.getItem())) {
            return false;
        } else {
            return RegistryDiffs.keepItem(stack);
        }
    }

    public static boolean keepItem(final Item item) {
        if (ProtocolTranslator.getTargetVersion().equals(c0_30cpe)) {
            final ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
            if (handler == null) {
                // Don't drop any items if the connection is not established yet
                return true;
            }
            final ExtensionProtocolMetadataStorage extensionProtocol = ((IClientConnection) handler.getConnection()).viaFabricPlus$getUserConnection().get(ExtensionProtocolMetadataStorage.class);
            if (extensionProtocol == null) { // Should never happen
                return false;
            }
            if (extensionProtocol.hasServerExtension(ClassicProtocolExtension.CUSTOM_BLOCKS, 1) && EXTENDED_CLASSIC_ITEMS.contains(item)) {
                return true;
            }
        }

        return containsItem(item, ProtocolTranslator.getTargetVersion());
    }

    public static boolean containsItem(final Item item, final ProtocolVersion version) {
        return !ITEM_DIFF.containsKey(item) || ITEM_DIFF.get(item).contains(version);
    }

}
