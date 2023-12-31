/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes.entity;

import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.Collections;
import java.util.Map;

import static de.florianmichael.viafabricplus.util.MapUtil.linkedHashMap;

/**
 * Data dump for entity dimension changes between versions.
 */
public class EntityDimensionReplacements {

    /**
     * A map of entity types to a map of versions to dimensions.
     */
    private static final Map<EntityType<?>, Map<VersionEnum, EntityDimensions>> ENTITY_DIMENSIONS = linkedHashMap(
            EntityType.WITHER, linkedHashMap(
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.9F, 4.0F),
                    VersionEnum.r1_8, EntityType.WITHER.getDimensions()
            ),
            EntityType.SILVERFISH, linkedHashMap(
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.3F, 0.7F),
                    VersionEnum.r1_8, EntityType.SILVERFISH.getDimensions()
            ),
            EntityType.SNOW_GOLEM, linkedHashMap(
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.4F, 1.8F),
                    VersionEnum.r1_8, EntityType.SNOW_GOLEM.getDimensions()
            ),
            EntityType.ZOMBIE, linkedHashMap(
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.6F, 1.8F),
                    VersionEnum.r1_8, EntityDimensions.fixed(EntityType.ZOMBIE.getDimensions().width, EntityType.ZOMBIE.getDimensions().height),
                    VersionEnum.r1_9, EntityType.ZOMBIE.getDimensions()
            ),
            EntityType.CHICKEN, linkedHashMap(
                    VersionEnum.b1_7tob1_7_3, EntityDimensions.changing(0.3F, 0.4F),
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.3F, 0.7F),
                    VersionEnum.r1_8, EntityType.CHICKEN.getDimensions()
            ),
            EntityType.SHEEP, linkedHashMap(
                    VersionEnum.c0_28toc0_30, EntityDimensions.changing(1.4F, 1.72F),
                    VersionEnum.a1_0_15, EntityType.SHEEP.getDimensions()
            ),
            EntityType.OCELOT, linkedHashMap(
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.6F, 0.8F),
                    VersionEnum.r1_8, EntityType.OCELOT.getDimensions()
            ),
            EntityType.BOAT, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(1.5F, 0.6F),
                    VersionEnum.r1_9, EntityType.BOAT.getDimensions()
            ),
            EntityType.CREEPER, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(0.6F, 1.8F),
                    VersionEnum.r1_9, EntityType.CREEPER.getDimensions()
            ),
            EntityType.IRON_GOLEM, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(1.4F, 2.9F),
                    VersionEnum.r1_9, EntityType.IRON_GOLEM.getDimensions()
            ),
            EntityType.SKELETON, linkedHashMap(
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.6F, 1.8F),
                    VersionEnum.r1_8, EntityDimensions.changing(0.6F, 1.95F),
                    VersionEnum.r1_9, EntityType.SKELETON.getDimensions()
            ),
            EntityType.WITHER_SKELETON, linkedHashMap(
                    VersionEnum.r1_4_6tor1_4_7, EntityDimensions.changing(0.72F, 2.16F),
                    VersionEnum.r1_7_6tor1_7_10, EntityDimensions.changing(0.72F, 2.34F),
                    VersionEnum.r1_8, EntityDimensions.changing(0.72F, 2.535F),
                    VersionEnum.r1_9, EntityType.WITHER_SKELETON.getDimensions()
            ),
            EntityType.COW, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(0.9F, 1.3F),
                    VersionEnum.r1_9, EntityType.COW.getDimensions()
            ),
            EntityType.HORSE, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(1.4F, 1.6F),
                    VersionEnum.r1_9, EntityType.HORSE.getDimensions()
            ),
            EntityType.MOOSHROOM, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(0.9F, 1.3F),
                    VersionEnum.r1_9, EntityType.MOOSHROOM.getDimensions()
            ),
            EntityType.RABBIT, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(0.6F, 0.7F),
                    VersionEnum.r1_9, EntityType.RABBIT.getDimensions()
            ),
            EntityType.SQUID, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(0.95F, 0.95F),
                    VersionEnum.r1_9, EntityType.SQUID.getDimensions()
            ),
            EntityType.VILLAGER, linkedHashMap(
                    VersionEnum.r1_8, EntityDimensions.changing(0.6F, 1.8F),
                    VersionEnum.r1_9, EntityType.VILLAGER.getDimensions()
            ),
            EntityType.WOLF, linkedHashMap(
                    VersionEnum.r1_1, EntityDimensions.changing(0.8F, 0.8F),
                    VersionEnum.r1_8, EntityDimensions.changing(0.6F, 0.8F),
                    VersionEnum.r1_9, EntityType.WOLF.getDimensions()
            ),
            EntityType.DRAGON_FIREBALL, linkedHashMap(
                    VersionEnum.r1_10, EntityDimensions.changing(0.3125F, 0.3125F),
                    VersionEnum.r1_11, EntityType.DRAGON_FIREBALL.getDimensions()
            ),
            EntityType.LEASH_KNOT, linkedHashMap(
                    VersionEnum.r1_16_4tor1_16_5, EntityDimensions.changing(0.5F, 0.5F),
                    VersionEnum.r1_17, EntityType.LEASH_KNOT.getDimensions()
            ),
            EntityType.SLIME, linkedHashMap(
                    VersionEnum.r1_13_2, EntityDimensions.changing(2F, 2F),
                    VersionEnum.r1_14, EntityType.SLIME.getDimensions()
            ),
            EntityType.MAGMA_CUBE, linkedHashMap(
                    VersionEnum.r1_13_2, EntityDimensions.changing(2F, 2F),
                    VersionEnum.r1_14, EntityType.MAGMA_CUBE.getDimensions()
            ),
            EntityType.ARROW, linkedHashMap(
                    VersionEnum.c0_28toc0_30, EntityDimensions.changing(0.3F, 0.5F),
                    VersionEnum.a1_0_15, EntityType.ARROW.getDimensions()
            )
    );

    static {
        ChangeProtocolVersionCallback.EVENT.register((oldVersion, newVersion) -> MinecraftClient.getInstance().execute(() -> ENTITY_DIMENSIONS.forEach((entityType, dimensionMap) -> {
            for (Map.Entry<VersionEnum, EntityDimensions> entry : dimensionMap.entrySet()) {
                final VersionEnum version = entry.getKey();
                final EntityDimensions dimensions = entry.getValue();
                if (oldVersion.isNewerThan(version) && newVersion.isOlderThanOrEqualTo(version)) {
                    entityType.dimensions = dimensions;
                    break;
                }
                if (newVersion.isNewerThanOrEqualTo(version) && oldVersion.isOlderThanOrEqualTo(version)) {
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
    public static Map<VersionEnum, EntityDimensions> getEntityDimensions(final EntityType<?> entityType) {
        if (!ENTITY_DIMENSIONS.containsKey(entityType)) {
            return null;
        }
        return Collections.unmodifiableMap(ENTITY_DIMENSIONS.get(entityType));
    }

    /**
     * @param entityType The {@link EntityType} to get the dimensions for.
     * @param version    The {@link VersionEnum} to get the dimensions for.
     * @return The closest dimensions for the given {@link EntityType} and {@link VersionEnum} or null if there are none.
     */
    public static EntityDimensions getEntityDimensions(final EntityType<?> entityType, final VersionEnum version) {
        final Map<VersionEnum, EntityDimensions> dimensionMap = getEntityDimensions(entityType);
        if (dimensionMap == null) {
            return null;
        }

        EntityDimensions closestDimensions = null;
        VersionEnum closestVersion = null;

        for (Map.Entry<VersionEnum, EntityDimensions> entry : dimensionMap.entrySet()) {
            final var currentVersion = entry.getKey();
            final var currentDimensions = entry.getValue();

            if (currentVersion == version) { // If the version is exactly the same, return the dimensions
                return currentDimensions;
            }

            // If the current version is closer to the version you are looking for
            if (closestVersion == null || Math.abs(version.ordinal() - currentVersion.ordinal()) < Math.abs(version.ordinal() - closestVersion.ordinal())) {
                closestVersion = currentVersion;
                closestDimensions = currentDimensions;
            }
        }

        return closestDimensions;
    }

}
