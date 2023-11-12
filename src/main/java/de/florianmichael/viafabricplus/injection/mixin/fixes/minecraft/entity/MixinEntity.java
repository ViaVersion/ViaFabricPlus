/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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

import de.florianmichael.viafabricplus.definition.EntityHeightOffsetsPre1_20_2;
import net.minecraft.entity.EntityDimensions;
import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public World world;
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


    @Shadow protected abstract Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor);

    @ModifyConstant(method = "movementInputToVelocity", constant = @Constant(doubleValue = 1E-7))
    private static double injectMovementInputToVelocity(double epsilon) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return 1E-4;
        }
        return epsilon;
    }

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    public void replaceAffectingVelocityMagnitude(CallbackInfoReturnable<BlockPos> cir) {
        final VersionEnum target = ProtocolHack.getTargetVersion();

        if (target.isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            cir.setReturnValue(BlockPos.ofFloored(pos.x, getBoundingBox().minY - (target.isOlderThanOrEqualTo(VersionEnum.r1_14_4) ? 1 : 0.5000001), pos.z));
        }
    }

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    public void revertCalculation(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(Vec3d.fromPolar(pitch, yaw));
        }
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void onSetSwimming(boolean swimming, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2) && swimming) {
            ci.cancel();
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "updateMovementInFluid", at = @At("HEAD"), cancellable = true)
    private void modifyFluidMovementBoundingBox(TagKey<Fluid> fluidTag, double d, CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_12_2)) {
            return;
        }

        Box box = getBoundingBox().expand(0, -0.4, 0).contract(0.001);
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);

        if (!world.isRegionLoaded(minX, minY, minZ, maxX, maxY, maxZ)) {
            ci.setReturnValue(false);
        }

        double waterHeight = 0;
        boolean foundFluid = false;
        Vec3d pushVec = Vec3d.ZERO;

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY - 1; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    mutable.set(x, y, z);
                    FluidState state = world.getFluidState(mutable);
                    if (state.isIn(fluidTag)) {
                        double height = y + state.getHeight(world, mutable);
                        if (height >= box.minY - 0.4)
                            waterHeight = Math.max(height - box.minY + 0.4, waterHeight);
                        if (y >= minY && maxY >= height) {
                            foundFluid = true;
                            pushVec = pushVec.add(state.getVelocity(world, mutable));
                        }
                    }
                }
            }
        }

        if (pushVec.length() > 0) {
            pushVec = pushVec.normalize().multiply(0.014);
            setVelocity(getVelocity().add(pushVec));
        }

        this.fluidHeight.put(fluidTag, waterHeight);
        ci.setReturnValue(foundFluid);
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    public void expandHitBox(CallbackInfoReturnable<Float> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            cir.setReturnValue(0.1F);
        }
    }

    @Redirect(method = {"setYaw", "setPitch"}, at = @At(value = "INVOKE", target = "Ljava/lang/Float;isFinite(F)Z"))
    public boolean modifyIsFinite(float f) {
        return Float.isFinite(f) || ((Object) this instanceof ClientPlayerEntity && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2));
    }

    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 1.0E-7))
    public double changeBlockCollisionConstant(double constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            return 0.001;
        }
        return constant;
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onLanding()V"))
    public void revertOnLanding(Entity instance) {
        if (ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19)) {
            instance.onLanding();
        }
    }

    @Inject(method = "getPosWithYOffset", at = @At("HEAD"), cancellable = true)
    public void changeLogic(float offset, CallbackInfoReturnable<BlockPos> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            int i = MathHelper.floor(this.pos.x);
            int j = MathHelper.floor(this.pos.y - (double)offset);
            int k = MathHelper.floor(this.pos.z);
            BlockPos blockPos = new BlockPos(i, j, k);
            if (this.world.getBlockState(blockPos).isAir()) {
                BlockPos blockPos2 = blockPos.down();
                BlockState blockState = this.world.getBlockState(blockPos2);
                if (blockState.isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || blockState.getBlock() instanceof FenceGateBlock) {
                    cir.setReturnValue(blockPos2);
                }
            }

            cir.setReturnValue(blockPos);
        }
    }

    @Inject(method = "getRidingOffset", at = @At("HEAD"), cancellable = true)
    public void replaceRidingOffset(Entity vehicle, CallbackInfoReturnable<Float> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            cir.setReturnValue((float) EntityHeightOffsetsPre1_20_2.getHeightOffset((Entity) (Object) this));
        }
    }

    @Redirect(method = "getPassengerRidingPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getPassengerAttachmentPos(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityDimensions;F)Lorg/joml/Vector3f;"))
    public Vector3f revertStaticRidingOffsetCalculation(Entity instance, Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            return EntityHeightOffsetsPre1_20_2.getMountedHeightOffset(instance, passenger);
        }
        return getPassengerAttachmentPos(passenger, dimensions, scaleFactor);
    }
}
