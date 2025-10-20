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

package com.viaversion.viafabricplus.updater;

import com.viaversion.viafabricplus.features.item.filter_creative_tabs.VersionedRegistries;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.io.FileWriter;
import java.io.IOException;
import net.lenni0451.reflect.stream.RStream;
import net.minecraft.Bootstrap;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourceType;
import org.junit.jupiter.api.Test;

import static com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator.NATIVE_VERSION;

public final class UpdateTaskTest {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final String CURRENT_VERSION_RANGE = VersionRange.andNewer(NATIVE_VERSION).toString();

    @Test
    public void update() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        if (SharedConstants.getProtocolVersion() != NATIVE_VERSION.getOriginalVersion()) {
            throw new UnsupportedOperationException("Please update ProtocolTranslator.NATIVE_VERSION to the current protocol version.");
        }

        updateVersionedRegistries();
        updateResourcePacks();
    }

    private static void updateVersionedRegistries() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("versioned-registries.json");
        addMissingItems(data.getAsJsonObject("items"));
        addMissingEnchantments(data.getAsJsonObject("enchantments"));
        addMissingPatterns(data.getAsJsonObject("banner_patterns"));
        addMissingEffects(data.getAsJsonObject("effects"));

        UpdateTaskTest.write("versioned-registries.json", data);
    }

    private static void addMissingItems(final JsonObject items) {
        for (final Item item : Registries.ITEM) {
            if (VersionedRegistries.ITEM_DIFF.containsKey(item) || item == Items.AIR) {
                continue;
            }

            items.addProperty(Registries.ITEM.getId(item).toString(), CURRENT_VERSION_RANGE);
        }
    }

    private static void addMissingEnchantments(final JsonObject enchantments) {
        RStream.of(Enchantments.class).fields().forEach(fieldWrapper -> {
            final RegistryKey registryKey = fieldWrapper.get();
            if (VersionedRegistries.ENCHANTMENT_DIFF.containsKey(registryKey)) {
                return;
            }

            enchantments.addProperty(registryKey.getValue().toString(), CURRENT_VERSION_RANGE);
        });
    }

    private static void addMissingPatterns(final JsonObject patterns) {
        RStream.of(BannerPatterns.class).fields().forEach(fieldWrapper -> {
            final RegistryKey registryKey = fieldWrapper.get();
            if (VersionedRegistries.PATTERN_DIFF.containsKey(registryKey)) {
                return;
            }

            patterns.addProperty(registryKey.getValue().toString(), CURRENT_VERSION_RANGE);
        });
    }

    private static void addMissingEffects(final JsonObject effects) {
        for (final StatusEffect effect : Registries.STATUS_EFFECT) {
            if (VersionedRegistries.EFFECT_DIFF.containsKey(Registries.STATUS_EFFECT.getEntry(effect))) {
                continue;
            }

            effects.addProperty(Registries.STATUS_EFFECT.getId(effect).toString(), CURRENT_VERSION_RANGE);
        }
    }

    private static void updateResourcePacks() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("resource-pack-headers.json");

        final GameVersion version = SharedConstants.getGameVersion();
        if (data.has(version.name())) {
            return;
        }

        final PackVersion packVersion = version.packVersion(ResourceType.CLIENT_RESOURCES);
        final JsonObject packFormat = new JsonObject();
        packFormat.addProperty("major", packVersion.major());
        packFormat.addProperty("minor", packVersion.minor());

        final JsonObject header = new JsonObject();
        header.addProperty("version", version.protocolVersion());
        header.add("pack_format", packFormat);
        data.add(version.name(), header);

        write("resource-pack-headers.json", data);
    }

    private static void write(final String name, final JsonObject data) {
        try (final FileWriter writer = new FileWriter("../src/main/resources/assets/viafabricplus/data/" + name)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
