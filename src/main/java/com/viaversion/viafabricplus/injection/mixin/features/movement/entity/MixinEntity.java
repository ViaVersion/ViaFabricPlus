/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.movement.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    private World world;

    @Shadow
    private Vec3d pos;

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract float getStepHeight();

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;lengthSquared()D", ordinal = 1), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;")
    ))
    private double allowSmallValues(Vec3d instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            return Double.MAX_VALUE;
        } else {
            return instance.lengthSquared();
        }
    }

    @Redirect(method = "setPitch", at = @At(value = "INVOKE", target = "Ljava/lang/Math;clamp(FFF)F", remap = false))
    private float dontClampPitch(float value, float min, float max) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            return value;
        } else {
            return Math.clamp(value, min, max);
        }
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void use1_20_6StepCollisionCalculation(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            final Entity thiz = (Entity) (Object) this;
            final Box box = this.getBoundingBox();
            final List<VoxelShape> collisions = this.getWorld().getEntityCollisions(thiz, box.stretch(movement));
            Vec3d adjustedMovement = movement.lengthSquared() == 0D ? movement : Entity.adjustMovementForCollisions(thiz, movement, box, this.getWorld(), collisions);
            final boolean changedX = movement.x != adjustedMovement.x;
            final boolean changedY = movement.y != adjustedMovement.y;
            final boolean changedZ = movement.z != adjustedMovement.z;
            final boolean mayTouchGround = this.isOnGround() || changedY && movement.y < 0D;
            if (this.getStepHeight() > 0F && mayTouchGround && (changedX || changedZ)) {
                Vec3d vec3d2 = Entity.adjustMovementForCollisions(thiz, new Vec3d(movement.x, this.getStepHeight(), movement.z), box, this.getWorld(), collisions);
                Vec3d vec3d3 = Entity.adjustMovementForCollisions(thiz, new Vec3d(0D, this.getStepHeight(), 0D), box.stretch(movement.x, 0D, movement.z), this.getWorld(), collisions);
                if (vec3d3.y < this.getStepHeight()) {
                    Vec3d vec3d4 = Entity.adjustMovementForCollisions(thiz, new Vec3d(movement.x, 0D, movement.z), box.offset(vec3d3), this.getWorld(), collisions).add(vec3d3);
                    if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                        vec3d2 = vec3d4;
                    }
                }

                if (vec3d2.horizontalLengthSquared() > adjustedMovement.horizontalLengthSquared()) {
                    adjustedMovement = vec3d2.add(Entity.adjustMovementForCollisions(thiz, new Vec3d(0D, -vec3d2.y + (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2) ? 0 : movement.y), 0D), box.offset(vec3d2), this.getWorld(), collisions));
                }
            }

            cir.setReturnValue(adjustedMovement);
        }
    }

    @Inject(method = "getPosWithYOffset", at = @At("HEAD"), cancellable = true)
    private void modifyPosWithYOffset(float offset, CallbackInfoReturnable<BlockPos> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_4)) {
            int i = MathHelper.floor(this.pos.x);
            int j = MathHelper.floor(this.pos.y - (double) (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_18_2) && offset == 1.0E-5F ? 0.2F : offset));
            int k = MathHelper.floor(this.pos.z);
            BlockPos blockPos = new BlockPos(i, j, k);
            if (this.world.getBlockState(blockPos).isAir()) {
                BlockPos downPos = blockPos.down();
                BlockState blockState = this.world.getBlockState(downPos);
                if (blockState.isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || blockState.getBlock() instanceof FenceGateBlock) {
                    cir.setReturnValue(downPos);
                    return;
                }
            }

            cir.setReturnValue(blockPos);
        }
    }

    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 9.999999747378752E-6))
    private double fixBlockCollisionMargin(double constant) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            return 1E-3;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            return 1E-7;
        } else {
            return constant;
        }
    }

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    private void modifyVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        final ProtocolVersion target = ProtocolTranslator.getTargetVersion();

        if (target.olderThanOrEqualTo(ProtocolVersion.v1_19_4)) {
            cir.setReturnValue(BlockPos.ofFloored(pos.x, getBoundingBox().minY - (target.olderThanOrEqualTo(ProtocolVersion.v1_14_4) ? 1 : 0.5000001), pos.z));
        }
    }

    @Redirect(method = {"setYaw", "setPitch"}, at = @At(value = "INVOKE", target = "Ljava/lang/Float;isFinite(F)Z"))
    private boolean allowInfiniteValues(float f) {
        return Float.isFinite(f) || ((Object) this instanceof ClientPlayerEntity && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_4));
    }

    @ModifyConstant(method = "movementInputToVelocity", constant = @Constant(doubleValue = 1E-7))
    private static double fixVelocityEpsilon(double epsilon) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return 1E-4;
        } else {
            return epsilon;
        }
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(D)D", ordinal = 0))
    private static double alwaysSortYXZ(double a) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return Double.MAX_VALUE;
        } else {
            return Math.abs(a);
        }
    }

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void revertCalculation(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(Vec3d.fromPolar(pitch, yaw));
        }
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void expandHitBox(CallbackInfoReturnable<Float> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(0.1F);
        }
    }

}