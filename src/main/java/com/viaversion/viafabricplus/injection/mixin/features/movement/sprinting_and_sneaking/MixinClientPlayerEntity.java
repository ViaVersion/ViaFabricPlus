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

package com.viaversion.viafabricplus.injection.mixin.features.movement.sprinting_and_sneaking;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow
    protected abstract boolean shouldStopSprinting();

    @Shadow
    protected abstract void sendSneakingPacket();

    @Shadow
    private boolean inSneakingPose;

    @Shadow
    public Input input;

    @Shadow
    protected abstract boolean canSprint();

    @Shadow
    protected abstract boolean isBlind();

    @Shadow
    public abstract boolean shouldSlowDown();

    @Shadow
    protected abstract boolean canVehicleSprint(final Entity vehicle);

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    public abstract void tickMovementInput();

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onMovement(Lnet/minecraft/client/input/Input;)V", shift = At.Shift.AFTER))
    private void checkShouldStopSprinting(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().equals(ProtocolVersion.v1_21_4) && this.shouldStopSprinting()) {
            this.setSprinting(false);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PlayerInput;backward()Z"))
    private boolean dontResetDoubleTapTicks(PlayerInput instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_21_4) && instance.backward();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldStopSprinting()Z"))
    private boolean changeStopSprintingConditions(ClientPlayerEntity instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return this.viaFabricPlus$shouldCancelSprinting() || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater();
        } else {
            return this.shouldStopSprinting();
        }
    }

    // TODO For some reason this is not working even thoug being a 1:1 of the original method

//    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canStartSprinting()Z"))
//    private boolean changeCanStartSprintingConditions(ClientPlayerEntity instance) {
//        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
//            boolean z5 = this.canStartSprinting();
//            boolean z6 = this.hasVehicle() ? this.getVehicle().isOnGround() : this.isOnGround();
//            boolean z7 = !this.input.playerInput.sneak() && !this.viaFabricPlus$isWalking1_21_4();
//            if ((z6 || this.isSubmergedInWater()) && z7 && z5) {
//                if (this.ticksLeftToDoubleTapSprint <= 0 && !this.client.options.sprintKey.isPressed()) {
//                    this.ticksLeftToDoubleTapSprint = 7;
//                } else {
//                    this.setSprinting(true);
//                }
//            }
//
//            if ((!this.isTouchingWater() || this.isSubmergedInWater()) && z5 && this.client.options.sprintKey.isPressed()) {
//                this.setSprinting(true);
//            }
//            return false;
//        } else {
//            return this.canStartSprinting();
//        }
//    }

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void changeCanStartSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        final ProtocolVersion version = ProtocolTranslator.getTargetVersion();
        if (version.olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            cir.setReturnValue(!this.isSprinting()
                && this.viaFabricPlus$isWalking1_21_4()
                && this.canSprint()
                && !this.isUsingItem()
                && !this.isBlind()
                && (!(version.newerThan(ProtocolVersion.v1_19_3) && this.hasVehicle()) || this.canVehicleSprint(this.getVehicle()))
                && !(version.newerThan(ProtocolVersion.v1_19_3) && this.isGliding())
                && (!(this.shouldSlowDown() && version.equals(ProtocolVersion.v1_21_4)) || (this.isSubmergedInWater() && version.equals(ProtocolVersion.v1_21_4))));
        }
    }

    @Inject(method = "shouldStopSwimSprinting", at = @At("HEAD"), cancellable = true)
    private void changeStopSwimSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            cir.setReturnValue(!this.isOnGround() && !this.input.playerInput.sneak() && this.viaFabricPlus$shouldCancelSprinting() || !this.isTouchingWater());
        }
    }

    @Inject(method = "shouldStopSprinting", at = @At("HEAD"), cancellable = true)
    private void changeStopSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final boolean ridingCamel = getVehicle() != null && getVehicle().getType() == EntityType.CAMEL;
            cir.setReturnValue(this.isGliding() || this.isBlind() || this.shouldSlowDown() || this.hasVehicle() && !ridingCamel || this.isUsingItem() && !this.hasVehicle() && !this.isSubmergedInWater());
        }
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSneakingPacket()V"))
    private boolean sendSneakingAfterSprinting(ClientPlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_2);
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V", shift = At.Shift.AFTER))
    private void sendSneakingAfterSprinting(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            this.sendSneakingPacket();
        }
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
    private boolean removeSprintingPacket(ClientPlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_19_3);
    }

    @Redirect(method = "canSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    private boolean dontAllowSprintingAsPassenger(ClientPlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_19_1) && instance.hasVehicle();
    }

    @Inject(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"))
    private void removeSneakingConditions(Vec2f input, CallbackInfoReturnable<Vec2f> cir) { // Allows sneaking while flying, inside blocks and vehicles
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            this.inSneakingPose = this.isSneaking() && !this.isSleeping();
        }
    }

    @Unique
    private boolean viaFabricPlus$shouldCancelSprinting() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_1)) {
            return this.input.getMovementInput().y < 0.8F || !this.canSprint(); // Disables sprint sneaking
        } else {
            return !this.input.hasForwardMovement() || !this.canSprint();
        }
    }

    @Unique
    private boolean viaFabricPlus$isWalking1_21_4() {
        final boolean submergedInWater = ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_14_1) && isSubmergedInWater();
        return submergedInWater ? this.input.hasForwardMovement() : this.input.movementVector.y >= 0.8;
    }

}
