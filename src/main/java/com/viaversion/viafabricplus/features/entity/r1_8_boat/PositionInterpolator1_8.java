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

package com.viaversion.viafabricplus.features.entity.r1_8_boat;

import com.viaversion.viafabricplus.injection.access.entity.r1_8_boat.IAbstractBoatEntity;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.Vec3d;

public class PositionInterpolator1_8 extends PositionInterpolator {

    private final AbstractBoatEntity boatEntity;

    public PositionInterpolator1_8(final AbstractBoatEntity entity) {
        super(entity);
        this.boatEntity = entity;
    }

    @Override
    public void refreshPositionAndAngles(final Vec3d pos, final float yaw, final float pitch) {
        final IAbstractBoatEntity mixinBoatEntity = (IAbstractBoatEntity) this.boatEntity;
        if (/*interpolate &&*/ boatEntity.hasPassengers() && ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_7_6)) {
            boatEntity.lastX = pos.x;
            boatEntity.lastY = pos.y;
            boatEntity.lastZ = pos.z;
            mixinBoatEntity.viaFabricPlus$setBoatInterpolationSteps(0);
            boatEntity.setPosition(pos);
            boatEntity.setRotation(yaw, pitch);
            boatEntity.setVelocity(Vec3d.ZERO);
            mixinBoatEntity.viaFabricPlus$setBoatVelocity(Vec3d.ZERO);
        } else {
            if (!boatEntity.hasPassengers()) {
                mixinBoatEntity.viaFabricPlus$setBoatInterpolationSteps(8);
            } else {
                if (boatEntity.squaredDistanceTo(pos.x, pos.y, pos.z) <= 1) {
                    return;
                }
                mixinBoatEntity.viaFabricPlus$setBoatInterpolationSteps(3);
            }

            this.data.pos = pos;
            this.data.yaw = yaw;
            this.data.pitch = pitch;
            boatEntity.setVelocity(mixinBoatEntity.viaFabricPlus$getBoatVelocity());
        }
    }

    @Override
    public void tick() {
    }

}
