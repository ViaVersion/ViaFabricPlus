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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.fixes.versioned.visual.EntityRidingOffsetsPre1_20_2;
import de.florianmichael.viafabricplus.injection.access.IEntity;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow
    private World world;

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow
    private Vec3d pos;

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Shadow
    protected abstract Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor);

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract boolean isOnGround();

    @Shadow
    public abstract float getStepHeight();

    @Unique
    private boolean viaFabricPlus$isInLoadedChunkAndShouldTick;

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
                    adjustedMovement = vec3d2.add(Entity.adjustMovementForCollisions(thiz, new Vec3d(0D, -vec3d2.y + movement.y, 0D), box.offset(vec3d2), this.getWorld(), collisions));
                }
            }

            cir.setReturnValue(adjustedMovement);
        }
    }

    @Redirect(method = "updateSubmergedInWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeY()D"))
    private double addMagicOffset(Entity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            return instance.getEyeY() - 0.11111111F;
        } else {
            return instance.getEyeY();
        }
    }

    @Redirect(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVehicleAttachmentPos(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d use1_20_1RidingOffset(Entity instance, Entity vehicle) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20)) {
            return new Vec3d(0, -EntityRidingOffsetsPre1_20_2.getHeightOffset(instance), 0);
        } else {
            return instance.getVehicleAttachmentPos(vehicle);
        }
    }

    @Redirect(method = "getPassengerRidingPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPassengerAttachmentPos(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityDimensions;F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d getPassengerRidingPos1_20_1(Entity instance, Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20)) {
            return EntityRidingOffsetsPre1_20_2.getMountedHeightOffset(instance, passenger).rotateY(-instance.getYaw() * (float) (Math.PI / 180));
        } else {
            return getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
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

    // TODO/NOTE: No longer subtracts 1.0E-7 from the box values, instead the boundingBox is contracted
//    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 1.0E-7))
//    private double fixBlockCollisionMargin(double constant) {
//        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
//            return 1E-3;
//        } else {
//            return constant;
//        }
//    }

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

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void cancelSwimming(boolean swimming, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) && swimming) {
            ci.cancel();
        }
    }

    @Inject(method = "updateMovementInFluid", at = @At("HEAD"), cancellable = true)
    private void modifyFluidMovementBoundingBox(TagKey<Fluid> fluidTag, double d, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
            return;
        }

        Box box = this.getBoundingBox().expand(0, -0.4, 0).contract(0.001);
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);

        if (!this.world.isRegionLoaded(minX, minY, minZ, maxX, maxY, maxZ)) {
            cir.setReturnValue(false);
            return;
        }

        double waterHeight = 0;
        boolean foundFluid = false;
        Vec3d pushVec = Vec3d.ZERO;

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY - 1; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    mutable.set(x, y, z);
                    FluidState state = this.world.getFluidState(mutable);
                    if (state.isIn(fluidTag)) {
                        double height = y + state.getHeight(this.world, mutable);
                        if (height >= box.minY - 0.4)
                            waterHeight = Math.max(height - box.minY + 0.4, waterHeight);
                        if (y >= minY && maxY >= height) {
                            foundFluid = true;
                            pushVec = pushVec.add(state.getVelocity(this.world, mutable));
                        }
                    }
                }
            }
        }

        if (pushVec.length() > 0) {
            pushVec = pushVec.normalize().multiply(0.014);
            this.setVelocity(this.getVelocity().add(pushVec));
        }

        this.fluidHeight.put(fluidTag, waterHeight);
        cir.setReturnValue(foundFluid);
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void expandHitBox(CallbackInfoReturnable<Float> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(0.1F);
        }
    }

    @Override
    public boolean viaFabricPlus$isInLoadedChunkAndShouldTick() {
        return this.viaFabricPlus$isInLoadedChunkAndShouldTick || DebugSettings.global().alwaysTickClientPlayer.isEnabled();
    }

    @Override
    public void viaFabricPlus$setInLoadedChunkAndShouldTick(final boolean inLoadedChunkAndShouldTick) {
        this.viaFabricPlus$isInLoadedChunkAndShouldTick = inLoadedChunkAndShouldTick;
    }

}
