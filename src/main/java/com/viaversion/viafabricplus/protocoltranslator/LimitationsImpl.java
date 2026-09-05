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

package com.viaversion.viafabricplus.protocoltranslator;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.protocoltranslator.Limitations;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viafabricplus.protocoltranslator.util.ItemUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersionRange;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.v1_10to1_11.Protocol1_10To1_11;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Objects;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.data.ClassicProtocolExtension;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;

import static com.viaversion.viafabricplus.features.classic.cpe_extension.CPEAdditions.EXTENDED_CLASSIC_ITEMS;

public final class LimitationsImpl implements Limitations {

    private final Reference2ObjectMap<ResourceKey<Enchantment>, ProtocolVersionRange> enchantmentDiff = new Reference2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<ResourceKey<BannerPattern>, ProtocolVersionRange> patternDiff = new Reference2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<Holder<MobEffect>, ProtocolVersionRange> effectDiff = new Reference2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<Item, ProtocolVersionRange> itemDiff = new Reference2ObjectOpenHashMap<>();

    public LimitationsImpl() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("versioned-registries.json");
        fillKeys(data.getAsJsonObject("enchantments"), Registries.ENCHANTMENT, enchantmentDiff);
        fillKeys(data.getAsJsonObject("banner_patterns"), Registries.BANNER_PATTERN, patternDiff);
        fillEntries(data.getAsJsonObject("effects"), BuiltInRegistries.MOB_EFFECT, effectDiff);
        fillItems(data.getAsJsonObject("items"));
    }

    @Override
    public int maxChatLength(final ProtocolVersion version) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(LegacyProtocolVersion.c0_28toc0_30)) {
            final ExtensionProtocolMetadataStorage storage = ViaFabricPlus.api().userConnection().get(ExtensionProtocolMetadataStorage.class);
            if (storage != null && storage.hasServerExtension(ClassicProtocolExtension.LONGER_MESSAGES)) {
                return Short.MAX_VALUE * 2;
            } else {
                return 64 - (Minecraft.getInstance().getUser().getName().length() + 2);
            }
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_9_3)) {
            return 100;
        } else {
            return SharedConstants.MAX_CHAT_LENGTH;
        }
    }

    @Override
    public boolean itemExists(final Item item, final ProtocolVersion version) {
        return !this.itemDiff.containsKey(item) || this.itemDiff.get(item).contains(version);
    }

    @Override
    public boolean enchantmentExists(final ResourceKey<Enchantment> enchantment, final ProtocolVersion version) {
        return !this.enchantmentDiff.containsKey(enchantment) || this.enchantmentDiff.get(enchantment).contains(version);
    }

    @Override
    public boolean effectExists(final Holder<MobEffect> effect, final ProtocolVersion version) {
        return !this.effectDiff.containsKey(effect) || this.effectDiff.get(effect).contains(version);
    }

    @Override
    public boolean bannerPatternExists(final ResourceKey<BannerPattern> pattern, final ProtocolVersion version) {
        return !this.patternDiff.containsKey(pattern) || this.patternDiff.get(pattern).contains(version);
    }

    @Override
    public boolean itemExistsInConnection(final Item item) {
        if (ViaFabricPlus.api().targetVersion().equals(LegacyProtocolVersion.c0_30cpe)) {
            if (ViaFabricPlus.api().userConnection() != null) {
                final ExtensionProtocolMetadataStorage storage = ViaFabricPlus.api().userConnection().get(ExtensionProtocolMetadataStorage.class);
                if (storage.hasServerExtension(ClassicProtocolExtension.CUSTOM_BLOCKS, 1) && EXTENDED_CLASSIC_ITEMS.contains(item)) {
                    return true;
                }
            }
        }

        return this.itemExists(item, ViaFabricPlus.api().targetVersion());
    }

    @Override
    public boolean itemExistsInConnection(final ItemStack stack) {
        if (!this.itemExistsInConnection(stack.getItem())) {
            return false;
        }

        if (this.filterEnchantments(DataComponents.ENCHANTMENTS, stack)) {
            return false;
        }

        if (this.filterEnchantments(DataComponents.STORED_ENCHANTMENTS, stack)) {
            return false;
        }

        final BannerPatternLayers bannerPatterns = stack.get(DataComponents.BANNER_PATTERNS);
        if (bannerPatterns != null) {
            for (final BannerPatternLayers.Layer layer : bannerPatterns.layers()) {
                if (!layer.pattern().unwrapKey().map(key -> this.bannerPatternExists(key, ViaFabricPlus.api().targetVersion())).orElse(true)) {
                    return false;
                }
            }
        }

        final PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
        if (potionContents != null) {
            for (final MobEffectInstance effectInstance : Objects.requireNonNull(potionContents).getAllEffects()) {
                if (!effectExists(effectInstance.getEffect(), ViaFabricPlus.api().targetVersion())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean filterEnchantments(final DataComponentType<ItemEnchantments> componentType, final ItemStack stack) {
        final ItemEnchantments enchantments = stack.get(componentType);
        if (enchantments != null) {
            for (final Holder<Enchantment> enchantment : enchantments.keySet()) {
                if (!enchantment.unwrapKey().map(key -> this.enchantmentExists(key, ViaFabricPlus.api().targetVersion())).orElse(true)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getStackCount(final ItemStack stack) {
        final CompoundTag tag = ItemUtil.getTagOrNull(stack);
        if (tag != null) {
            return tag.getIntOr(ItemUtil.vvNbtName(Protocol1_10To1_11.class), stack.getCount());
        } else {
            return stack.getCount();
        }
    }

    private void fillKeys(final JsonObject object, final ResourceKey registryKey, final Reference2ObjectMap map) {
        for (final String element : object.keySet()) {
            final ProtocolVersionRange versions = ProtocolVersionRange.fromString(object.get(element).getAsString());
            final ResourceKey<?> key = ResourceKey.create(registryKey, Identifier.parse(element));
            map.put(key, versions);
        }
    }

    private void fillEntries(final JsonObject object, final Registry<?> registry, final Reference2ObjectMap map) {
        for (final String element : object.keySet()) {
            final ProtocolVersionRange versions = ProtocolVersionRange.fromString(object.get(element).getAsString());
            final Holder entry = registry.get(Identifier.parse(element)).orElseThrow();
            map.put(entry, versions);
        }
    }

    private void fillItems(final JsonObject object) {
        for (final String element : object.keySet()) {
            final ProtocolVersionRange versions = ProtocolVersionRange.fromString(object.get(element).getAsString());
            final Item item = BuiltInRegistries.ITEM.getOptional(Identifier.parse(element)).orElse(null);
            if (item == null) {
                throw new IllegalStateException("Unknown item: " + element);
            }

            itemDiff.put(item, versions);
        }
    }

}
