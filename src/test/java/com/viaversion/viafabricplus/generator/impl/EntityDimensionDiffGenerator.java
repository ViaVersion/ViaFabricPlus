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

package com.viaversion.viafabricplus.generator.impl;

import com.viaversion.viafabricplus.features.entity.EntityDimensionDiff;
import com.viaversion.viafabricplus.generator.util.FieldUtil;
import com.viaversion.viafabricplus.generator.util.Generator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Map;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

@SuppressWarnings("unchecked")
public final class EntityDimensionDiffGenerator implements Generator {

    @Override
    public StringBuilder generate(final ProtocolVersion nativeVersion) {
        final Object entityDimensions = FieldUtil.getFieldValue(EntityDimensionDiff.class, "ENTITY_DIMENSIONS");

        final StringBuilder output = new StringBuilder();
        for (final EntityType<?> type : Registries.ENTITY_TYPE) {
            final var dimensions = ((Map<EntityType<?>, Map<ProtocolVersion, EntityDimensions>>) entityDimensions).get(type);
            if (dimensions == null) {
                continue;
            }

            final EntityDimensions dimension = dimensions.values().stream().toList().getLast();
            if (dimension == null) {
                output.append("EntityType.").append(type.getName()).append(" was null");
                continue;
            }

            final EntityDimensions current = type.getDimensions();
            if (dimension.width() != current.width() || dimension.height() != current.height()) {
                output.append("EntityType.").append(type.getName()).append(" -> ").append(dimension).append(" (was ").append(current).append(")");
            }
        }
        return output;
    }

}
