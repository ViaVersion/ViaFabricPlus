/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    @Shadow
    @Final
    private PlayerAbilities abilities;

    @Shadow
    public abstract boolean canHarvest(BlockState state);

    @Shadow
    @Final
    private PlayerInventory inventory;
    @Unique
    private static final EntityDimensions viaFabricPlus$sneaking_dimensions_v1_13_2 = EntityDimensions.changing(0.6f, 1.65f);

    @Unique
    private static final SoundEvent viaFabricPlus$random_hurt = SoundEvent.of(new Identifier("viafabricplus", "random.hurt"));

    @Unique
    public boolean viaFabricPlus$isSprinting;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "getMaxRelativeHeadRotation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isBlocking()Z"))
    private boolean dontModifyHeadRotationWhenBlocking(PlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20_2)) {
            return false;
        }
        return instance.isBlocking();
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setMovementSpeed(F)V"))
    private void storeSprintingState(CallbackInfo ci) {
        viaFabricPlus$isSprinting = this.isSprinting();
    }

    @Redirect(method = "getOffGroundSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z"))
    private boolean useLastSprintingState(PlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3)) {
            return viaFabricPlus$isSprinting;
        }
        return instance.isSprinting();
    }


    @Redirect(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void dontSwingHand(PlayerEntity instance, Hand hand) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) return;

        instance.swingHand(hand);
    }

    @Redirect(method = "checkFallFlying", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean allowElytraWhenLevitating(PlayerEntity instance, StatusEffect statusEffect) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_2)) {
            return false;
        }
        return instance.hasStatusEffect(statusEffect);
    }

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void replaceFallFlyingCondition(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_4)) {
            if (!this.isOnGround() && this.getVelocity().y < 0D && !this.isFallFlying()) {
                final ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
                if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "getActiveEyeHeight", constant = @Constant(floatValue = 1.27f))
    private float modifySneakEyeHeight(float prevEyeHeight) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            return 1.54F;
        }
        return prevEyeHeight;
    }

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePose(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            final EntityPose pose;
            if (this.isFallFlying()) {
                pose = EntityPose.FALL_FLYING;
            } else if (this.isSleeping()) {
                pose = EntityPose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = EntityPose.SWIMMING;
            } else if (this.isUsingRiptide()) {
                pose = EntityPose.SPIN_ATTACK;
            } else if (this.isSneaking() && !this.abilities.flying) {
                pose = EntityPose.CROUCHING;
            } else {
                pose = EntityPose.STANDING;
            }
            this.setPose(pose);
            ci.cancel();
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void modifyDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == EntityPose.CROUCHING) {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
                cir.setReturnValue(PlayerEntity.STANDING_DIMENSIONS);
            } else if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
                cir.setReturnValue(viaFabricPlus$sneaking_dimensions_v1_13_2);
            }
        }
    }

    @Redirect(method = "adjustMovementForSneaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F"))
    private float modifyStepHeight1_10(PlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_10)) {
            return 1.0F;
        }
        return instance.getStepHeight();
    }

    @Inject(method = "getAttackCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void removeAttackCooldown(CallbackInfoReturnable<Float> ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            ci.setReturnValue(1f);
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void replaceSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (VisualSettings.global().replaceHurtSoundWithOOFSound.isEnabled()) {
            cir.setReturnValue(viaFabricPlus$random_hurt);
        }
    }

    /**
     * @author Mojang, RK_01
     * @reason Block break speed calculation changes
     */
    @Overwrite
    public float getBlockBreakingSpeed(BlockState block) {
        float f = this.inventory.getBlockBreakingSpeed(block);
        final ItemStack itemStack = this.getMainHandStack();
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_4_4tor1_4_5)) {
            int i = EnchantmentHelper.getEfficiency(this);
            if (i > 0 && this.canHarvest(block)) f += (float) (i * i + 1);
        } else {
            if (f > 1F || ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_4_6tor1_4_7)) {
                int i = EnchantmentHelper.getEfficiency(this);
                if (i > 0 && !itemStack.isEmpty()) {
                    final float f1 = (i * i + 1);
                    if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_7_6tor1_7_10)) {
                        if (f <= 1.0 && !this.canHarvest(block)) f += f1 * 0.08F;
                        else f += f1;
                    } else {
                        f += f1;
                    }
                }
            }
        }

        if (StatusEffectUtil.hasHaste(this)) {
            f *= 1.0F + (float) (StatusEffectUtil.getHasteAmplifier(this) + 1) * 0.2F;
        }

        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_7_6tor1_7_10)) {
                f *= 1.0F - (float) (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier() + 1) * 0.2F;
                if (f < 0) f = 0;
            } else {
                f *= switch (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                    case 0 -> 0.3F;
                    case 1 -> 0.09F;
                    case 2 -> 0.0027F;
                    default -> 8.1E-4F;
                };
            }
        }

        if (this.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= 5.0F;
        }

        if (!this.isOnGround()) {
            f /= 5F;
        }

        return f;
    }

}
