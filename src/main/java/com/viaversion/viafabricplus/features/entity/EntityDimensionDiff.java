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

package com.viaversion.viafabricplus.features.entity;

import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Data dump for entity dimension changes between versions.
 */
public final class EntityDimensionDiff {

    /**
     * A map of entity types to a map of versions to dimensions.
     */
    private static final Map<EntityType<?>, Map<ProtocolVersion, EntityDimensions>> ENTITY_DIMENSIONS = new HashMap<>();

    public static void init() {
        final JsonObject dimensionDiff = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("entity-dimensions.json");
        for (final String entity : dimensionDiff.keySet()) {
            final EntityType<?> entityType = Registries.ENTITY_TYPE.getOptionalValue(Identifier.of(entity)).orElse(null);
            if (entityType == null) {
                throw new IllegalStateException("Unknown entity: " + entity);
            }

            final JsonObject versions = dimensionDiff.getAsJsonObject(entity);
            final Map<ProtocolVersion, EntityDimensions> dimensionMap = new HashMap<>();
            for (final String version : versions.keySet()) {
                final ProtocolVersion protocolVersion = ProtocolVersion.getClosest(version);
                if (protocolVersion == null) {
                    throw new IllegalStateException("Unknown protocol version: " + version);
                }

                final JsonObject dimensionData = versions.getAsJsonObject(version);
                final float width = dimensionData.get("width").getAsFloat();
                final float height = dimensionData.get("height").getAsFloat();
                final float eyeHeight = dimensionData.get("eyeHeight").getAsFloat();
                final boolean fixed = dimensionData.get("fixed").getAsBoolean();
                final EntityDimensions entityDimensions = new EntityDimensions(width, height, eyeHeight, entityType.dimensions.attachments(), fixed);
                dimensionMap.put(protocolVersion, entityDimensions);
            }
            ENTITY_DIMENSIONS.put(entityType, dimensionMap);
        }

        Events.CHANGE_PROTOCOL_VERSION.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> ENTITY_DIMENSIONS.forEach((entityType, dimensionMap) -> {
            for (Map.Entry<ProtocolVersion, EntityDimensions> entry : dimensionMap.entrySet()) {
                final ProtocolVersion version = entry.getKey();
                final EntityDimensions dimensions = entry.getValue();
                if (oldVersion.newerThan(version) && newVersion.olderThanOrEqualTo(version)) {
                    entityType.dimensions = dimensions;
                    break;
                }
                if (newVersion.newerThanOrEqualTo(version) && oldVersion.olderThanOrEqualTo(version)) {
                    entityType.dimensions = dimensions;
                }
            }
        })));
    }

}
