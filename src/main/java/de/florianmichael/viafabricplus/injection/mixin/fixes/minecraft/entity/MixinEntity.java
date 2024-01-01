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

import de.florianmichael.viafabricplus.fixes.entity.EntityRidingOffsetsPre1_20_2;
import de.florianmichael.viafabricplus.injection.access.IEntity;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
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
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    protected abstract Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor);

    @Unique
    private boolean viaFabricPlus$isInLoadedChunkAndShouldTick;

    @Inject(method = "getRidingOffset", at = @At("HEAD"), cancellable = true)
    private void getRidingOffset1_20_1(Entity vehicle, CallbackInfoReturnable<Float> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            cir.setReturnValue((float) EntityRidingOffsetsPre1_20_2.getHeightOffset((Entity) (Object) this));
        }
    }

    @Redirect(method = "getPassengerRidingPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPassengerAttachmentPos(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityDimensions;F)Lorg/joml/Vector3f;"))
    private Vector3f getPassengerRidingPos1_20_1(Entity instance, Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            return EntityRidingOffsetsPre1_20_2.getMountedHeightOffset(instance, passenger);
        }

        return getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
    }

    @Inject(method = "getPosWithYOffset", at = @At("HEAD"), cancellable = true)
    private void modifyPosWithYOffset(float offset, CallbackInfoReturnable<BlockPos> cir) {
        final VersionEnum target = ProtocolHack.getTargetVersion();
        if (target.isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            int i = MathHelper.floor(this.pos.x);
            int j = MathHelper.floor(this.pos.y - (double) (target.isOlderThanOrEqualTo(VersionEnum.r1_18_2) && offset == 1.0E-5F ? 0.2F : offset));
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

    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 1.0E-7))
    private double fixBlockCollisionMargin(double constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            return 1E-3;
        }

        return constant;
    }

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    private void modifyVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        final VersionEnum target = ProtocolHack.getTargetVersion();

        if (target.isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            cir.setReturnValue(BlockPos.ofFloored(pos.x, getBoundingBox().minY - (target.isOlderThanOrEqualTo(VersionEnum.r1_14_4) ? 1 : 0.5000001), pos.z));
        }
    }

    @Redirect(method = {"setYaw", "setPitch"}, at = @At(value = "INVOKE", target = "Ljava/lang/Float;isFinite(F)Z"))
    private boolean allowInfiniteValues(float f) {
        return Float.isFinite(f) || ((Object) this instanceof ClientPlayerEntity && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_16_4tor1_16_5));
    }

    @ModifyConstant(method = "movementInputToVelocity", constant = @Constant(doubleValue = 1E-7))
    private static double fixVelocityEpsilon(double epsilon) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return 1E-4;
        }
        return epsilon;
    }

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void revertCalculation(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(Vec3d.fromPolar(pitch, yaw));
        }
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void cancelSwimming(boolean swimming, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2) && swimming) {
            ci.cancel();
        }
    }

    @Inject(method = "updateMovementInFluid", at = @At("HEAD"), cancellable = true)
    private void modifyFluidMovementBoundingBox(TagKey<Fluid> fluidTag, double d, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_12_2)) {
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
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
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
