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

package com.viaversion.viafabricplus.features.item.r1_14_4_enchantment_tooltip;

import com.viaversion.viaversion.util.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;

public final class Enchantments1_14_4 {

    private static final Map<String, RegistryKey<Enchantment>> ENCHANTMENT_REGISTRY = new HashMap<>();

    static {
        ENCHANTMENT_REGISTRY.put("protection", Enchantments.PROTECTION);
        ENCHANTMENT_REGISTRY.put("fire_protection", Enchantments.FIRE_PROTECTION);
        ENCHANTMENT_REGISTRY.put("feather_falling", Enchantments.FEATHER_FALLING);
        ENCHANTMENT_REGISTRY.put("blast_protection", Enchantments.BLAST_PROTECTION);
        ENCHANTMENT_REGISTRY.put("projectile_protection", Enchantments.PROJECTILE_PROTECTION);
        ENCHANTMENT_REGISTRY.put("respiration", Enchantments.RESPIRATION);
        ENCHANTMENT_REGISTRY.put("aqua_affinity", Enchantments.AQUA_AFFINITY);
        ENCHANTMENT_REGISTRY.put("thorns", Enchantments.THORNS);
        ENCHANTMENT_REGISTRY.put("depth_strider", Enchantments.DEPTH_STRIDER);
        ENCHANTMENT_REGISTRY.put("frost_walker", Enchantments.FROST_WALKER);
        ENCHANTMENT_REGISTRY.put("binding_curse", Enchantments.BINDING_CURSE);
        ENCHANTMENT_REGISTRY.put("sharpness", Enchantments.SHARPNESS);
        ENCHANTMENT_REGISTRY.put("smite", Enchantments.SMITE);
        ENCHANTMENT_REGISTRY.put("bane_of_arthropods", Enchantments.BANE_OF_ARTHROPODS);
        ENCHANTMENT_REGISTRY.put("knockback", Enchantments.KNOCKBACK);
        ENCHANTMENT_REGISTRY.put("fire_aspect", Enchantments.FIRE_ASPECT);
        ENCHANTMENT_REGISTRY.put("looting", Enchantments.LOOTING);
        ENCHANTMENT_REGISTRY.put("sweeping", Enchantments.SWEEPING_EDGE);
        ENCHANTMENT_REGISTRY.put("efficiency", Enchantments.EFFICIENCY);
        ENCHANTMENT_REGISTRY.put("silk_touch", Enchantments.SILK_TOUCH);
        ENCHANTMENT_REGISTRY.put("unbreaking", Enchantments.UNBREAKING);
        ENCHANTMENT_REGISTRY.put("fortune", Enchantments.FORTUNE);
        ENCHANTMENT_REGISTRY.put("power", Enchantments.POWER);
        ENCHANTMENT_REGISTRY.put("punch", Enchantments.PUNCH);
        ENCHANTMENT_REGISTRY.put("flame", Enchantments.FLAME);
        ENCHANTMENT_REGISTRY.put("infinity", Enchantments.INFINITY);
        ENCHANTMENT_REGISTRY.put("luck_of_the_sea", Enchantments.LUCK_OF_THE_SEA);
        ENCHANTMENT_REGISTRY.put("lure", Enchantments.LURE);
        ENCHANTMENT_REGISTRY.put("loyalty", Enchantments.LOYALTY);
        ENCHANTMENT_REGISTRY.put("impaling", Enchantments.IMPALING);
        ENCHANTMENT_REGISTRY.put("riptide", Enchantments.RIPTIDE);
        ENCHANTMENT_REGISTRY.put("channeling", Enchantments.CHANNELING);
        ENCHANTMENT_REGISTRY.put("multishot", Enchantments.MULTISHOT);
        ENCHANTMENT_REGISTRY.put("quick_charge", Enchantments.QUICK_CHARGE);
        ENCHANTMENT_REGISTRY.put("piercing", Enchantments.PIERCING);
        ENCHANTMENT_REGISTRY.put("mending", Enchantments.MENDING);
        ENCHANTMENT_REGISTRY.put("vanishing_curse", Enchantments.VANISHING_CURSE);
    }

    public static Optional<RegistryKey<Enchantment>> getOrEmpty(final String identifier) {
        return Optional.ofNullable(ENCHANTMENT_REGISTRY.get(Key.stripMinecraftNamespace(identifier)));
    }

}
