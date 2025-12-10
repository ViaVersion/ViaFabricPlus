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

package com.viaversion.viafabricplus.features.entity.riding_offset;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

/**
 * Minecraft 1.20.2 changed the calculation of the mounted height offset for all entities, this class contains the old
 * values for all entities. This class is used for 1.20.1 and lower.
 */
public final class EntityRidingOffsetsPre1_20_2 {

    /**
     * Returns the mounted height offset for the given entity and passenger. This method is used for 1.20.1 and lower.
     *
     * @param entity    The entity to get the mounted height offset for.
     * @param passenger The passenger of the entity.
     * @return The mounted height offset.
     */
    public static Vec3 getMountedHeightOffset(final Entity entity, final Entity passenger) {
        double yOffset = entity.getBbHeight() * 0.75F;

        if (entity instanceof AbstractBoat abstractBoatEntity) {
            if (!abstractBoatEntity.hasPassenger(passenger)) return Vec3.ZERO;

            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
                yOffset = -0.3F;
                final double xOffset = Mth.cos(abstractBoatEntity.getYRot() * Mth.PI / 180F);
                final double zOffset = Mth.sin(abstractBoatEntity.getYRot() * Mth.PI / 180F);

                return new Vec3(0.4F * xOffset, yOffset, 0.4F * zOffset);
            } else {
                if (abstractBoatEntity.isRemoved()) {
                    yOffset = 0.01F;
                } else {
                    yOffset = abstractBoatEntity.getType() == EntityType.BAMBOO_RAFT || abstractBoatEntity.getType() == EntityType.BAMBOO_CHEST_RAFT ? 0.25F : -0.1F;
                }

                double xOffset = abstractBoatEntity instanceof AbstractChestBoat ? 0.15F : 0F;
                if (abstractBoatEntity.getPassengers().size() > 1) {
                    final int idx = abstractBoatEntity.getPassengers().indexOf(passenger);
                    if (idx == 0) {
                        xOffset = 0.2F;
                    } else {
                        xOffset = -0.6F;
                    }

                    if (passenger instanceof Animal) xOffset += 0.2F;
                }

                return new Vec3(xOffset, yOffset, 0F).yRot(-(float) (Math.PI / 2));
            }
        } else if (entity instanceof Camel camelEntity) {
            if (!camelEntity.hasPassenger(passenger)) return Vec3.ZERO;

            final boolean firstPassenger = camelEntity.getPassengers().indexOf(passenger) == 0;
            yOffset = camelEntity.getDimensions(camelEntity.isCamelSitting() ? Pose.SITTING : Pose.STANDING).height() - (camelEntity.isBaby() ? 0.35F : 0.6F);
            if (camelEntity.isRemoved()) {
                yOffset = 0.01F;
            } else {
                yOffset = camelEntity.getBodyAnchorAnimationYOffset(firstPassenger, 0F, EntityDimensions.fixed(0F, (float) ((0.375F * camelEntity.getAgeScale()) + yOffset)), camelEntity.getAgeScale());
            }

            double zOffset = 0.5F;
            if (camelEntity.getPassengers().size() > 1) {
                if (!firstPassenger) zOffset = -0.7F;
                if (passenger instanceof Animal) zOffset += 0.2F;
            }

            return new Vec3(0, yOffset, zOffset);
        } else if (entity instanceof Chicken chickenEntity) {
            return new Vec3(0, chickenEntity.getY(0.5D) - chickenEntity.getY(), -0.1F);
        } else if (entity instanceof EnderDragon enderDragonEntity) {
            yOffset = enderDragonEntity.body.getBbHeight();
        } else if (entity instanceof Hoglin hoglinEntity) {
            yOffset = hoglinEntity.getBbHeight() - (hoglinEntity.isBaby() ? 0.2F : 0.15F);
        } else if (entity instanceof Llama) {
            return new Vec3(0, entity.getBbHeight() * 0.6F, -0.3F);
        } else if (entity instanceof Phantom) {
            yOffset = entity.getEyeHeight();
        } else if (entity instanceof Piglin) {
            yOffset = entity.getBbHeight() * 0.92F;
        } else if (entity instanceof Ravager) {
            yOffset = 2.1F;
        } else if (entity instanceof SkeletonHorse) {
            yOffset -= 0.1875F;
        } else if (entity instanceof Sniffer) {
            yOffset = 1.8F;
        } else if (entity instanceof Spider) {
            yOffset = entity.getBbHeight() * 0.5F;
        } else if (entity instanceof Strider striderEntity) {
            final float speed = Math.min(0.25F, striderEntity.walkAnimation.speed());
            final float animationProgress = striderEntity.walkAnimation.position();
            yOffset = striderEntity.getBbHeight() - 0.19F + (0.12F * Mth.cos(animationProgress * 1.5F) * 2F * speed);
        } else if (entity instanceof Zoglin zoglinEntity) {
            yOffset = zoglinEntity.getBbHeight() - (zoglinEntity.isBaby() ? 0.2F : 0.15F);
        } else if (entity instanceof AbstractChestedHorse) {
            yOffset -= 0.25F;
        } else if (entity instanceof AbstractMinecart) {
            yOffset = 0F;
        }

        if (entity instanceof AbstractHorse abstractHorseEntity) {
            if (abstractHorseEntity.standAnimO > 0.0F) {
                return new Vec3(0, yOffset + 0.15F * abstractHorseEntity.standAnimO, -0.7F * abstractHorseEntity.standAnimO);
            }
        }

        return new Vec3(0, yOffset, 0);
    }

    /**
     * Returns the height offset for the given entity. This method is used for 1.20.1 and lower.
     *
     * @param entity The entity to get the height offset for.
     * @return The height offset.
     */
    public static double getHeightOffset(final Entity entity) {
        if (entity instanceof Allay || entity instanceof Vex) {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
                return 0D;
            } else {
                return 0.4D;
            }
        } else if (entity instanceof ArmorStand armorStandEntity) {
            return armorStandEntity.isMarker() ? 0D : 0.1D;
        } else if (entity instanceof Endermite) {
            return 0.1D;
        } else if (entity instanceof Shulker shulkerEntity) {
            final EntityType<?> vehicleType = shulkerEntity.getVehicle().getType();
            return !(shulkerEntity.getVehicle() instanceof Boat) && vehicleType != EntityType.MINECART ? 0D : 0.1875D - getMountedHeightOffset(shulkerEntity.getVehicle(), null).y;
        } else if (entity instanceof Silverfish) {
            return 0.1D;
        } else if (entity instanceof ZombifiedPiglin zombifiedPiglinEntity) {
            return zombifiedPiglinEntity.isBaby() ? -0.05D : -0.45D;
        } else if (entity instanceof Zombie zombieEntity) {
            return zombieEntity.isBaby() ? 0D : -0.45D;
        } else if (entity instanceof Animal) {
            return 0.14D;
        } else if (entity instanceof PatrollingMonster) {
            return -0.45D;
        } else if (entity instanceof Player) {
            return -0.35D;
        } else if (entity instanceof AbstractPiglin abstractPiglinEntity) {
            return abstractPiglinEntity.isBaby() ? -0.05D : -0.45D;
        } else if (entity instanceof AbstractSkeleton) {
            return -0.6D;
        }

        return 0D;
    }

}
