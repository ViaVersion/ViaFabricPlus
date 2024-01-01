/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.util.math.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;
import org.joml.Vector3f;

/**
 * Minecraft 1.20.2 changed the calculation of the mounted height offset for all entities, this class contains the old
 * values for all entities. This class is used for 1.20.1 and lower.
 */
public class EntityRidingOffsetsPre1_20_2 {

    /**
     * Returns the mounted height offset for the given entity and passenger. This method is used for 1.20.1 and lower.
     *
     * @param entity    The entity to get the mounted height offset for.
     * @param passenger The passenger of the entity.
     * @return The mounted height offset.
     */
    public static Vector3f getMountedHeightOffset(final Entity entity, final Entity passenger) {
        float yOffset = entity.getHeight() * 0.75F;

        if (entity instanceof BoatEntity boatEntity) {
            if (!boatEntity.hasPassenger(passenger)) return new Vector3f();

            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
                yOffset = -0.3F;
                final float xOffset = MathHelper.cos(boatEntity.getYaw() * MathHelper.PI / 180F);
                final float zOffset = MathHelper.sin(boatEntity.getYaw() * MathHelper.PI / 180F);

                return new Vector3f(0.4F * xOffset, yOffset, 0.4F * zOffset);
            } else {
                if (boatEntity.isRemoved()) {
                    yOffset = 0.01F;
                } else {
                    yOffset = boatEntity.getVariant() == BoatEntity.Type.BAMBOO ? 0.25F : -0.1F;
                }

                float xOffset = boatEntity instanceof ChestBoatEntity ? 0.15F : 0F;
                if (boatEntity.getPassengerList().size() > 1) {
                    final int idx = boatEntity.getPassengerList().indexOf(passenger);
                    if (idx == 0) {
                        xOffset = 0.2F;
                    } else {
                        xOffset = -0.6F;
                    }

                    if (passenger instanceof AnimalEntity) xOffset += 0.2F;
                }

                return new Vector3f(xOffset, yOffset, 0F);
            }
        } else if (entity instanceof CamelEntity camelEntity) {
            if (!camelEntity.hasPassenger(passenger)) return new Vector3f();

            final boolean firstPassenger = camelEntity.getPassengerList().indexOf(passenger) == 0;
            yOffset = camelEntity.getDimensions(camelEntity.isSitting() ? EntityPose.SITTING : EntityPose.STANDING).height - (camelEntity.isBaby() ? 0.35F : 0.6F);
            if (camelEntity.isRemoved()) {
                yOffset = 0.01F;
            } else {
                yOffset = (float) camelEntity.getPassengerAttachmentY(firstPassenger, 0F, EntityDimensions.fixed(0F, (0.375F * camelEntity.getScaleFactor()) + yOffset), camelEntity.getScaleFactor());
            }

            float zOffset = 0.5F;
            if (camelEntity.getPassengerList().size() > 1) {
                if (!firstPassenger) zOffset = -0.7F;
                if (passenger instanceof AnimalEntity) zOffset += 0.2F;
            }

            return new Vector3f(0, yOffset, zOffset);
        } else if (entity instanceof ChickenEntity chickenEntity) {
            return new Vector3f(0, (float) (chickenEntity.getBodyY(0.5D) - chickenEntity.getY()), -0.1F);
        } else if (entity instanceof EnderDragonEntity enderDragonEntity) {
            yOffset = enderDragonEntity.body.getHeight();
        } else if (entity instanceof HoglinEntity hoglinEntity) {
            yOffset = hoglinEntity.getHeight() - (hoglinEntity.isBaby() ? 0.2F : 0.15F);
        } else if (entity instanceof LlamaEntity) {
            return new Vector3f(0, entity.getHeight() * 0.6F, -0.3F);
        } else if (entity instanceof PhantomEntity) {
            yOffset = entity.getStandingEyeHeight();
        } else if (entity instanceof PiglinEntity) {
            yOffset = entity.getHeight() * 0.92F;
        } else if (entity instanceof RavagerEntity) {
            yOffset = 2.1F;
        } else if (entity instanceof SkeletonHorseEntity) {
            yOffset -= 0.1875F;
        } else if (entity instanceof SnifferEntity) {
            yOffset = 1.8F;
        } else if (entity instanceof SpiderEntity) {
            yOffset = entity.getHeight() * 0.5F;
        } else if (entity instanceof StriderEntity striderEntity) {
            final float f = Math.min(0.25F, striderEntity.limbAnimator.getSpeed());
            final float g = striderEntity.limbAnimator.getPos();
            yOffset = striderEntity.getHeight() - 0.19F + (0.12F * MathHelper.cos(g * 1.5F) * 2F * f);
        } else if (entity instanceof ZoglinEntity zoglinEntity) {
            yOffset = zoglinEntity.getHeight() - (zoglinEntity.isBaby() ? 0.2F : 0.15F);
        } else if (entity instanceof AbstractDonkeyEntity) {
            yOffset -= 0.25F;
        } else if (entity instanceof AbstractMinecartEntity) {
            yOffset = 0F;
        }

        if (entity instanceof AbstractHorseEntity abstractHorseEntity) {
            if (abstractHorseEntity.lastAngryAnimationProgress > 0.0f) {
                return new Vector3f(0, yOffset + 0.15F * abstractHorseEntity.lastAngryAnimationProgress, -0.7F * abstractHorseEntity.lastAngryAnimationProgress);
            }
        }

        return new Vector3f(0, yOffset, 0);
    }

    /**
     * Returns the height offset for the given entity. This method is used for 1.20.1 and lower.
     *
     * @param entity The entity to get the height offset for.
     * @return The height offset.
     */
    public static double getHeightOffset(final Entity entity) {
        if (entity instanceof AllayEntity || entity instanceof VexEntity) {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
                return 0D;
            } else {
                return 0.4D;
            }
        } else if (entity instanceof ArmorStandEntity armorStandEntity) {
            return armorStandEntity.isMarker() ? 0D : 0.1D;
        } else if (entity instanceof EndermiteEntity) {
            return 0.1D;
        } else if (entity instanceof ShulkerEntity shulkerEntity) {
            final EntityType<?> vehicleType = shulkerEntity.getVehicle().getType();
            return !(shulkerEntity.getVehicle() instanceof BoatEntity) && vehicleType != EntityType.MINECART ? 0D : 0.1875D - getMountedHeightOffset(shulkerEntity.getVehicle(), null).y;
        } else if (entity instanceof SilverfishEntity) {
            return 0.1D;
        } else if (entity instanceof ZombifiedPiglinEntity zombifiedPiglinEntity) {
            return zombifiedPiglinEntity.isBaby() ? -0.05D : -0.45D;
        } else if (entity instanceof ZombieEntity zombieEntity) {
            return zombieEntity.isBaby() ? 0D : -0.45D;
        } else if (entity instanceof AnimalEntity) {
            return 0.14D;
        } else if (entity instanceof PatrolEntity) {
            return -0.45D;
        } else if (entity instanceof PlayerEntity) {
            return -0.35D;
        } else if (entity instanceof AbstractPiglinEntity abstractPiglinEntity) {
            return abstractPiglinEntity.isBaby() ? -0.05D : -0.45D;
        } else if (entity instanceof AbstractSkeletonEntity) {
            return -0.6D;
        }

        return 0D;
    }

}
