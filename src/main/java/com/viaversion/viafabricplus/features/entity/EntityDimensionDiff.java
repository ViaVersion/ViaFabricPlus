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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import static com.viaversion.viafabricplus.util.MapUtil.linkedHashMap;

/**
 * Data dump for entity dimension changes between versions.
 */
public final class EntityDimensionDiff {

    /**
     * A map of entity types to a map of versions to dimensions.
     */
    private static final Map<EntityType<?>, Map<ProtocolVersion, EntityDimensions>> ENTITY_DIMENSIONS = linkedHashMap(
            EntityType.WITHER, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.WITHER).withChangingDimensions(0.9F, 4.0F).build(),
                    ProtocolVersion.v1_8, EntityType.WITHER.getDimensions()
            ),
            EntityType.SILVERFISH, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.SILVERFISH).withChangingDimensions(0.3F, 0.7F).build(),
                    ProtocolVersion.v1_8, EntityType.SILVERFISH.getDimensions()
            ),
            EntityType.SNOW_GOLEM, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.SNOW_GOLEM).withChangingDimensions(0.4F, 1.8F).build(),
                    ProtocolVersion.v1_8, EntityType.SNOW_GOLEM.getDimensions()
            ),
            EntityType.ZOMBIE, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.ZOMBIE).withChangingDimensions(0.6F, 1.8F).build(),
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.ZOMBIE).withFixedDimensions(0.6F, 1.95F).build(),
                    ProtocolVersion.v1_9, EntityType.ZOMBIE.getDimensions()
            ),
            EntityType.CHICKEN, linkedHashMap(
                    LegacyProtocolVersion.b1_7tob1_7_3, EntityDimensionsBuilder.create(EntityType.CHICKEN).withChangingDimensions(0.3F, 0.4F).build(),
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.CHICKEN).withChangingDimensions(0.3F, 0.7F).build(),
                    ProtocolVersion.v1_8, EntityType.CHICKEN.getDimensions()
            ),
            EntityType.SHEEP, linkedHashMap(
                    LegacyProtocolVersion.c0_28toc0_30, EntityDimensionsBuilder.create(EntityType.SHEEP).withChangingDimensions(1.4F, 1.72F).build(),
                    LegacyProtocolVersion.a1_0_15, EntityType.SHEEP.getDimensions()
            ),
            EntityType.OCELOT, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.OCELOT).withChangingDimensions(0.6F, 0.8F).build(),
                    ProtocolVersion.v1_8, EntityType.OCELOT.getDimensions()
            ),
            EntityType.OAK_BOAT, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.OAK_BOAT).withChangingDimensions(1.5F, 0.6F).build(),
                    ProtocolVersion.v1_9, EntityType.OAK_BOAT.getDimensions()
            ),
            EntityType.CREEPER, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.CREEPER).withChangingDimensions(0.6F, 1.8F).build(),
                    ProtocolVersion.v1_9, EntityType.CREEPER.getDimensions()
            ),
            EntityType.IRON_GOLEM, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.IRON_GOLEM).withChangingDimensions(1.4F, 2.9F).build(),
                    ProtocolVersion.v1_9, EntityType.IRON_GOLEM.getDimensions()
            ),
            EntityType.SKELETON, linkedHashMap(
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.SKELETON).withChangingDimensions(0.6F, 1.8F).build(),
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.SKELETON).withChangingDimensions(0.6F, 1.95F).build(),
                    ProtocolVersion.v1_9, EntityType.SKELETON.getDimensions()
            ),
            EntityType.WITHER_SKELETON, linkedHashMap(
                    LegacyProtocolVersion.r1_4_6tor1_4_7, EntityDimensionsBuilder.create(EntityType.WITHER_SKELETON).withChangingDimensions(0.72F, 2.16F).build(),
                    ProtocolVersion.v1_7_6, EntityDimensionsBuilder.create(EntityType.WITHER_SKELETON).withChangingDimensions(0.72F, 2.34F).build(),
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.WITHER_SKELETON).withChangingDimensions(0.72F, 2.535F).build(),
                    ProtocolVersion.v1_9, EntityType.WITHER_SKELETON.getDimensions()
            ),
            EntityType.COW, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.COW).withChangingDimensions(0.9F, 1.3F).build(),
                    ProtocolVersion.v1_9, EntityType.COW.getDimensions()
            ),
            EntityType.HORSE, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.HORSE).withChangingDimensions(1.4F, 1.6F).build(),
                    ProtocolVersion.v1_9, EntityType.HORSE.getDimensions()
            ),
            EntityType.MOOSHROOM, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.MOOSHROOM).withChangingDimensions(0.9F, 1.3F).build(),
                    ProtocolVersion.v1_9, EntityType.MOOSHROOM.getDimensions()
            ),
            EntityType.RABBIT, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.RABBIT).withChangingDimensions(0.6F, 0.7F).build(),
                    ProtocolVersion.v1_9, EntityType.RABBIT.getDimensions()
            ),
            EntityType.SQUID, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.SQUID).withChangingDimensions(0.95F, 0.95F).build(),
                    ProtocolVersion.v1_9, EntityType.SQUID.getDimensions()
            ),
            EntityType.VILLAGER, linkedHashMap(
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.VILLAGER).withChangingDimensions(0.6F, 1.8F).build(),
                    ProtocolVersion.v1_9, EntityType.VILLAGER.getDimensions()
            ),
            EntityType.WOLF, linkedHashMap(
                    LegacyProtocolVersion.r1_1, EntityDimensionsBuilder.create(EntityType.WOLF).withChangingDimensions(0.8F, 0.8F).build(),
                    ProtocolVersion.v1_8, EntityDimensionsBuilder.create(EntityType.WOLF).withChangingDimensions(0.6F, 0.8F).build(),
                    ProtocolVersion.v1_9, EntityType.WOLF.getDimensions()
            ),
            EntityType.DRAGON_FIREBALL, linkedHashMap(
                    ProtocolVersion.v1_10, EntityDimensionsBuilder.create(EntityType.DRAGON_FIREBALL).withChangingDimensions(0.3125F, 0.3125F).build(),
                    ProtocolVersion.v1_11, EntityType.DRAGON_FIREBALL.getDimensions()
            ),
            EntityType.LEASH_KNOT, linkedHashMap(
                    ProtocolVersion.v1_16_4, EntityDimensionsBuilder.create(EntityType.LEASH_KNOT).withChangingDimensions(0.5F, 0.5F).build(),
                    ProtocolVersion.v1_17, EntityType.LEASH_KNOT.getDimensions()
            ),
            EntityType.SLIME, linkedHashMap(
                    ProtocolVersion.v1_13_2, EntityDimensionsBuilder.create(EntityType.SLIME).withChangingDimensions(2F * 0.255F, 2F * 0.255F).build(),
                    ProtocolVersion.v1_14, EntityDimensionsBuilder.create(EntityType.SLIME).withChangingDimensions(2.04F * 0.255F, 2.04F * 0.255F).build(),
                    ProtocolVersion.v1_20_5, EntityType.SLIME.getDimensions()
            ),
            EntityType.MAGMA_CUBE, linkedHashMap(
                    ProtocolVersion.v1_13_2, EntityDimensionsBuilder.create(EntityType.MAGMA_CUBE).withChangingDimensions(2F * 0.255F, 2F * 0.255F).build(),
                    ProtocolVersion.v1_14, EntityDimensionsBuilder.create(EntityType.MAGMA_CUBE).withChangingDimensions(2.04F * 0.255F, 2.04F * 0.255F).build(),
                    ProtocolVersion.v1_20_5, EntityType.MAGMA_CUBE.getDimensions()
            ),
            EntityType.ARROW, linkedHashMap(
                    LegacyProtocolVersion.c0_28toc0_30, EntityDimensionsBuilder.create(EntityType.ARROW).withChangingDimensions(0.3F, 0.5F).build(),
                    LegacyProtocolVersion.a1_0_15, EntityType.ARROW.getDimensions()
            )
    );

    static {
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

    public static void init() {
        // Calls the static block
    }

    private static class EntityDimensionsBuilder {

        private EntityDimensions entityDimensions;
        private EntityAttachments.Builder attachments = EntityAttachments.builder();

        public static EntityDimensionsBuilder create() {
            return new EntityDimensionsBuilder();
        }

        public static EntityDimensionsBuilder create(final EntityType<?> template) {
            final EntityDimensionsBuilder entityDimensionsBuilder = new EntityDimensionsBuilder();
            entityDimensionsBuilder.entityDimensions = template.getDimensions();
            return entityDimensionsBuilder;
        }

        public EntityDimensionsBuilder withChangingDimensions(final float width, final float height) {
            this.entityDimensions = new EntityDimensions(width, height, this.entityDimensions.eyeHeight(), this.entityDimensions.attachments(), false);
            return this;
        }

        public EntityDimensionsBuilder withFixedDimensions(final float width, final float height) {
            this.entityDimensions = new EntityDimensions(width, height, this.entityDimensions.eyeHeight(), this.entityDimensions.attachments(), true);
            return this;
        }

        public EntityDimensionsBuilder withEyeHeight(final float eyeHeight) {
            this.entityDimensions = this.entityDimensions.withEyeHeight(eyeHeight);
            return this;
        }

        public EntityDimensionsBuilder withPassengerAttachments(final float... offsetYs) {
            for (float f : offsetYs) {
                this.attachments = this.attachments.add(EntityAttachmentType.PASSENGER, 0.0F, f, 0.0F);
            }
            this.entityDimensions = this.entityDimensions.withAttachments(this.attachments);

            return this;
        }

        public EntityDimensions build() {
            return this.entityDimensions;
        }

    }

}
