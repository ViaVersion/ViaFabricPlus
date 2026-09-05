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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.movement;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    public float xxa;

    @Shadow
    public float zza;

    public MixinLivingEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"))
    private void moveMovementSpeedFactors(CallbackInfo ci) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            this.xxa *= 0.98F;
            this.zza *= 0.98F;
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;is(Ljava/lang/Object;)Z"))
    private boolean useEuclideanDistanceCalculation(LivingEntity instance, Object o) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return false;
        } else {
            return instance.is((EntityType<?>) o);
        }
    }

    @Redirect(method = "travelFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z"))
    private boolean dontStopGlidingWhenClimbing(LivingEntity instance) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_21_4) && instance.onClimbable();
    }

    @Redirect(method = "handleRelativeFrictionAndCalculateMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;wasInPowderSnow:Z", opcode = Opcodes.GETFIELD))
    private boolean dontCheckLastTick(LivingEntity instance) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return this.getInBlockState().is(Blocks.POWDER_SNOW);
        } else {
            return instance.wasInPowderSnow;
        }
    }

}
