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

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import de.florianmichael.viafabricplus.settings.impl.ExperimentalSettings;
import de.florianmichael.viafabricplus.definition.EntityHeightOffsetsPre1_20_2;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("ConstantValue")
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    protected boolean jumping;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private static void removeShieldSlotPreference(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_9_3tor1_9_4) && stack.isOf(Items.SHIELD)) {
            cir.setReturnValue(EquipmentSlot.MAINHAND);
        }
    }

    @Shadow
    protected abstract float getBaseMovementSpeedMultiplier();

    @Shadow
    private Optional<BlockPos> climbingPos;

    @Shadow
    protected abstract boolean canEnterTrapdoor(BlockPos pos, BlockState state);

    @Redirect(method = "applyMovementInput", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;jumping:Z"))
    private boolean disableJumpOnLadder(LivingEntity self) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return false;
        }

        return jumping;
    }

    @Redirect(method = "travel",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;DOLPHINS_GRACE:Lnet/minecraft/entity/effect/StatusEffect;")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;horizontalCollision:Z", ordinal = 0))
    private boolean disableClimbing(LivingEntity self) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return false;
        }

        return horizontalCollision;
    }

    @ModifyVariable(method = "applyFluidMovingSpeed", ordinal = 0, at = @At("HEAD"), argsOnly = true)
    private boolean modifyMovingDown(boolean movingDown) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return true;
        }

        return movingDown;
    }

    @WrapWithCondition(method = "tickMovement",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;LEVITATION:Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onLanding()V", ordinal = 0))
    private boolean dontResetLevitationFallDistance(LivingEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_12_2);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z", ordinal = 0))
    private boolean modifySwimSprintSpeed(LivingEntity self) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            return false;
        }
        return self.isSprinting();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D"))
    private double redirectFluidHeight(LivingEntity instance, TagKey<Fluid> tagKey) {
        if (ExperimentalSettings.INSTANCE.waterMovementEdgeDetection.getValue() && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2) && tagKey == FluidTags.WATER) {
            if (instance.getFluidHeight(tagKey) > 0) {
                return 1;
            }
        }
        return instance.getFluidHeight(tagKey);
    }

    @Inject(method = "applyFluidMovingSpeed", at = @At("HEAD"), cancellable = true)
    private void modifySwimSprintFallSpeed(double gravity, boolean movingDown, Vec3d velocity, CallbackInfoReturnable<Vec3d> ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2) && !hasNoGravity()) {
            ci.setReturnValue(new Vec3d(velocity.x, velocity.y - 0.02, velocity.z));
        }
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.003D))
    public double modifyVelocityZero(final double constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return 0.005D;
        }
        return constant;
    }

    @Inject(method = "canEnterTrapdoor", at = @At("HEAD"), cancellable = true)
    private void onCanEnterTrapdoor(CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            ci.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.9F))
    private float changeEntitySpeed(float constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            //noinspection ConstantConditions
            if ((Entity) this instanceof SkeletonHorseEntity) {
                return this.getBaseMovementSpeedMultiplier(); // 0.96F
            }
            return 0.8F;
        }
        return constant;
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Ljava/lang/Math;cos(D)D"))
    public double fixCosTable(double a) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18_2)) {
            return MathHelper.cos((float) a);
        }
        return Math.cos(a);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D"))
    public double fixLavaMovement(LivingEntity instance, TagKey<Fluid> tagKey) {
        double height = instance.getFluidHeight(tagKey);

        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
            height += getSwimHeight() + 4;
        }
        return height;
    }

    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    public int shieldBlockCounter(int constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return 0;
        }
        return constant;
    }

    @Redirect(method = "tickCramming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z"))
    public boolean revertOnlyPlayerCramming(World instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            return false;
        }
        return instance.isClient();
    }

    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    private void allowGappedLadderClimb(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThan(VersionEnum.b1_5tob1_5_2) && !cir.getReturnValueZ() && !this.isSpectator()) {
            final BlockPos blockPos = this.getBlockPos().up();
            final BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isIn(BlockTags.CLIMBABLE)) {
                this.climbingPos = Optional.of(blockPos);
                cir.setReturnValue(true);
            } else if (blockState.getBlock() instanceof TrapdoorBlock && this.canEnterTrapdoor(blockPos, blockState)) {
                this.climbingPos = Optional.of(blockPos);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getRidingOffset", at = @At("HEAD"), cancellable = true)
    public void replaceRidingOffset(Entity vehicle, CallbackInfoReturnable<Float> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            cir.setReturnValue((float) EntityHeightOffsetsPre1_20_2.getHeightOffset(this));
        }
    }

    @Redirect(method = "getPassengerRidingPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPassengerAttachmentPos(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityDimensions;F)Lorg/joml/Vector3f;"))
    public Vector3f revertStaticRidingOffsetCalculation(LivingEntity instance, Entity entity, EntityDimensions entityDimensions, float v) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            return EntityHeightOffsetsPre1_20_2.getMountedHeightOffset(instance, entity);
        }
        return getPassengerAttachmentPos(entity, entityDimensions, v);
    }
}
