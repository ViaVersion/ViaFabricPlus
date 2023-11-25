/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.*;
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

public class EntityHeightOffsetsPre1_20_2 {

    public static Vector3f getMountedHeightOffset(final Entity entity, final Entity passenger) {
        double yOffset = entity.getHeight() * 0.75;

        if (entity instanceof LlamaEntity llamaEntity) {
            yOffset = entity.getHeight() * 0.6;

            final float xOffset = MathHelper.sin(llamaEntity.bodyYaw * 0.017453292F);
            final float zOffset = MathHelper.cos(llamaEntity.bodyYaw * 0.017453292F);

            return new Vector3f(0.3F * xOffset, (float) yOffset, -(0.3F * zOffset));
        } else if (entity instanceof CamelEntity camelEntity) {
            yOffset = entity.getDimensions(camelEntity.isSitting() ? EntityPose.SITTING : EntityPose.STANDING).height - (camelEntity.isBaby() ? 0.35F : 0.6F);

            final int passengerIndex = camelEntity.getPassengerList().indexOf(passenger);
            final boolean firstIndex = passengerIndex == 0;
            if (passengerIndex >= 0) {
                float zOffset = 0.5f;
                if (camelEntity.isRemoved()) {
                    yOffset = 0.01f;
                } else {
                    final var fakeDimension = EntityDimensions.fixed(0F, (0.375F * camelEntity.getScaleFactor()) + (float) yOffset); // Reverts original calculation to set yOffset to our field
                    yOffset = camelEntity.getPassengerAttachmentY(firstIndex, 0.0f, fakeDimension, camelEntity.getScaleFactor());
                }

                if (camelEntity.getPassengerList().size() > 1) {
                    if (!firstIndex) zOffset = -0.7f;
                    if (passenger instanceof AnimalEntity) zOffset += 0.2f;
                }

                return new Vector3f(0, (float) yOffset, zOffset);
            } else {
                return new Vector3f();
            }
        } else if (entity instanceof SnifferEntity) {
            yOffset = 1.8;
        } else if (entity instanceof EnderDragonEntity enderDragonEntity) {
            yOffset = enderDragonEntity.body.getHeight();
        } else if (entity instanceof PiglinEntity) {
            yOffset = entity.getHeight() * 0.92;
        } else if (entity instanceof HoglinEntity hoglinEntity) {
            yOffset = entity.getHeight() - (hoglinEntity.isBaby() ? 0.2 : 0.15);
        } else if (entity instanceof SkeletonHorseEntity) {
            yOffset -= 0.1875;
        } else if (entity instanceof PhantomEntity) {
            yOffset = entity.getEyeHeight(entity.getPose());
        } else if (entity instanceof RavagerEntity) {
            yOffset = 2.1;
        } else if (entity instanceof ZoglinEntity zoglinEntity) {
            yOffset = (double) entity.getHeight() - (zoglinEntity.isBaby() ? 0.2 : 0.15);
        } else if (entity instanceof BoatEntity boatEntity) {
            final var version = ProtocolHack.getTargetVersion();
            if (version.isOlderThanOrEqualTo(VersionEnum.r1_8)) {
                yOffset = -0.3;
            } else {
                yOffset = boatEntity.getVariant() == BoatEntity.Type.BAMBOO ? 0.25 : -0.1;
            }

            if (boatEntity.hasPassenger(passenger)) {
                float xOffset = (boatEntity instanceof ChestBoatEntity) ? 0.15F : 0.0F;
                yOffset = (boatEntity.isRemoved() ? (double) 0.01f : yOffset);

                if (boatEntity.getPassengerList().size() > 1) {
                    int i = boatEntity.getPassengerList().indexOf(passenger);
                    xOffset = i == 0 ? 0.2f : -0.6f;
                    if (passenger instanceof AnimalEntity) {
                        xOffset += 0.2f;
                    }
                }

                return new Vector3f(xOffset, (float) yOffset, 0.0F);
            } else {
                return new Vector3f();
            }
        } else if (entity instanceof StriderEntity striderEntity) {
            final var var1 = Math.min(0.25F, striderEntity.limbAnimator.getSpeed());
            final var var2 = striderEntity.limbAnimator.getPos();

            yOffset = (double) striderEntity.getHeight() - 0.19 + (double) (0.12F * MathHelper.cos(var2 * 1.5F) * 2.0F * var1);
        } else if (entity instanceof SpiderEntity) {
            yOffset = entity.getHeight() * 0.5F;
        } else if (entity instanceof ChickenEntity chickenEntity) {
            final var xOffset = MathHelper.sin(chickenEntity.bodyYaw * (MathHelper.PI / 180));
            final var zOffset = MathHelper.cos(chickenEntity.bodyYaw * (MathHelper.PI / 180));

            return new Vector3f(0.1f * xOffset, (float) (chickenEntity.getBodyY(0.5) - chickenEntity.getY()), -(0.1f * zOffset));
        }

        if (entity instanceof AbstractDonkeyEntity) {
            yOffset -= 0.25;
        } else if (entity instanceof AbstractMinecartEntity) {
            yOffset = 0.0;
        } else if (entity instanceof AbstractHorseEntity abstractHorseEntity) {
            if (abstractHorseEntity.lastAngryAnimationProgress > 0.0f) {
                final float xOffset = MathHelper.sin(abstractHorseEntity.bodyYaw * ((float) Math.PI / 180));
                final float zOffset = MathHelper.cos(abstractHorseEntity.bodyYaw * ((float) Math.PI / 180));

                final float xzFactor = 0.7F * abstractHorseEntity.lastAngryAnimationProgress;

                return new Vector3f(xzFactor * xOffset, (float) (yOffset + 0.15F * abstractHorseEntity.lastAngryAnimationProgress), xzFactor * zOffset);
            }
        }

        return new Vector3f(0.0F, (float) yOffset, 0.0F);
    }

    public static double getHeightOffset(final Entity entity) {
        if (entity instanceof AllayEntity) {
            return ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4) ? 0 : 0.4;
        } else if (entity instanceof ArmorStandEntity armorStandEntity && !armorStandEntity.isMarker()) {
            return 0.1;
        } else if (entity instanceof EndermiteEntity) {
            return 0.1;
        } else if (entity instanceof ShulkerEntity shulkerEntity) {
            final var vehicleType = shulkerEntity.getVehicle().getType();

            return !(shulkerEntity.getVehicle() instanceof BoatEntity) && vehicleType != EntityType.MINECART ? 0 : 0.1875 - getMountedHeightOffset(shulkerEntity.getVehicle(), null).y;
        } else if (entity instanceof SilverfishEntity) {
            return 0.1;
        } else if (entity instanceof VexEntity) {
            return 0.4;
        } else if (entity instanceof ZombifiedPiglinEntity zombifiedPiglinEntity) {
            return zombifiedPiglinEntity.isBaby() ? -0.05 : -0.45;
        } else if (entity instanceof ZombieEntity zombieEntity) {
            return zombieEntity.isBaby() ? 0.0 : -0.45;
        }

        if (entity instanceof PlayerEntity) {
            return -0.35;
        } else if (entity instanceof PatrolEntity) {
            return -0.45;
        } else if (entity instanceof AbstractPiglinEntity abstractPiglinEntity) {
            return abstractPiglinEntity.isBaby() ? -0.05 : -0.45;
        } else if (entity instanceof AbstractSkeletonEntity) {
            return -0.6;
        } else if (entity instanceof AnimalEntity) {
            return 0.14;
        }
        return 0;
    }
}
