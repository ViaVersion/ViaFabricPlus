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

import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Objects;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;

import static com.viaversion.viafabricplus.features.classic.cpe_extension.CPEAdditions.EXTENDED_CLASSIC_ITEMS;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.c0_30cpe;

public final class VersionedRegistries {

    public static final Reference2ObjectMap<ResourceKey<Enchantment>, VersionRange> ENCHANTMENT_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<ResourceKey<BannerPattern>, VersionRange> PATTERN_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<Holder<MobEffect>, VersionRange> EFFECT_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<Item, VersionRange> ITEM_DIFF = new Reference2ObjectOpenHashMap<>();

    public static void init() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("versioned-registries.json");
        fillKeys(data.getAsJsonObject("enchantments"), Registries.ENCHANTMENT, ENCHANTMENT_DIFF);
        fillKeys(data.getAsJsonObject("banner_patterns"), Registries.BANNER_PATTERN, PATTERN_DIFF);
        fillEntries(data.getAsJsonObject("effects"), BuiltInRegistries.MOB_EFFECT, EFFECT_DIFF);
        fillItems(data.getAsJsonObject("items"));
    }

    private static void fillKeys(final JsonObject object, final ResourceKey registryKey, final Reference2ObjectMap map) {
        for (final String element : object.keySet()) {
            final VersionRange versions = VersionRange.fromString(object.get(element).getAsString());
            final ResourceKey<?> key = ResourceKey.create(registryKey, Identifier.parse(element));
            map.put(key, versions);
        }
    }

    private static void fillEntries(final JsonObject object, final Registry<?> registry, final Reference2ObjectMap map) {
        for (final String element : object.keySet()) {
            final VersionRange versions = VersionRange.fromString(object.get(element).getAsString());
            final Holder entry = registry.get(Identifier.parse(element)).orElseThrow();
            map.put(entry, versions);
        }
    }

    private static void fillItems(final JsonObject object) {
        for (final String element : object.keySet()) {
            final VersionRange versions = VersionRange.fromString(object.get(element).getAsString());
            final Item item = BuiltInRegistries.ITEM.getOptional(Identifier.parse(element)).orElse(null);
            if (item == null) {
                throw new IllegalStateException("Unknown item: " + element);
            }

            ITEM_DIFF.put(item, versions);
        }
    }

    public static boolean keepItem(final Item item) {
        if (ProtocolTranslator.getTargetVersion().equals(c0_30cpe)) {
            final ClientPacketListener handler = Minecraft.getInstance().getConnection();
            if (handler == null) {
                // Don't drop any items if the connection is not established yet
                return true;
            }
            final ExtensionProtocolMetadataStorage extensionProtocol = ((IConnection) handler.getConnection()).viaFabricPlus$getUserConnection().get(ExtensionProtocolMetadataStorage.class);
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

        if (filterEnchantments(DataComponents.ENCHANTMENTS, stack)) {
            return false;
        }

        if (filterEnchantments(DataComponents.STORED_ENCHANTMENTS, stack)) {
            return false;
        }

        final BannerPatternLayers bannerPatterns = stack.get(DataComponents.BANNER_PATTERNS);
        if (bannerPatterns != null) {
            for (final BannerPatternLayers.Layer layer : bannerPatterns.layers()) {
                if (!layer.pattern().unwrapKey().map(key -> containsBannerPattern(key, ProtocolTranslator.getTargetVersion())).orElse(true)) {
                    return false;
                }
            }
        }

        final PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null) {
            for (final MobEffectInstance effectInstance : Objects.requireNonNull(potionContents).getAllEffects()) {
                if (!containsEffect(effectInstance.getEffect(), ProtocolTranslator.getTargetVersion())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean filterEnchantments(final DataComponentType<ItemEnchantments> componentType, final ItemStack stack) {
        final ItemEnchantments enchantments = stack.get(componentType);
        if (enchantments != null) {
            for (final Holder<Enchantment> enchantment : enchantments.keySet()) {
                if (!enchantment.unwrapKey().map(key -> containsEnchantment(key, ProtocolTranslator.getTargetVersion())).orElse(true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsEnchantment(final ResourceKey<Enchantment> enchantment, final ProtocolVersion version) {
        return !ENCHANTMENT_DIFF.containsKey(enchantment) || ENCHANTMENT_DIFF.get(enchantment).contains(version);
    }

    public static boolean containsBannerPattern(final ResourceKey<BannerPattern> bannerPattern, final ProtocolVersion version) {
        return !PATTERN_DIFF.containsKey(bannerPattern) || PATTERN_DIFF.get(bannerPattern).contains(version);
    }

    public static boolean containsEffect(final Holder<MobEffect> effect, final ProtocolVersion version) {
        return !EFFECT_DIFF.containsKey(effect) || EFFECT_DIFF.get(effect).contains(version);
    }

    public static boolean containsItem(final Item item, final ProtocolVersion version) {
        return !ITEM_DIFF.containsKey(item) || ITEM_DIFF.get(item).contains(version);
    }

}
