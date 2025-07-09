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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import java.util.HashMap;
import java.util.Map;

import static com.viaversion.vialoader.util.VersionRange.*;
import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.*;
import static net.minecraft.entity.effect.StatusEffects.*;
import static net.raphimc.vialegacy.api.LegacyProtocolVersion.*;

public final class EffectRegistryDiff {
    public static final Map<RegistryEntry<StatusEffect>, VersionRange> EFFECT_DIFF = new HashMap<>();

    static {
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
    }

    public static boolean keepEffect(final RegistryEntry<StatusEffect> effect) {
        return contains(effect, ProtocolTranslator.getTargetVersion());
    }

    private static boolean contains(final RegistryEntry<StatusEffect> effect, final ProtocolVersion version) {
        return !EFFECT_DIFF.containsKey(effect) || EFFECT_DIFF.get(effect).contains(version);
    }
}
