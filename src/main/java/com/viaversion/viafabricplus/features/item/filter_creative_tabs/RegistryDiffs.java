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

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.vialoader.util.VersionRange;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Objects;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import static com.viaversion.vialoader.util.VersionRange.andNewer;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_11;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_11_1;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_13;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_14;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_16;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_19;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20_5;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_7_2;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_8;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_9;
import static net.minecraft.block.entity.BannerPatterns.BASE;
import static net.minecraft.block.entity.BannerPatterns.BORDER;
import static net.minecraft.block.entity.BannerPatterns.BRICKS;
import static net.minecraft.block.entity.BannerPatterns.CIRCLE;
import static net.minecraft.block.entity.BannerPatterns.CROSS;
import static net.minecraft.block.entity.BannerPatterns.CURLY_BORDER;
import static net.minecraft.block.entity.BannerPatterns.DIAGONAL_LEFT;
import static net.minecraft.block.entity.BannerPatterns.DIAGONAL_RIGHT;
import static net.minecraft.block.entity.BannerPatterns.DIAGONAL_UP_LEFT;
import static net.minecraft.block.entity.BannerPatterns.DIAGONAL_UP_RIGHT;
import static net.minecraft.block.entity.BannerPatterns.FLOW;
import static net.minecraft.block.entity.BannerPatterns.GLOBE;
import static net.minecraft.block.entity.BannerPatterns.GRADIENT;
import static net.minecraft.block.entity.BannerPatterns.GRADIENT_UP;
import static net.minecraft.block.entity.BannerPatterns.GUSTER;
import static net.minecraft.block.entity.BannerPatterns.HALF_HORIZONTAL;
import static net.minecraft.block.entity.BannerPatterns.HALF_HORIZONTAL_BOTTOM;
import static net.minecraft.block.entity.BannerPatterns.HALF_VERTICAL;
import static net.minecraft.block.entity.BannerPatterns.HALF_VERTICAL_RIGHT;
import static net.minecraft.block.entity.BannerPatterns.PIGLIN;
import static net.minecraft.block.entity.BannerPatterns.RHOMBUS;
import static net.minecraft.block.entity.BannerPatterns.SMALL_STRIPES;
import static net.minecraft.block.entity.BannerPatterns.SQUARE_BOTTOM_LEFT;
import static net.minecraft.block.entity.BannerPatterns.SQUARE_BOTTOM_RIGHT;
import static net.minecraft.block.entity.BannerPatterns.SQUARE_TOP_LEFT;
import static net.minecraft.block.entity.BannerPatterns.SQUARE_TOP_RIGHT;
import static net.minecraft.block.entity.BannerPatterns.STRAIGHT_CROSS;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_BOTTOM;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_CENTER;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_DOWNLEFT;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_DOWNRIGHT;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_LEFT;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_MIDDLE;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_RIGHT;
import static net.minecraft.block.entity.BannerPatterns.STRIPE_TOP;
import static net.minecraft.block.entity.BannerPatterns.TRIANGLES_BOTTOM;
import static net.minecraft.block.entity.BannerPatterns.TRIANGLES_TOP;
import static net.minecraft.block.entity.BannerPatterns.TRIANGLE_BOTTOM;
import static net.minecraft.block.entity.BannerPatterns.TRIANGLE_TOP;
import static net.minecraft.enchantment.Enchantments.AQUA_AFFINITY;
import static net.minecraft.enchantment.Enchantments.BANE_OF_ARTHROPODS;
import static net.minecraft.enchantment.Enchantments.BINDING_CURSE;
import static net.minecraft.enchantment.Enchantments.BLAST_PROTECTION;
import static net.minecraft.enchantment.Enchantments.BREACH;
import static net.minecraft.enchantment.Enchantments.CHANNELING;
import static net.minecraft.enchantment.Enchantments.DENSITY;
import static net.minecraft.enchantment.Enchantments.DEPTH_STRIDER;
import static net.minecraft.enchantment.Enchantments.EFFICIENCY;
import static net.minecraft.enchantment.Enchantments.FEATHER_FALLING;
import static net.minecraft.enchantment.Enchantments.FIRE_ASPECT;
import static net.minecraft.enchantment.Enchantments.FIRE_PROTECTION;
import static net.minecraft.enchantment.Enchantments.FLAME;
import static net.minecraft.enchantment.Enchantments.FORTUNE;
import static net.minecraft.enchantment.Enchantments.FROST_WALKER;
import static net.minecraft.enchantment.Enchantments.IMPALING;
import static net.minecraft.enchantment.Enchantments.INFINITY;
import static net.minecraft.enchantment.Enchantments.KNOCKBACK;
import static net.minecraft.enchantment.Enchantments.LOOTING;
import static net.minecraft.enchantment.Enchantments.LOYALTY;
import static net.minecraft.enchantment.Enchantments.LUCK_OF_THE_SEA;
import static net.minecraft.enchantment.Enchantments.LURE;
import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.MULTISHOT;
import static net.minecraft.enchantment.Enchantments.PIERCING;
import static net.minecraft.enchantment.Enchantments.POWER;
import static net.minecraft.enchantment.Enchantments.PROJECTILE_PROTECTION;
import static net.minecraft.enchantment.Enchantments.PROTECTION;
import static net.minecraft.enchantment.Enchantments.PUNCH;
import static net.minecraft.enchantment.Enchantments.QUICK_CHARGE;
import static net.minecraft.enchantment.Enchantments.RESPIRATION;
import static net.minecraft.enchantment.Enchantments.RIPTIDE;
import static net.minecraft.enchantment.Enchantments.SHARPNESS;
import static net.minecraft.enchantment.Enchantments.SILK_TOUCH;
import static net.minecraft.enchantment.Enchantments.SMITE;
import static net.minecraft.enchantment.Enchantments.SOUL_SPEED;
import static net.minecraft.enchantment.Enchantments.SWEEPING_EDGE;
import static net.minecraft.enchantment.Enchantments.SWIFT_SNEAK;
import static net.minecraft.enchantment.Enchantments.THORNS;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;
import static net.minecraft.enchantment.Enchantments.VANISHING_CURSE;
import static net.minecraft.enchantment.Enchantments.WIND_BURST;
import static net.minecraft.entity.effect.StatusEffects.ABSORPTION;
import static net.minecraft.entity.effect.StatusEffects.BAD_OMEN;
import static net.minecraft.entity.effect.StatusEffects.BLINDNESS;
import static net.minecraft.entity.effect.StatusEffects.CONDUIT_POWER;
import static net.minecraft.entity.effect.StatusEffects.DARKNESS;
import static net.minecraft.entity.effect.StatusEffects.DOLPHINS_GRACE;
import static net.minecraft.entity.effect.StatusEffects.FIRE_RESISTANCE;
import static net.minecraft.entity.effect.StatusEffects.GLOWING;
import static net.minecraft.entity.effect.StatusEffects.HASTE;
import static net.minecraft.entity.effect.StatusEffects.HEALTH_BOOST;
import static net.minecraft.entity.effect.StatusEffects.HERO_OF_THE_VILLAGE;
import static net.minecraft.entity.effect.StatusEffects.HUNGER;
import static net.minecraft.entity.effect.StatusEffects.INFESTED;
import static net.minecraft.entity.effect.StatusEffects.INSTANT_DAMAGE;
import static net.minecraft.entity.effect.StatusEffects.INSTANT_HEALTH;
import static net.minecraft.entity.effect.StatusEffects.INVISIBILITY;
import static net.minecraft.entity.effect.StatusEffects.JUMP_BOOST;
import static net.minecraft.entity.effect.StatusEffects.LEVITATION;
import static net.minecraft.entity.effect.StatusEffects.LUCK;
import static net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE;
import static net.minecraft.entity.effect.StatusEffects.NAUSEA;
import static net.minecraft.entity.effect.StatusEffects.NIGHT_VISION;
import static net.minecraft.entity.effect.StatusEffects.OOZING;
import static net.minecraft.entity.effect.StatusEffects.POISON;
import static net.minecraft.entity.effect.StatusEffects.RAID_OMEN;
import static net.minecraft.entity.effect.StatusEffects.REGENERATION;
import static net.minecraft.entity.effect.StatusEffects.RESISTANCE;
import static net.minecraft.entity.effect.StatusEffects.SATURATION;
import static net.minecraft.entity.effect.StatusEffects.SLOWNESS;
import static net.minecraft.entity.effect.StatusEffects.SLOW_FALLING;
import static net.minecraft.entity.effect.StatusEffects.SPEED;
import static net.minecraft.entity.effect.StatusEffects.STRENGTH;
import static net.minecraft.entity.effect.StatusEffects.TRIAL_OMEN;
import static net.minecraft.entity.effect.StatusEffects.UNLUCK;
import static net.minecraft.entity.effect.StatusEffects.WATER_BREATHING;
import static net.minecraft.entity.effect.StatusEffects.WEAKNESS;
import static net.minecraft.entity.effect.StatusEffects.WEAVING;
import static net.minecraft.entity.effect.StatusEffects.WIND_CHARGED;
import static net.minecraft.entity.effect.StatusEffects.WITHER;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.b1_8tob1_8_1;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.r1_0_0tor1_0_1;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.r1_1;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.r1_4_2;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.r1_4_6tor1_4_7;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.r1_6_1;

