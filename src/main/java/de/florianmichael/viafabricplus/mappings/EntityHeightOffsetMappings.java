/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.mappings;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.raphimc.vialoader.util.VersionEnum;

public class EntityHeightOffsetMappings {

    public static double getMountedHeightOffset(final Entity entity) {
        double offset = entity.getHeight() * 0.75;

        if (entity instanceof LlamaEntity) {
            offset = entity.getHeight() * 0.6;
        } else if (entity instanceof CamelEntity camelEntity) {
            offset = entity.getDimensions(camelEntity.isSitting() ? EntityPose.SITTING : EntityPose.STANDING).height - (camelEntity.isBaby() ? 0.35F : 0.6F);
        } else if (entity instanceof SnifferEntity) {
            offset = 1.8;
        } else if (entity instanceof EnderDragonEntity enderDragonEntity) {
            offset = enderDragonEntity.body.getHeight();
        } else if (entity instanceof PiglinEntity) {
            offset = entity.getHeight() * 0.92;
        } else if (entity instanceof HoglinEntity hoglinEntity) {
            offset = entity.getHeight() - (hoglinEntity.isBaby() ? 0.2 : 0.15);
        } else if (entity instanceof SkeletonHorseEntity) {
            offset -= 0.1875;
        } else if (entity instanceof PhantomEntity) {
            offset = entity.getEyeHeight(entity.getPose());
        } else if (entity instanceof RavagerEntity) {
            offset = 2.1;
        } else if (entity instanceof ZoglinEntity zoglinEntity) {
            offset = (double) entity.getHeight() - (zoglinEntity.isBaby() ? 0.2 : 0.15);
        } else if (entity instanceof BoatEntity boatEntity) {
            final var version = ProtocolHack.getTargetVersion();
            if (version.isOlderThanOrEqualTo(VersionEnum.r1_8)) {
                offset = -0.3;
            } else {
                offset = boatEntity.getVariant() == BoatEntity.Type.BAMBOO ? version.isOlderThanOrEqualTo(VersionEnum.r1_19_4) ? 0.3 : 0.25 : -0.1;
            }
        } else if (entity instanceof StriderEntity striderEntity) {
            float var1 = Math.min(0.25F, striderEntity.limbAnimator.getSpeed());
            float var2 = striderEntity.limbAnimator.getPos();
            offset = (double) striderEntity.getHeight() - 0.19 + (double) (0.12F * MathHelper.cos(var2 * 1.5F) * 2.0F * var1);
        } else if (entity instanceof SpiderEntity) {
            offset = entity.getHeight() * 0.5F;
        }

        if (entity instanceof AbstractDonkeyEntity) {
            offset -= 0.25;
        } else if (entity instanceof AbstractMinecartEntity) {
            offset = 0.0;
        }

        return offset;
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

            return !(shulkerEntity.getVehicle() instanceof BoatEntity) && vehicleType != EntityType.MINECART ? 0 : 0.1875 - getMountedHeightOffset(shulkerEntity.getVehicle());
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
