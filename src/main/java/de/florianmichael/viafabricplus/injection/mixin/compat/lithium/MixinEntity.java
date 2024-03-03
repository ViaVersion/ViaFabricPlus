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

package de.florianmichael.viafabricplus.injection.mixin.compat.lithium;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Lithium is overriding the sorting with an optimized implementation
// https://github.com/CaffeineMC/lithium-fabric/blob/develop/src/main/java/me/jellysquid/mods/lithium/mixin/entity/collisions/movement/EntityMixin.java#L54
@Mixin(value = Entity.class, priority = 1001 /* Lithium has to be applied first */)
public abstract class MixinEntity {

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Redirect(method = "lithiumCollideMultiAxisMovement", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(D)D", ordinal = 0), remap = false)
    private static double alwaysSortYXZ(double a) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return Double.MAX_VALUE;
        } else {
            return Math.abs(a);
        }
    }

}
