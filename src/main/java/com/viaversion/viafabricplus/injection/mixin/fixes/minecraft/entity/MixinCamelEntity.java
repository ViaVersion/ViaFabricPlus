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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CamelEntity.class)
public abstract class MixinCamelEntity extends AbstractHorseEntity {

    public MixinCamelEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20) && this.getControllingPassenger() != passenger) {
            this.viaFabricPlus$clampPassengerYaw1_20_1(passenger);
        }
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        super.updatePassengerPosition(passenger, positionUpdater);

        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20)) {
            this.viaFabricPlus$clampPassengerYaw1_20_1(passenger);
        }
    }

    @Unique
    private void viaFabricPlus$clampPassengerYaw1_20_1(final Entity passenger) {
        passenger.setBodyYaw(this.getYaw());
        final float passengerYaw = passenger.getYaw();

        final float deltaDegrees = MathHelper.wrapDegrees(passengerYaw - this.getYaw());
        final float clampedDelta = MathHelper.clamp(deltaDegrees, -160.0F, 160.0F);
        passenger.prevYaw += clampedDelta - deltaDegrees;

        final float newYaw = passengerYaw + clampedDelta - deltaDegrees;
        passenger.setYaw(newYaw);
        passenger.setHeadYaw(newYaw);
    }

}
