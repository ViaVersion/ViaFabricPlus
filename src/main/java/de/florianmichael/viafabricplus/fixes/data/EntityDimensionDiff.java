/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.fixes.data;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.Collections;
import java.util.Map;

import static de.florianmichael.viafabricplus.util.MapUtil.linkedHashMap;

/**
 * Data dump for entity dimension changes between versions.
 */
public class EntityDimensionDiff {

    /**
     * A map of entity types to a map of versions to dimensions.
     */
    private static final Map<EntityType<?>, Map<ProtocolVersion, EntityDimensions>> ENTITY_DIMENSIONS = linkedHashMap(
            EntityType.WITHER, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.9F, 4.0F),
                    ProtocolVersion.v1_8, EntityType.WITHER.getDimensions()
            ),
            EntityType.SILVERFISH, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.3F, 0.7F),
                    ProtocolVersion.v1_8, EntityType.SILVERFISH.getDimensions()
            ),
            EntityType.SNOW_GOLEM, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.4F, 1.8F),
                    ProtocolVersion.v1_8, EntityType.SNOW_GOLEM.getDimensions()
            ),
            EntityType.ZOMBIE, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.6F, 1.8F),
                    ProtocolVersion.v1_8, EntityDimensions.fixed(EntityType.ZOMBIE.getDimensions().width, EntityType.ZOMBIE.getDimensions().height),
                    ProtocolVersion.v1_9, EntityType.ZOMBIE.getDimensions()
            ),
            EntityType.CHICKEN, linkedHashMap(
                    LegacyProtocolVersion.b1_7tob1_7_3, EntityDimensions.changing(0.3F, 0.4F),
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.3F, 0.7F),
                    ProtocolVersion.v1_8, EntityType.CHICKEN.getDimensions()
            ),
            EntityType.SHEEP, linkedHashMap(
                    LegacyProtocolVersion.c0_28toc0_30, EntityDimensions.changing(1.4F, 1.72F),
                    LegacyProtocolVersion.a1_0_15, EntityType.SHEEP.getDimensions()
            ),
            EntityType.OCELOT, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.6F, 0.8F),
                    ProtocolVersion.v1_8, EntityType.OCELOT.getDimensions()
            ),
            EntityType.BOAT, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(1.5F, 0.6F),
                    ProtocolVersion.v1_9, EntityType.BOAT.getDimensions()
            ),
            EntityType.CREEPER, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.6F, 1.8F),
                    ProtocolVersion.v1_9, EntityType.CREEPER.getDimensions()
            ),
            EntityType.IRON_GOLEM, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(1.4F, 2.9F),
                    ProtocolVersion.v1_9, EntityType.IRON_GOLEM.getDimensions()
            ),
            EntityType.SKELETON, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.6F, 1.8F),
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.6F, 1.95F),
                    ProtocolVersion.v1_9, EntityType.SKELETON.getDimensions()
            ),
            EntityType.WITHER_SKELETON, linkedHashMap(
                    LegacyProtocolVersion.r1_4_6tor1_4_7, EntityDimensions.changing(0.72F, 2.16F),
                    ProtocolVersion.v1_7_6, EntityDimensions.changing(0.72F, 2.34F),
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.72F, 2.535F),
                    ProtocolVersion.v1_9, EntityType.WITHER_SKELETON.getDimensions()
            ),
            EntityType.COW, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.9F, 1.3F),
                    ProtocolVersion.v1_9, EntityType.COW.getDimensions()
            ),
            EntityType.HORSE, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(1.4F, 1.6F),
                    ProtocolVersion.v1_9, EntityType.HORSE.getDimensions()
            ),
            EntityType.MOOSHROOM, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.9F, 1.3F),
                    ProtocolVersion.v1_9, EntityType.MOOSHROOM.getDimensions()
            ),
            EntityType.RABBIT, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.6F, 0.7F),
                    ProtocolVersion.v1_9, EntityType.RABBIT.getDimensions()
            ),
            EntityType.SQUID, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.95F, 0.95F),
                    ProtocolVersion.v1_9, EntityType.SQUID.getDimensions()
            ),
            EntityType.VILLAGER, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.6F, 1.8F),
                    ProtocolVersion.v1_9, EntityType.VILLAGER.getDimensions()
            ),
            EntityType.WOLF, linkedHashMap(
                    LegacyProtocolVersion.r1_1, EntityDimensions.changing(0.8F, 0.8F),
                    ProtocolVersion.v1_8, EntityDimensions.changing(0.6F, 0.8F),
                    ProtocolVersion.v1_9, EntityType.WOLF.getDimensions()
            ),
            EntityType.DRAGON_FIREBALL, linkedHashMap(
                    ProtocolVersion.v1_10, EntityDimensions.changing(0.3125F, 0.3125F),
                    ProtocolVersion.v1_11, EntityType.DRAGON_FIREBALL.getDimensions()
            ),
            EntityType.LEASH_KNOT, linkedHashMap(
                    ProtocolVersion.v1_16_4, EntityDimensions.changing(0.5F, 0.5F),
                    ProtocolVersion.v1_17, EntityType.LEASH_KNOT.getDimensions()
            ),
            EntityType.SLIME, linkedHashMap(
                    ProtocolVersion.v1_13_2, EntityDimensions.changing(2F, 2F),
                    ProtocolVersion.v1_14, EntityType.SLIME.getDimensions()
            ),
            EntityType.MAGMA_CUBE, linkedHashMap(
                    ProtocolVersion.v1_13_2, EntityDimensions.changing(2F, 2F),
                    ProtocolVersion.v1_14, EntityType.MAGMA_CUBE.getDimensions()
            ),
            EntityType.ARROW, linkedHashMap(
                    LegacyProtocolVersion.c0_28toc0_30, EntityDimensions.changing(0.3F, 0.5F),
                    LegacyProtocolVersion.a1_0_15, EntityType.ARROW.getDimensions()
            )
    );

    static {
        ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> ENTITY_DIMENSIONS.forEach((entityType, dimensionMap) -> {
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

    public static void init() {
        // Loads the class and triggers the static initializer.
    }

    /**
     * @param entityType The {@link EntityType} to get the dimensions for.
     * @return The dimensions for the given {@link EntityType} or null if there are none. The map is unmodifiable.
     */
    public static Map<ProtocolVersion, EntityDimensions> getEntityDimensions(final EntityType<?> entityType) {
        if (!ENTITY_DIMENSIONS.containsKey(entityType)) {
            return null;
        }
        return Collections.unmodifiableMap(ENTITY_DIMENSIONS.get(entityType));
    }

    /**
     * @param entityType The {@link EntityType} to get the dimensions for.
     * @param version    The {@link ProtocolVersion} to get the dimensions for.
     * @return The closest dimensions for the given {@link EntityType} and {@link ProtocolVersion} or null if there are none.
     */
    public static EntityDimensions getEntityDimensions(final EntityType<?> entityType, final ProtocolVersion version) {
        final Map<ProtocolVersion, EntityDimensions> dimensionMap = getEntityDimensions(entityType);
        if (dimensionMap == null) {
            return null;
        }

        EntityDimensions closestDimensions = null;
        ProtocolVersion closestVersion = null;

        for (Map.Entry<ProtocolVersion, EntityDimensions> entry : dimensionMap.entrySet()) {
            final var currentVersion = entry.getKey();
            final var currentDimensions = entry.getValue();

            if (currentVersion == version) { // If the version is exactly the same, return the dimensions
                return currentDimensions;
            }

            // If the current version is closer to the version you are looking for
            // TODO: Fix
            /*if (closestVersion == null || Math.abs(version.ordinal() - currentVersion.ordinal()) < Math.abs(version.ordinal() - closestVersion.ordinal())) {
                closestVersion = currentVersion;
                closestDimensions = currentDimensions;
            }*/
        }

        return closestDimensions;
    }

}
