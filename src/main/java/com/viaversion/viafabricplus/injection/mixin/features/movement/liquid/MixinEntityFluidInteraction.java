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

package com.viaversion.viafabricplus.injection.mixin.features.movement.liquid;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFluidInteraction.class)
public abstract class MixinEntityFluidInteraction {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyeY()D"))
    private double subtractMagicOffset(Entity instance) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_16, ProtocolVersion.v1_20_3)) {
            return instance.getEyeY() - 0.11111111F;
        } else {
            return instance.getEyeY();
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getHeight(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float addMagicOffset(FluidState instance, BlockGetter blockGetter, BlockPos blockPos) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            return instance.getHeight(blockGetter, blockPos) + 0.11111111F;
        } else {
            return instance.getHeight(blockGetter, blockPos);
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D", ordinal = 0))
    private double modifyFluidHeight(double a, double b, @Local(name = "tracker") EntityFluidInteraction.Tracker tracker, @Local(name = "box") AABB box, @Local(name = "fluidTop") double fluidTop) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            if (fluidTop >= box.minY - 0.4) {
                return Math.max(fluidTop - box.minY + 0.4, tracker.height);
            } else {
                return tracker.height;
            }
        } else {
            return Math.max(a, b);
        }
    }

}
