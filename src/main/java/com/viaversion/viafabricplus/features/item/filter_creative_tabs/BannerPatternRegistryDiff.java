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
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;

import java.util.HashMap;
import java.util.Map;

import static com.viaversion.vialoader.util.VersionRange.*;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.*;
import static net.minecraft.block.entity.BannerPatterns.*;

public final class BannerPatternRegistryDiff {
    public static final Map<RegistryKey<BannerPattern>, VersionRange> PATTERN_DIFF = new HashMap<>();

    static {
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

    public static boolean keepBannerPattern(final RegistryKey<BannerPattern> bannerPattern) {
        return contains(bannerPattern, ProtocolTranslator.getTargetVersion());
    }

    private static boolean contains(final RegistryKey<BannerPattern> bannerPattern, final ProtocolVersion version) {
        return !PATTERN_DIFF.containsKey(bannerPattern) || PATTERN_DIFF.get(bannerPattern).contains(version);
    }
}
