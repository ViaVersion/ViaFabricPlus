/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.movement.collision;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Shapes.class)
public abstract class MixinShapes {

    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private static void calculateMaxOffset1_12_2(Direction.Axis axis, AABB box, Iterable<VoxelShape> shapes, double maxDist, CallbackInfoReturnable<Double> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            for (final VoxelShape shape : shapes) {
                for (final AABB shapeBox : shape.toAabbs()) {
                    maxDist = switch (axis) {
                        case X -> viaFabricPlus$intersectX(box, shapeBox, maxDist);
                        case Y -> viaFabricPlus$intersectY(box, shapeBox, maxDist);
                        case Z -> viaFabricPlus$intersectZ(box, shapeBox, maxDist);
                    };
                }
            }
            cir.setReturnValue(maxDist);
        }
    }

    @Unique
    private static double viaFabricPlus$intersectX(final AABB box, final AABB shapeBox, double maxDist) {
        if (box.maxY <= shapeBox.minY || box.minY >= shapeBox.maxY || box.maxZ <= shapeBox.minZ || box.minZ >= shapeBox.maxZ) {
            return maxDist;
        }

        double e;
        if (maxDist > 0.0 && box.maxX <= shapeBox.minX) {
            double d = shapeBox.minX - box.maxX;
            if (d < maxDist) {
                maxDist = d;
            }
        } else if (maxDist < 0.0 && box.minX >= shapeBox.maxX && (e = shapeBox.maxX - box.minX) > maxDist) {
            maxDist = e;
        }
        return maxDist;
    }

    @Unique
    private static double viaFabricPlus$intersectY(final AABB playerBox, final AABB shapeBox, double maxDist) {
        if (playerBox.maxX <= shapeBox.minX || playerBox.minX >= shapeBox.maxX || playerBox.maxZ <= shapeBox.minZ || playerBox.minZ >= shapeBox.maxZ) {
            return maxDist;
        }

        double e;
        if (maxDist > 0.0 && playerBox.maxY <= shapeBox.minY) {
            double d = shapeBox.minY - playerBox.maxY;
            if (d < maxDist) {
                maxDist = d;
            }
        } else if (maxDist < 0.0 && playerBox.minY >= shapeBox.maxY && (e = shapeBox.maxY - playerBox.minY) > maxDist) {
            maxDist = e;
        }
        return maxDist;
    }

    @Unique
    private static double viaFabricPlus$intersectZ(final AABB playerBox, final AABB shapeBox, double maxDist) {
        if (playerBox.maxX <= shapeBox.minX || playerBox.minX >= shapeBox.maxX || playerBox.maxY <= shapeBox.minY || playerBox.minY >= shapeBox.maxY) {
            return maxDist;
        }

        double e;
        if (maxDist > 0.0 && playerBox.maxZ <= shapeBox.minZ) {
            double d = shapeBox.minZ - playerBox.maxZ;
            if (d < maxDist) {
                maxDist = d;
            }
        } else if (maxDist < 0.0 && playerBox.minZ >= shapeBox.maxZ && (e = shapeBox.maxZ - playerBox.minZ) > maxDist) {
            maxDist = e;
        }
        return maxDist;
    }

}
