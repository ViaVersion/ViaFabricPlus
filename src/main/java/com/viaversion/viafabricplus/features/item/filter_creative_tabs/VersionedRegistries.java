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
import com.viaversion.viaversion.libs.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Objects;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;

import static com.viaversion.viafabricplus.features.classic.cpe_extension.CPEAdditions.EXTENDED_CLASSIC_ITEMS;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.c0_30cpe;

public final class VersionedRegistries {

    public static final Reference2ObjectMap<RegistryKey<Enchantment>, VersionRange> ENCHANTMENT_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<RegistryKey<BannerPattern>, VersionRange> PATTERN_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<RegistryEntry<StatusEffect>, VersionRange> EFFECT_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<Item, VersionRange> ITEM_DIFF = new Reference2ObjectOpenHashMap<>();

    public static void init() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("versioned-registries.json");
        fillKeys(data.getAsJsonObject("enchantments"), RegistryKeys.ENCHANTMENT, ENCHANTMENT_DIFF);
        fillKeys(data.getAsJsonObject("banner_patterns"), RegistryKeys.BANNER_PATTERN, PATTERN_DIFF);
        fillEntries(data.getAsJsonObject("effects"), Registries.STATUS_EFFECT, EFFECT_DIFF);
        fillItems(data.getAsJsonObject("items"));
    }

    private static void fillKeys(final JsonObject object, final RegistryKey registryKey, final Reference2ObjectMap map) {
        for (final String element : object.keySet()) {
            final VersionRange versions = VersionRange.fromString(object.get(element).getAsString());
            final RegistryKey<?> key = RegistryKey.of(registryKey, Identifier.of(element));
            map.put(key, versions);
        }
    }

    private static void fillEntries(final JsonObject object, final Registry<?> registry, final Reference2ObjectMap map) {
        for (final String element : object.keySet()) {
            final VersionRange versions = VersionRange.fromString(object.get(element).getAsString());
            final RegistryEntry entry = registry.getEntry(Identifier.of(element)).orElseThrow();
            map.put(entry, versions);
        }
    }

    private static void fillItems(final JsonObject object) {
        for (final String element : object.keySet()) {
            final VersionRange versions = VersionRange.fromString(object.get(element).getAsString());
            final Item item = Registries.ITEM.getOptionalValue(Identifier.of(element)).orElse(null);
            if (item == null) {
                throw new IllegalStateException("Unknown item: " + element);
            }

            ITEM_DIFF.put(item, versions);
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

    public static boolean keepItem(final ItemStack stack) {
        if (!keepItem(stack.getItem())) {
            return false;
        }

        if (filterEnchantments(DataComponentTypes.ENCHANTMENTS, stack)) {
            return false;
        }

        if (filterEnchantments(DataComponentTypes.STORED_ENCHANTMENTS, stack)) {
            return false;
        }

        final BannerPatternsComponent bannerPatterns = stack.get(DataComponentTypes.BANNER_PATTERNS);
        if (bannerPatterns != null) {
            for (final BannerPatternsComponent.Layer layer : bannerPatterns.layers()) {
                if (!layer.pattern().getKey().map(key -> containsBannerPattern(key, ProtocolTranslator.getTargetVersion())).orElse(true)) {
                    return false;
                }
            }
        }

        final PotionContentsComponent potionContents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContents != null) {
            for (final StatusEffectInstance effectInstance : Objects.requireNonNull(potionContents).getEffects()) {
                if (!containsEffect(effectInstance.getEffectType(), ProtocolTranslator.getTargetVersion())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean filterEnchantments(final ComponentType<ItemEnchantmentsComponent> componentType, final ItemStack stack) {
        final ItemEnchantmentsComponent enchantments = stack.get(componentType);
        if (enchantments != null) {
            for (final RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                if (!enchantment.getKey().map(key -> containsEnchantment(key, ProtocolTranslator.getTargetVersion())).orElse(true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsEnchantment(final RegistryKey<Enchantment> enchantment, final ProtocolVersion version) {
        return !ENCHANTMENT_DIFF.containsKey(enchantment) || ENCHANTMENT_DIFF.get(enchantment).contains(version);
    }

    public static boolean containsBannerPattern(final RegistryKey<BannerPattern> bannerPattern, final ProtocolVersion version) {
        return !PATTERN_DIFF.containsKey(bannerPattern) || PATTERN_DIFF.get(bannerPattern).contains(version);
    }

    public static boolean containsEffect(final RegistryEntry<StatusEffect> effect, final ProtocolVersion version) {
        return !EFFECT_DIFF.containsKey(effect) || EFFECT_DIFF.get(effect).contains(version);
    }

    public static boolean containsItem(final Item item, final ProtocolVersion version) {
        return !ITEM_DIFF.containsKey(item) || ITEM_DIFF.get(item).contains(version);
    }

}
