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

package com.viaversion.viafabricplus.features.v1_11_1;

import com.mojang.datafixers.util.Pair;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class FurnaceFuels1_11_2 {

    private static final List<Pair<Fuel, ProtocolVersionRange>> LEGACY_FUELS = new ArrayList<>();
    private static Set<Item> FUELS;

    public static void init() {
        if (!LEGACY_FUELS.isEmpty()) {
            throw new IllegalStateException("FurnaceFuels1_11_2 is already initialized");
        }

        final JsonArray fuels = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("furnace-fuels-1.11.2.json").getAsJsonArray("");
        for (JsonElement fuelElement : fuels) {
            final JsonObject fuel = fuelElement.getAsJsonObject();
            final ProtocolVersionRange versionRange = ProtocolVersionRange.fromString(fuel.get("version").getAsString());
            if (fuel.has("tag")) {
                LEGACY_FUELS.add(new Pair<>(new Fuel(null, TagKey.create(Registries.ITEM, Identifier.parse(fuel.get("tag").getAsString()))), versionRange));
            } else {
                final Identifier id = Identifier.parse(fuel.get("item").getAsString());
                final Item item = BuiltInRegistries.ITEM.getOptional(id).orElseThrow(() -> new IllegalStateException("Unknown item: " + id));
                LEGACY_FUELS.add(new Pair<>(new Fuel(item, null), versionRange));
            }
        }
    }

    public static boolean isFuel(final ItemStack itemStack) {
        if (FUELS == null) {
            FUELS = resolveFuels(ViaFabricPlus.api().targetVersion());
        }
        return FUELS.contains(itemStack.getItem());
    }

    public static void reset() {
        FUELS = null;
    }

    private static Set<Item> resolveFuels(final ProtocolVersion version) {
        final Set<Item> fuels = new HashSet<>();
        for (Pair<Fuel, ProtocolVersionRange> legacyFuel : LEGACY_FUELS) {
            if (!legacyFuel.getSecond().contains(version)) {
                continue;
            }

            final Fuel fuel = legacyFuel.getFirst();
            if (fuel.item() != null) {
                fuels.add(fuel.item());
            } else {
                BuiltInRegistries.ITEM.get(fuel.tag()).ifPresent(items -> {
                    for (Holder<Item> item : items) {
                        fuels.add(item.value());
                    }
                });
            }
        }
        return fuels;
    }

    private record Fuel(@Nullable Item item, @Nullable TagKey<Item> tag) {
    }

}
