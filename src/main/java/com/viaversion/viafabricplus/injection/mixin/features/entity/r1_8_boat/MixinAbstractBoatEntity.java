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

package com.viaversion.viafabricplus.injection.mixin.features.entity.r1_8_boat;

import com.viaversion.viafabricplus.features.entity.r1_8_boat.PositionInterpolator1_8;
import com.viaversion.viafabricplus.injection.access.entity.r1_8_boat.IAbstractBoatEntity;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoatEntity.class)
public abstract class MixinAbstractBoatEntity extends VehicleEntity implements IAbstractBoatEntity {
    @Shadow
    private BoatEntity.Location location;

    @Shadow
    public abstract PositionInterpolator getInterpolator();

    @Unique
    private final PositionInterpolator viaFabricPlus$positionInterpolator = new PositionInterpolator1_8((AbstractBoatEntity) (Object) this);

    @Unique
    private double viaFabricPlus$speedMultiplier = 0.07D;

    @Unique
    private int viaFabricPlus$boatInterpolationSteps;

    @Unique
    private Vec3d viaFabricPlus$boatVelocity = Vec3d.ZERO;

    public MixinAbstractBoatEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void pushAwayFrom1_8(Entity entity, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.cancel();
            super.pushAwayFrom(entity);
        }
    }

    @Inject(method = "getInterpolator", at = @At("HEAD"), cancellable = true)
    private void replaceInterpolation(CallbackInfoReturnable<PositionInterpolator> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(viaFabricPlus$positionInterpolator);
        }
    }

    @Override
    public void setVelocityClient(final Vec3d clientVelocity) {
        super.setVelocityClient(clientVelocity);
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            this.viaFabricPlus$boatVelocity = clientVelocity;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick1_8(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.cancel();
            super.tick();
            if (this.getDamageWobbleTicks() > 0) {
                this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
            }

            if (this.getDamageWobbleStrength() > 0) {
                this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1);
            }

            this.lastX = this.getX();
            this.lastY = this.getY();
            this.lastZ = this.getZ();

            final int yPartitions = 5;
            double percentSubmerged = 0;
            for (int partitionIndex = 0; partitionIndex < yPartitions; partitionIndex++) {
                final double minY = this.getBoundingBox().minY + this.getBoundingBox().getLengthY() * partitionIndex / yPartitions - 0.125;
                final double maxY = this.getBoundingBox().minY + this.getBoundingBox().getLengthY() * (partitionIndex + 1) / yPartitions - 0.125;
                final Box box = new Box(this.getBoundingBox().minX, minY, this.getBoundingBox().minZ, this.getBoundingBox().maxX, maxY, this.getBoundingBox().maxZ);
                if (BlockPos.stream(box).anyMatch(pos -> this.getEntityWorld().getFluidState(pos).isIn(FluidTags.WATER))) {
                    percentSubmerged += 1.0 / yPartitions;
                }
            }

            final double oldHorizontalSpeed = this.getVelocity().horizontalLength();
            if (oldHorizontalSpeed > (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6) ? 0.2625D : 0.2975D)) {
                final double rx = Math.cos(this.getYaw() * Math.PI / 180);
                final double rz = Math.sin(this.getYaw() * Math.PI / 180);
                for (int i = 0; i < 1 + oldHorizontalSpeed * 60; i++) {
                    final double dForward = this.random.nextFloat() * 2 - 1;
                    final double dSideways = (this.random.nextInt(2) * 2 - 1) * 0.7D;
                    if (this.random.nextBoolean()) {
                        final double x = this.getX() - rx * dForward * 0.8 + rz * dSideways;
                        final double z = this.getZ() - rz * dForward * 0.8 - rx * dSideways;
                        this.getEntityWorld().addParticleClient(ParticleTypes.SPLASH, x, this.getY() - 0.125D, z, this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
                    } else {
                        final double x = this.getX() + rx + rz * dForward * 0.7;
                        final double z = this.getZ() + rz - rx * dForward * 0.7;
                        this.getEntityWorld().addParticleClient(ParticleTypes.SPLASH, x, this.getY() - 0.125D, z, this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
                    }
                }
            }

            if (this.getEntityWorld().isClient() && !this.hasPassengers()) {
                if (this.viaFabricPlus$boatInterpolationSteps > 0) {
                    final PositionInterpolator.Data data = viaFabricPlus$positionInterpolator.data;
                    final double newX = this.getX() + (data.pos.x - this.getX()) / this.viaFabricPlus$boatInterpolationSteps;
                    final double newY = this.getY() + (data.pos.y - this.getY()) / this.viaFabricPlus$boatInterpolationSteps;
                    final double newZ = this.getZ() + (data.pos.z - this.getZ()) / this.viaFabricPlus$boatInterpolationSteps;
                    final double newYaw = this.getYaw() + MathHelper.wrapDegrees(data.yaw - this.getYaw()) / this.viaFabricPlus$boatInterpolationSteps;
                    final double newPitch = this.getPitch() + (data.pitch - this.getPitch()) / this.viaFabricPlus$boatInterpolationSteps;
                    this.viaFabricPlus$boatInterpolationSteps--;
                    this.setPosition(newX, newY, newZ);
                    this.setRotation((float) newYaw, (float) newPitch);
                } else {
                    this.setPosition(this.getX() + this.getVelocity().x, this.getY() + this.getVelocity().y, this.getZ() + this.getVelocity().z);
                    if (this.isOnGround()) {
                        this.setVelocity(this.getVelocity().multiply(0.5D));
                    }
                    this.setVelocity(this.getVelocity().multiply(0.99D, 0.95D, 0.99D));
                }
            } else {
                if (percentSubmerged < 1) {
                    final double normalizedDistanceFromMiddle = percentSubmerged * 2 - 1;
                    this.setVelocity(this.getVelocity().add(0, 0.04D * normalizedDistanceFromMiddle, 0));
                } else {
                    if (this.getVelocity().y < 0) {
                        this.setVelocity(this.getVelocity().multiply(1, 0.5D, 1));
                    }
                    this.setVelocity(this.getVelocity().add(0, 0.007D, 0));
                }

                if (this.getControllingPassenger() != null) {
                    final LivingEntity passenger = this.getControllingPassenger();
                    if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_5_2)) {
                        final double xAcceleration = passenger.getVelocity().x * this.viaFabricPlus$speedMultiplier;
                        final double zAcceleration = passenger.getVelocity().z * this.viaFabricPlus$speedMultiplier;
                        this.setVelocity(this.getVelocity().add(xAcceleration, 0, zAcceleration));
                    } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                        if (passenger.forwardSpeed > 0) {
                            final double xAcceleration = -Math.sin(passenger.getYaw() * Math.PI / 180) * this.viaFabricPlus$speedMultiplier * 0.05D;
                            final double zAcceleration = Math.cos(passenger.getYaw() * Math.PI / 180) * this.viaFabricPlus$speedMultiplier * 0.05D;
                            this.setVelocity(this.getVelocity().add(xAcceleration, 0, zAcceleration));
                        }
                    } else {
                        final float boatAngle = passenger.getYaw() - passenger.sidewaysSpeed * 90F;
                        final double xAcceleration = -Math.sin(boatAngle * Math.PI / 180) * this.viaFabricPlus$speedMultiplier * passenger.forwardSpeed * 0.05D;
                        final double zAcceleration = Math.cos(boatAngle * Math.PI / 180) * this.viaFabricPlus$speedMultiplier * passenger.forwardSpeed * 0.05D;
                        this.setVelocity(this.getVelocity().add(xAcceleration, 0, zAcceleration));
                    }
                }

                double newHorizontalSpeed = this.getVelocity().horizontalLength();
                if (newHorizontalSpeed > 0.35D) {
                    final double multiplier = 0.35D / newHorizontalSpeed;
                    this.setVelocity(this.getVelocity().multiply(multiplier, 1, multiplier));
                    newHorizontalSpeed = 0.35D;
                }

                if (newHorizontalSpeed > oldHorizontalSpeed && this.viaFabricPlus$speedMultiplier < 0.35D) {
                    this.viaFabricPlus$speedMultiplier += (0.35D - this.viaFabricPlus$speedMultiplier) / 35;
                    if (this.viaFabricPlus$speedMultiplier > 0.35D) {
                        this.viaFabricPlus$speedMultiplier = 0.35D;
                    }
                } else {
                    this.viaFabricPlus$speedMultiplier -= (this.viaFabricPlus$speedMultiplier - 0.07D) / 35;
                    if (this.viaFabricPlus$speedMultiplier < 0.07D) {
                        this.viaFabricPlus$speedMultiplier = 0.07D;
                    }
                }

                if (ProtocolTranslator.getTargetVersion().newerThan(LegacyProtocolVersion.r1_6_4)) {
                    for (int i = 0; i < 4; i++) {
                        final int dx = MathHelper.floor(this.getX() + ((i % 2) - 0.5D) * 0.8D);
                        //noinspection IntegerDivisionInFloatingPointContext
                        final int dz = MathHelper.floor(this.getZ() + ((i / 2) - 0.5D) * 0.8D);
                        for (int ddy = 0; ddy < 2; ddy++) {
                            final int dy = MathHelper.floor(this.getY()) + ddy;
                            final BlockPos pos = new BlockPos(dx, dy, dz);
                            final Block block = this.getEntityWorld().getBlockState(pos).getBlock();
                            if (block == Blocks.SNOW) {
                                this.getEntityWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
                                this.horizontalCollision = false;
                            } else if (block == Blocks.LILY_PAD) {
                                this.getEntityWorld().breakBlock(pos, true);
                                this.horizontalCollision = false;
                            }
                        }
                    }
                }

                if (this.isOnGround()) {
                    this.setVelocity(this.getVelocity().multiply(0.5D));
                }

                this.move(MovementType.SELF, this.getVelocity());

                if (!this.horizontalCollision || oldHorizontalSpeed <= 0.2975D) {
                    this.setVelocity(this.getVelocity().multiply(0.99D, 0.95D, 0.99D));
                }

                this.setPitch(0);
                final double deltaX = this.lastX - this.getX();
                final double deltaZ = this.lastZ - this.getZ();
                if (deltaX * deltaX + deltaZ * deltaZ > 0.001D) {
                    final double yawDelta = MathHelper.clamp(MathHelper.wrapDegrees((MathHelper.atan2(deltaZ, deltaX) * 180 / Math.PI) - this.getYaw()), -20, 20);
                    this.setYaw((float) (this.getYaw() + yawDelta));
                }
            }
        }
    }

    @Inject(method = "onPassengerLookAround", at = @At("HEAD"), cancellable = true)
    private void onPassengerLookAround1_8(Entity passenger, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.cancel();
            super.onPassengerLookAround(passenger);
        }
    }

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    private void fall1_8(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            ci.cancel();
            super.fall(heightDifference, onGround, state, landedPosition);
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            this.location = BoatEntity.Location.ON_LAND;
        }
    }

    @Inject(method = "canAddPassenger", at = @At("HEAD"), cancellable = true)
    private void canAddPassenger1_8(Entity passenger, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(super.canAddPassenger(passenger));
        }
    }

    @Override
    public void viaFabricPlus$setBoatInterpolationSteps(final int steps) {
        viaFabricPlus$boatInterpolationSteps = steps;
    }

    @Override
    public Vec3d viaFabricPlus$getBoatVelocity() {
        return viaFabricPlus$boatVelocity;
    }

    @Override
    public void viaFabricPlus$setBoatVelocity(final Vec3d velocity) {
        viaFabricPlus$boatVelocity = velocity;
    }
}
