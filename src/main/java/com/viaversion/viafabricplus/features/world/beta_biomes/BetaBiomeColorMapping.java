/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.features.world.beta_biomes;

import net.minecraft.resources.Identifier;

public record BetaBiomeColorMapping(
    Identifier id,
    int baseColor,
    int mutatedColor,
    float temperature,
    float downfall
) {

    public BetaBiomeColorMapping(Identifier id, int baseColor, int mutatedColor) {
        this(id, baseColor, mutatedColor, 0.5F, 0.5F);
    }

    public BetaBiomeColorMapping(Identifier id, int baseColor) {
        this(id, baseColor, baseColor);
    }

    public boolean hasMutatedColor() {
        return this.baseColor != this.mutatedColor;
    }

}
