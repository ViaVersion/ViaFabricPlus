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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    @Shadow
    @Final
    private PlayerAbilities abilities;

    @Shadow
    public abstract boolean canHarvest(BlockState state);

    @Shadow
    @Final
    PlayerInventory inventory;

    @Unique
    private static final EntityDimensions viaFabricPlus$sneaking_dimensions_v1_13_2 = EntityDimensions.changing(0.6F, 1.65F).withEyeHeight(1.54F).
            withAttachments(EntityAttachments.builder().add(EntityAttachmentType.VEHICLE, PlayerEntity.VEHICLE_ATTACHMENT_POS));

    @Unique
    private static final EntityDimensions viaFabricPlus$sneaking_dimensions_v1_8 = EntityDimensions.changing(0.6F, 1.8F).withEyeHeight(1.54F).
            withAttachments(EntityAttachments.builder().add(EntityAttachmentType.VEHICLE, PlayerEntity.VEHICLE_ATTACHMENT_POS));

    @Unique
    private static final SoundEvent viaFabricPlus$oof_hurt = SoundEvent.of(Identifier.of("viafabricplus", "oof.hurt"));

    @Unique
    public boolean viaFabricPlus$isSprinting;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
    private void allowClimbingWhileFlying(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            cir.setReturnValue(super.isClimbing());
        }
    }

    @Inject(method = "isLoaded", at = @At("HEAD"), cancellable = true)
    private void alwaysLoadPlayer(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyConstant(method = "isSpaceAroundPlayerEmpty", constant = @Constant(doubleValue = 9.999999747378752E-6 /* 1.0E-5F */))
    private double removeOffsetWhenCheckingSneakingCollision(double constant) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            return 0;
        } else {
            return constant;
        }
    }

    @Redirect(method = "getMaxRelativeHeadRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isBlocking()Z"))
    private boolean dontModifyHeadRotationWhenBlocking(PlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20_2) && instance.isBlocking();
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setMovementSpeed(F)V"))
    private void storeSprintingState(CallbackInfo ci) {
        viaFabricPlus$isSprinting = this.isSprinting();
    }

    @Redirect(method = "getOffGroundSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z"))
    private boolean useLastSprintingState(PlayerEntity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_3)) {
            return viaFabricPlus$isSprinting;
        } else {
            return instance.isSprinting();
        }
    }

    @WrapWithCondition(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private boolean dontSwingHand(PlayerEntity instance, Hand hand) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_15_2);
    }

    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void preventEatingFoodInCreative(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4) && this.abilities.invulnerable) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "checkGliding", at = @At("HEAD"), cancellable = true)
    private void replaceGlidingCondition(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            if (!this.isOnGround() && this.getVelocity().y < 0D && !this.isGliding()) {
                final ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
                if (itemStack.isOf(Items.ELYTRA) && canGlideWith(itemStack, EquipmentSlot.CHEST)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            final EntityPose pose;
            if (this.isGliding()) {
                pose = EntityPose.GLIDING;
            } else if (this.isSleeping()) {
                pose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = EntityPose.SWIMMING;
            } else if (this.isUsingRiptide()) {
                pose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking()) {
                pose = EntityPose.CROUCHING;
            } else {
                pose = EntityPose.STANDING;
            }
            this.setPose(pose);
            ci.cancel();
        }
    }

    @Inject(method = "getBaseDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == EntityPose.CROUCHING) {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
                cir.setReturnValue(viaFabricPlus$sneaking_dimensions_v1_8);
            } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
                cir.setReturnValue(viaFabricPlus$sneaking_dimensions_v1_13_2);
            }
        }
    }

    @Redirect(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F"))
    private float modifyStepHeight(PlayerEntity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_10)) {
            return 1.0F;
        } else {
            return instance.getStepHeight();
        }
    }

    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void removeAttackCooldown(CallbackInfoReturnable<Float> ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.setReturnValue(1F);
        }
    }

    @Redirect(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
    private boolean changeSpeedCalculation(PlayerEntity instance, RegistryEntry<StatusEffect> statusEffect, @Local LocalFloatRef f) {
        final boolean hasMiningFatigue = instance.hasStatusEffect(statusEffect);
        if (hasMiningFatigue && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            f.set(f.get() * (1.0F - (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier() + 1) * 0.2F));
            if (f.get() < 0) {
                f.set(0);
            }
            return false; // disable original code
        }
        return hasMiningFatigue;
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(Lnet/minecraft/entity/LivingEntity;)Z", shift = At.Shift.BEFORE))
    private void changeSpeedCalculation(BlockState block, CallbackInfoReturnable<Float> cir, @Local LocalFloatRef f) {
        final float efficiency = (float) this.getAttributeValue(EntityAttributes.MINING_EFFICIENCY);
        if (efficiency <= 0) {
            return;
        }

        final float speed = this.inventory.getBlockBreakingSpeed(block);
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_4tor1_4_5) && this.canHarvest(block)) {
            f.set(speed + efficiency);
        } else if (speed > 1F || ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            if (!this.getMainHandStack().isEmpty()) {
                if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
                    if (speed <= 1.0 && !this.canHarvest(block)) {
                        f.set(speed + efficiency * 0.08F);
                    } else {
                        f.set(speed + efficiency);
                    }
                }
            }
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void replaceSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (VisualSettings.global().replaceHurtSoundWithOOFSound.isEnabled()) {
            cir.setReturnValue(viaFabricPlus$oof_hurt);
        }
    }

}
