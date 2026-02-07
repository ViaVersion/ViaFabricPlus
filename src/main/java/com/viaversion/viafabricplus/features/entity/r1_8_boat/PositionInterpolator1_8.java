/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

import com.viaversion.viafabricplus.injection.access.entity.r1_8_boat.IAbstractBoat;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;

public class PositionInterpolator1_8 extends InterpolationHandler {

    private final AbstractBoat boatEntity;

    public PositionInterpolator1_8(final AbstractBoat entity) {
        super(entity);
        this.boatEntity = entity;
    }

    @Override
    public void interpolateTo(final Vec3 pos, final float yaw, final float pitch) {
        final IAbstractBoat mixinBoatEntity = (IAbstractBoat) this.boatEntity;
        if (/*interpolate &&*/ boatEntity.isVehicle() && ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_7_6)) {
            boatEntity.xo = pos.x;
            boatEntity.yo = pos.y;
            boatEntity.zo = pos.z;
            mixinBoatEntity.viaFabricPlus$setBoatInterpolationSteps(0);
            boatEntity.setPos(pos);
            boatEntity.setRot(yaw, pitch);
            boatEntity.setDeltaMovement(Vec3.ZERO);
            mixinBoatEntity.viaFabricPlus$setBoatVelocity(Vec3.ZERO);
        } else {
            if (!boatEntity.isVehicle()) {
                mixinBoatEntity.viaFabricPlus$setBoatInterpolationSteps(8);
            } else {
                if (boatEntity.distanceToSqr(pos.x, pos.y, pos.z) <= 1) {
                    return;
                }
                mixinBoatEntity.viaFabricPlus$setBoatInterpolationSteps(3);
            }

            this.interpolationData.position = pos;
            this.interpolationData.yRot = yaw;
            this.interpolationData.xRot = pitch;
            boatEntity.setDeltaMovement(mixinBoatEntity.viaFabricPlus$getBoatVelocity());
        }
    }

    @Override
    public void interpolate() {
    }

}
