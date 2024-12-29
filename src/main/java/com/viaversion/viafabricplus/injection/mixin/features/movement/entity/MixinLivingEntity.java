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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    protected boolean jumping;

    @Shadow
    private Optional<BlockPos> climbingPos;

    @Shadow
    protected abstract boolean canEnterTrapdoor(BlockPos pos, BlockState state);

    @Shadow
    private int jumpingCooldown;

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(method = "tickStatusEffects", at = @At(value = "CONSTANT", args = "intValue=4"))
    private int changeParticleDensity(int original) {
        if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_20_5)) {
            return 2;
        } else {
            return original;
        }
    }

    @Inject(method = "tickCramming", at = @At("HEAD"), cancellable = true)
    private void preventEntityPush(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.cancel();
        }
    }

    @Redirect(method = "tickActiveItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean replaceItemStackEqualsCheck(ItemStack left, ItemStack right) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_3)) {
            return left == right;
        } else {
            return ItemStack.areItemsEqual(left, right);
        }
    }

    @Redirect(method = "travelMidAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean modifyLoadedCheck(World instance, BlockPos blockPos) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return this.getWorld().isChunkLoaded(blockPos) && instance.getChunkManager().isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        } else {
            return this.getWorld().isChunkLoaded(blockPos);
        }
    }

    @Redirect(method = "applyMovementInput", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;jumping:Z"))
    private boolean disableJumpOnLadder(LivingEntity self) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_13_2) && jumping;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onLanding()V"))
    private void dontResetLevitationFallDistance(LivingEntity instance) {
        if (this.hasStatusEffect(StatusEffects.SLOW_FALLING) || ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
            instance.onLanding();
        }
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private void removeShieldSlotPreference(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_9_3) && stack.isOf(Items.SHIELD)) {
            cir.setReturnValue(EquipmentSlot.MAINHAND);
        }
    }

    @ModifyConstant(method = "getBlockingItem", constant = @Constant(intValue = 5))
    private int removeBlockActionUseDelay(int constant) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0;
        } else {
            return constant;
        }
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.003D))
    private double modifyVelocityZero(double constant) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0.005D;
        } else {
            return constant;
        }
    }

    @Inject(method = "canEnterTrapdoor", at = @At("HEAD"), cancellable = true)
    private void disableCrawling(CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.setReturnValue(false);
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void removeJumpDelay(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThan(LegacyProtocolVersion.r1_0_0tor1_0_1)) {
            this.jumpingCooldown = 0;
        }
    }

    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    private void allowGappedLadderClimb(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThan(LegacyProtocolVersion.b1_5tob1_5_2) && !cir.getReturnValueZ() && !this.isSpectator()) {
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

}
