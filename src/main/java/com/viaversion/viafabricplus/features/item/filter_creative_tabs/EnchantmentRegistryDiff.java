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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import java.util.HashMap;
import java.util.Map;

import static com.viaversion.vialoader.util.VersionRange.*;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.*;
import static net.minecraft.enchantment.Enchantments.*;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.*;

public final class EnchantmentRegistryDiff {
    public static final Map<RegistryKey<Enchantment>, VersionRange> ENCHANTMENT_DIFF = new HashMap<>();

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
    }

    public static boolean keepEnchantment(final RegistryKey<Enchantment> enchantment) {
        return contains(enchantment, ProtocolTranslator.getTargetVersion());
    }

    private static boolean contains(final RegistryKey<Enchantment> enchantment, final ProtocolVersion version) {
        return !ENCHANTMENT_DIFF.containsKey(enchantment) || ENCHANTMENT_DIFF.get(enchantment).contains(version);
    }
}
