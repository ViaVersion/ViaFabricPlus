/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_9.movement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {

    @Shadow
    public ClientInput input;

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected int sprintTriggerTime;

    @Shadow
    protected abstract boolean shouldStopRunSprinting();

    @Shadow
    public abstract boolean isMovingSlowly();

    @Shadow
    protected abstract boolean vehicleCanSprint(final Entity vehicle);

    @Shadow
    protected abstract boolean canStartSprinting();

    @Shadow
    public abstract boolean isUnderWater();

    @Shadow
    public abstract boolean isUsingItem();

    public MixinLocalPlayer(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "isSprintingPossible", at = @At("HEAD"), cancellable = true)
    private void isSprintingPossible1_21_10(boolean allowedInShallowWater, CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_9)) {
            cir.setReturnValue(!this.isMobilityRestricted() && this.viaFabricPlus$hasEnoughFoodToSprint1_19_1()
                && (!this.isPassenger() || this.vehicleCanSprint(this.getVehicle())) && (allowedInShallowWater || !this.isInShallowWater()));
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;shouldStopRunSprinting()Z"))
    private boolean changeStopSprintingConditions(LocalPlayer instance) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return this.viaFabricPlus$shouldCancelSprinting() || this.horizontalCollision && !this.minorHorizontalCollision || !this.viaFabricPlus$canWaterSprint();
        } else {
            return this.shouldStopRunSprinting();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void storeSprintingSneakingState(CallbackInfo ci, @Share("sneakSprint") LocalBooleanRef ref) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            ref.set(!this.input.keyPresses.shift() && !this.viaFabricPlus$isWalking1_21_4());
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;canStartSprinting()Z"))
    private boolean changeCanStartSprintingConditions(LocalPlayer instance, @Share("sneakSprint") LocalBooleanRef ref) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final boolean canStartSprinting = this.canStartSprinting();
            final boolean onGround = this.isPassenger() ? this.getVehicle().onGround() : this.onGround();
            if ((onGround || this.isUnderWater()) && ref.get() && canStartSprinting) {
                if (this.sprintTriggerTime <= 0 && !this.minecraft.options.keySprint.isDown()) {
                    this.sprintTriggerTime = this.minecraft.options.sprintWindow().get();
                } else {
                    this.setSprinting(true);
                }
            }

            if ((this.viaFabricPlus$canWaterSprint()) && canStartSprinting && this.minecraft.options.keySprint.isDown()) {
                this.setSprinting(true);
            }
            return false;
        } else {
            return this.canStartSprinting();
        }
    }

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void changeCanStartSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        final ProtocolVersion version = ViaFabricPlus.api().targetVersion();
        if (version.olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(!this.isSprinting()
                && (version.olderThanOrEqualTo(ProtocolVersion.v1_21_4) ? this.viaFabricPlus$isWalking1_21_4() : this.input.hasForwardImpulse())
                && this.viaFabricPlus$hasEnoughFoodToSprint1_19_1()
                && !this.isUsingItem()
                && !this.isMobilityRestricted()
                && (!(version.newerThan(ProtocolVersion.v1_19_3) && this.isPassenger()) || this.vehicleCanSprint(this.getVehicle()))
                && (!(version.newerThan(ProtocolVersion.v1_19_3) && this.isFallFlying()) || this.isUnderWater())
                && (!(this.isMovingSlowly() && version.equals(ProtocolVersion.v1_21_4)) || (this.isUnderWater() && version.equals(ProtocolVersion.v1_21_4)))
                && (!version.olderThanOrEqualTo(ProtocolVersion.v1_21_4) && (!this.isInWater() || this.isUnderWater()) || version.olderThanOrEqualTo(ProtocolVersion.v1_21_4)));
        }
    }

    @Inject(method = "shouldStopSwimSprinting", at = @At("HEAD"), cancellable = true)
    private void changeStopSwimSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        // Not needed, but for consistency and in case a mod uses this method
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            cir.setReturnValue(!this.onGround() && !this.input.keyPresses.shift() && this.viaFabricPlus$shouldCancelSprinting() || !this.isInWater());
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(this.isMobilityRestricted() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle())
                || !this.isInWater() || !this.input.hasForwardImpulse() && !this.onGround() && !this.input.keyPresses.shift() || !this.viaFabricPlus$hasEnoughFoodToSprint1_19_1());
        }
    }

    @Inject(method = "shouldStopRunSprinting", at = @At("HEAD"), cancellable = true)
    private void changeStopSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final boolean ridingCamel = getVehicle() != null && getVehicle().getType() == EntityTypes.CAMEL;
            cir.setReturnValue(this.isFallFlying() || this.isMobilityRestricted() || this.isMovingSlowly() || this.isPassenger() && !ridingCamel || this.isUsingItem() && !this.isPassenger() && !this.isUnderWater());
        } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(this.isMobilityRestricted() || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle()) || !this.input.hasForwardImpulse() || !this.viaFabricPlus$hasEnoughFoodToSprint1_19_1() || this.horizontalCollision && !this.minorHorizontalCollision || this.isInWater() && !this.isUnderWater());
        }
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean easierUnderwaterSprinting(ClientInput instance, Operation<Boolean> original) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return this.viaFabricPlus$isWalking1_21_4();
        } else {
            return original.call(instance);
        }
    }

    @Unique
    private boolean viaFabricPlus$shouldCancelSprinting() {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_1)) {
            return !(this.input.moveVector.y >= 0.8F) || !this.viaFabricPlus$hasEnoughFoodToSprint1_19_1(); // Disables sprint sneaking
        } else {
            return !this.input.hasForwardImpulse() || !this.viaFabricPlus$hasEnoughFoodToSprint1_19_1();
        }
    }

    @Unique
    private boolean viaFabricPlus$hasEnoughFoodToSprint1_19_1() {
        return (ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_19_1) && this.isPassenger()) || this.hasEnoughFoodToDoExhaustiveManoeuvres();
    }

    @Unique
    private boolean viaFabricPlus$isWalking1_21_4() {
        final boolean submergedInWater = ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_14_1) && isUnderWater();
        return submergedInWater ? this.input.hasForwardImpulse() : this.input.moveVector.y >= 0.8;
    }

    @Unique
    private boolean viaFabricPlus$canWaterSprint() {
        return ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) || (!this.isInWater() || this.isUnderWater());
    }

}