/**
 * Similar to {@link ItemDiff} but for smaller registry references. Mainly used for item component diffing.
 */
public final class RegistryDiffs {

    public static final Reference2ObjectMap<RegistryKey<Enchantment>, VersionRange> ENCHANTMENT_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<RegistryEntry<StatusEffect>, VersionRange> EFFECT_DIFF = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectMap<RegistryKey<BannerPattern>, VersionRange> PATTERN_DIFF = new Reference2ObjectOpenHashMap<>();

    static {
        ENCHANTMENT_DIFF.put(BREACH, andNewer(v1_20_5));
        ENCHANTMENT_DIFF.put(DENSITY, andNewer(v1_20_5));
        ENCHANTMENT_DIFF.put(WIND_BURST, andNewer(v1_20_5));

        ENCHANTMENT_DIFF.put(SWIFT_SNEAK, andNewer(v1_19));

        ENCHANTMENT_DIFF.put(SOUL_SPEED, andNewer(v1_16));

        ENCHANTMENT_DIFF.put(MULTISHOT, andNewer(v1_14));
        ENCHANTMENT_DIFF.put(PIERCING, andNewer(v1_14));
        ENCHANTMENT_DIFF.put(QUICK_CHARGE, andNewer(v1_14));

        ENCHANTMENT_DIFF.put(CHANNELING, andNewer(v1_13));
        ENCHANTMENT_DIFF.put(IMPALING, andNewer(v1_13));
        ENCHANTMENT_DIFF.put(LOYALTY, andNewer(v1_13));
        ENCHANTMENT_DIFF.put(RIPTIDE, andNewer(v1_13));

        ENCHANTMENT_DIFF.put(SWEEPING_EDGE, andNewer(v1_11_1));

        ENCHANTMENT_DIFF.put(BINDING_CURSE, andNewer(v1_11));
        ENCHANTMENT_DIFF.put(VANISHING_CURSE, andNewer(v1_11));

        ENCHANTMENT_DIFF.put(FROST_WALKER, andNewer(v1_9));
        ENCHANTMENT_DIFF.put(MENDING, andNewer(v1_9));

        ENCHANTMENT_DIFF.put(DEPTH_STRIDER, andNewer(v1_8));

        ENCHANTMENT_DIFF.put(LUCK_OF_THE_SEA, andNewer(v1_7_2));
        ENCHANTMENT_DIFF.put(LURE, andNewer(v1_7_2));

        ENCHANTMENT_DIFF.put(THORNS, andNewer(r1_4_6tor1_4_7));

        ENCHANTMENT_DIFF.put(FLAME, andNewer(r1_1));
        ENCHANTMENT_DIFF.put(INFINITY, andNewer(r1_1));
        ENCHANTMENT_DIFF.put(POWER, andNewer(r1_1));
        ENCHANTMENT_DIFF.put(PUNCH, andNewer(r1_1));

        ENCHANTMENT_DIFF.put(AQUA_AFFINITY, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(BANE_OF_ARTHROPODS, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(BLAST_PROTECTION, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(EFFICIENCY, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(FEATHER_FALLING, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(FIRE_ASPECT, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(FIRE_PROTECTION, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(FORTUNE, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(KNOCKBACK, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(LOOTING, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(PROJECTILE_PROTECTION, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(PROTECTION, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(RESPIRATION, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(SHARPNESS, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(SILK_TOUCH, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(SMITE, andNewer(r1_0_0tor1_0_1));
        ENCHANTMENT_DIFF.put(UNBREAKING, andNewer(r1_0_0tor1_0_1));

        // ---

        EFFECT_DIFF.put(WIND_CHARGED, andNewer(v1_20_5));
        EFFECT_DIFF.put(WEAVING, andNewer(v1_20_5));
        EFFECT_DIFF.put(OOZING, andNewer(v1_20_5));
        EFFECT_DIFF.put(INFESTED, andNewer(v1_20_5));
        EFFECT_DIFF.put(RAID_OMEN, andNewer(v1_20_5));
        EFFECT_DIFF.put(TRIAL_OMEN, andNewer(v1_20_5));

        EFFECT_DIFF.put(DARKNESS, andNewer(v1_19));

        EFFECT_DIFF.put(HERO_OF_THE_VILLAGE, andNewer(v1_14));
        EFFECT_DIFF.put(BAD_OMEN, andNewer(v1_14));

        EFFECT_DIFF.put(DOLPHINS_GRACE, andNewer(v1_13));
        EFFECT_DIFF.put(CONDUIT_POWER, andNewer(v1_13));
        EFFECT_DIFF.put(SLOW_FALLING, andNewer(v1_13));

        EFFECT_DIFF.put(LUCK, andNewer(v1_9));
        EFFECT_DIFF.put(UNLUCK, andNewer(v1_9));
        EFFECT_DIFF.put(GLOWING, andNewer(v1_9));
        EFFECT_DIFF.put(LEVITATION, andNewer(v1_9));

        EFFECT_DIFF.put(SATURATION, andNewer(r1_6_1));
        EFFECT_DIFF.put(ABSORPTION, andNewer(r1_6_1));
        EFFECT_DIFF.put(HEALTH_BOOST, andNewer(r1_6_1));

        EFFECT_DIFF.put(WITHER, andNewer(r1_4_2));

        EFFECT_DIFF.put(SPEED, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(SLOWNESS, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(HASTE, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(MINING_FATIGUE, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(STRENGTH, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(INSTANT_HEALTH, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(INSTANT_DAMAGE, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(JUMP_BOOST, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(NAUSEA, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(REGENERATION, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(RESISTANCE, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(FIRE_RESISTANCE, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(WATER_BREATHING, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(INVISIBILITY, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(BLINDNESS, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(NIGHT_VISION, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(HUNGER, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(WEAKNESS, andNewer(b1_8tob1_8_1));
        EFFECT_DIFF.put(POISON, andNewer(b1_8tob1_8_1));
        // https://minecraft.wiki/w/Effect#Effect_additions

        // ---

        PATTERN_DIFF.put(FLOW, andNewer(v1_20_5));
        PATTERN_DIFF.put(GUSTER, andNewer(v1_20_5));

        PATTERN_DIFF.put(PIGLIN, andNewer(v1_16));

        PATTERN_DIFF.put(GLOBE, andNewer(v1_14));

        PATTERN_DIFF.put(BASE, andNewer(v1_8));
        PATTERN_DIFF.put(SQUARE_BOTTOM_LEFT, andNewer(v1_8));
        PATTERN_DIFF.put(SQUARE_BOTTOM_RIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(SQUARE_TOP_LEFT, andNewer(v1_8));
        PATTERN_DIFF.put(SQUARE_TOP_RIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_BOTTOM, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_TOP, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_LEFT, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_RIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_CENTER, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_MIDDLE, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_DOWNRIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(STRIPE_DOWNLEFT, andNewer(v1_8));
        PATTERN_DIFF.put(SMALL_STRIPES, andNewer(v1_8));
        PATTERN_DIFF.put(CROSS, andNewer(v1_8));
        PATTERN_DIFF.put(STRAIGHT_CROSS, andNewer(v1_8));
        PATTERN_DIFF.put(TRIANGLE_BOTTOM, andNewer(v1_8));
        PATTERN_DIFF.put(TRIANGLE_TOP, andNewer(v1_8));
        PATTERN_DIFF.put(TRIANGLES_BOTTOM, andNewer(v1_8));
        PATTERN_DIFF.put(TRIANGLES_TOP, andNewer(v1_8));
        PATTERN_DIFF.put(DIAGONAL_LEFT, andNewer(v1_8));
        PATTERN_DIFF.put(DIAGONAL_UP_RIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(DIAGONAL_UP_LEFT, andNewer(v1_8));
        PATTERN_DIFF.put(DIAGONAL_RIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(CIRCLE, andNewer(v1_8));
        PATTERN_DIFF.put(RHOMBUS, andNewer(v1_8));
        PATTERN_DIFF.put(HALF_VERTICAL, andNewer(v1_8));
        PATTERN_DIFF.put(HALF_HORIZONTAL, andNewer(v1_8));
        PATTERN_DIFF.put(HALF_VERTICAL_RIGHT, andNewer(v1_8));
        PATTERN_DIFF.put(HALF_HORIZONTAL_BOTTOM, andNewer(v1_8));
        PATTERN_DIFF.put(BORDER, andNewer(v1_8));
        PATTERN_DIFF.put(CURLY_BORDER, andNewer(v1_8));
        PATTERN_DIFF.put(GRADIENT, andNewer(v1_8));
        PATTERN_DIFF.put(GRADIENT_UP, andNewer(v1_8));
        PATTERN_DIFF.put(BRICKS, andNewer(v1_8));
    }

    public static boolean keepItem(final ItemStack stack) {
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

    public static boolean containsEffect(final RegistryEntry<StatusEffect> effect, final ProtocolVersion version) {
        return !EFFECT_DIFF.containsKey(effect) || EFFECT_DIFF.get(effect).contains(version);
    }

    public static boolean containsBannerPattern(final RegistryKey<BannerPattern> bannerPattern, final ProtocolVersion version) {
        return !PATTERN_DIFF.containsKey(bannerPattern) || PATTERN_DIFF.get(bannerPattern).contains(version);
    }

}
