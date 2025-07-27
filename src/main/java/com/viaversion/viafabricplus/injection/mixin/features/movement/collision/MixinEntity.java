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

package com.viaversion.viafabricplus.injection.mixin.features.movement.collision;

import com.google.common.collect.ImmutableList;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    private Vec3d pos;

    @Shadow
    private World world;

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract float getStepHeight();

    @Shadow
    private static Iterable<Direction.Axis> getAxisCheckOrder(final Vec3d movement) {
        return null;
    }

    @Shadow
    @Final
    private static ImmutableList<Direction.Axis> X_THEN_Z;

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

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    private void modifyVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        final ProtocolVersion target = ProtocolTranslator.getTargetVersion();

        if (target.olderThanOrEqualTo(ProtocolVersion.v1_19_4)) {
            cir.setReturnValue(BlockPos.ofFloored(pos.x, getBoundingBox().minY - (target.olderThanOrEqualTo(ProtocolVersion.v1_14_4) ? 1 : 0.5000001), pos.z));
        }
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getAxisCheckOrder(Lnet/minecraft/util/math/Vec3d;)Ljava/lang/Iterable;"))
    private static Iterable<Direction.Axis> alwaysSortYXZ(Vec3d movement) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return X_THEN_Z;
        } else {
            return getAxisCheckOrder(movement);
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;approximatelyEquals(DD)Z"))
    private static boolean horizontalExactCollisionEqualness(double a, double b) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return a == b;
        } else {
            return MathHelper.approximatelyEquals(a, b);
        }
    }

}
