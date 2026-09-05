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

package com.viaversion.viafabricplus.injection.mixin.features.v1_13_2.movement;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    protected boolean jumping;

    @Shadow
    public abstract float getSpeed();

    @Shadow
    protected abstract float getFlyingSpeed();

    public MixinLivingEntity(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Redirect(method = "handleRelativeFrictionAndCalculateMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;jumping:Z", opcode = Opcodes.GETFIELD))
    private boolean disableJumpOnLadder(LivingEntity self) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_13_2) && jumping;
    }

    @Inject(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), cancellable = true)
    private void modifyFrictionInfluencedSpeed(float blockFriction, CallbackInfoReturnable<Float> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            float drag = this.onGround() ? blockFriction * 0.91F : 0.91F;
            float accel = 0.16277136F / (drag * drag * drag);
            cir.setReturnValue(this.onGround() ? this.getSpeed() * accel : this.getFlyingSpeed());
        }
    }

    @Redirect(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasChunkAt(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean modifyLoadedCheck(Level instance, BlockPos blockPos) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return instance.hasChunkAt(blockPos) && instance.getChunkSource().hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        } else {
            return instance.hasChunkAt(blockPos);
        }
    }

    @Redirect(method = "travelInWater",
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/world/effect/MobEffects;DOLPHINS_GRACE:Lnet/minecraft/core/Holder;", opcode = Opcodes.GETSTATIC)),
        at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;horizontalCollision:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
    private boolean disableClimbing(LivingEntity instance) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_13_2) && instance.horizontalCollision;
    }

    @ModifyVariable(method = "getFluidFallingAdjustedMovement", at = @At("HEAD"), argsOnly = true)
    private boolean modifyMovingDown(boolean isFalling) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_13_2) && isFalling;
    }

}
