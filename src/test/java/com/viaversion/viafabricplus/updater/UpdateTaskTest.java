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

package com.viaversion.viafabricplus.updater;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.io.FileWriter;
import java.io.IOException;
import net.lenni0451.reflect.stream.RStream;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.junit.jupiter.api.Test;

import static com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl.NATIVE_VERSION;

public final class UpdateTaskTest {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final String CURRENT_VERSION_RANGE = ProtocolVersionRange.andNewer(NATIVE_VERSION).toString();

    @Test
    public void update() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
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
        for (final Item item : BuiltInRegistries.ITEM) {
            if (ViaFabricPlus.api().limitations().itemExists(item, ProtocolVersion.unknown) || item == Items.AIR) {
                continue;
            }

            items.addProperty(BuiltInRegistries.ITEM.getKey(item).toString(), CURRENT_VERSION_RANGE);
        }
    }

    private static void addMissingEnchantments(final JsonObject enchantments) {
        RStream.of(Enchantments.class).fields().forEach(fieldWrapper -> {
            final ResourceKey<Enchantment> registryKey = fieldWrapper.get();
            if (ViaFabricPlus.api().limitations().enchantmentExists(registryKey, ProtocolVersion.unknown)) {
                return;
            }

            enchantments.addProperty(registryKey.identifier().toString(), CURRENT_VERSION_RANGE);
        });
    }

    private static void addMissingPatterns(final JsonObject patterns) {
        RStream.of(BannerPatterns.class).fields().forEach(fieldWrapper -> {
            final ResourceKey<BannerPattern> registryKey = fieldWrapper.get();
            if (ViaFabricPlus.api().limitations().bannerPatternExists(registryKey, ProtocolVersion.unknown)) {
                return;
            }

            patterns.addProperty(registryKey.identifier().toString(), CURRENT_VERSION_RANGE);
        });
    }

    private static void addMissingEffects(final JsonObject effects) {
        for (final MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            if (ViaFabricPlus.api().limitations().effectExists(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect), ProtocolVersion.unknown)) {
                continue;
            }

            effects.addProperty(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), CURRENT_VERSION_RANGE);
        }
    }

    private static void updateResourcePacks() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("resource-pack-headers.json");

        final WorldVersion version = SharedConstants.getCurrentVersion();
        if (data.has(version.name())) {
            return;
        }

        final PackFormat packVersion = version.packVersion(PackType.CLIENT_RESOURCES);
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
