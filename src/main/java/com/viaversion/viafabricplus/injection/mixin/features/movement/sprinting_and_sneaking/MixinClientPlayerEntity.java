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
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPackets1_21_5;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.Protocol1_21_5To1_21_6;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow
    public Input input;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    protected int ticksLeftToDoubleTapSprint;

    @Shadow
    private boolean inSneakingPose;

    @Unique
    private boolean viaFabricPlus$lastSneaking;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    private static Vec2f applyDirectionalMovementSpeedFactors(final Vec2f vec) {
        return null;
    }

    @Shadow
    protected abstract boolean shouldStopSprinting();

    @Shadow
    protected abstract boolean canSprint();

    @Shadow
    public abstract boolean shouldSlowDown();

    @Shadow
    protected abstract boolean canVehicleSprint(final Entity vehicle);

    @Shadow
    public abstract void tickMovementInput();

    @Shadow
    protected abstract boolean canStartSprinting();

    @Shadow
    protected abstract Vec2f applyMovementSpeedFactors(final Vec2f input);

    @Shadow
    public abstract void init();

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract boolean isSubmergedInWater();

    @Shadow
    public abstract boolean isUsingItem();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initLastSneaking(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, PlayerInput lastPlayerInput, boolean lastSprinting, CallbackInfo ci) {
        viaFabricPlus$lastSneaking = lastPlayerInput.sneak();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    private void sendSneakingPacket(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_21_2, ProtocolVersion.v1_21_5)) {
            this.viaFabricPlus$sendSneakingPacket();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    private void skipVVProtocol(ClientPlayNetworkHandler instance, Packet<?> packet) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_5) && packet instanceof PlayerInputC2SPacket(
            PlayerInput i
        )) {
            // Directly send the player input packet in order to bypass the code in the 1.21.5->1.21.6 protocol.
            // This allows mods to directly send raw packets which will then be remapped by VV instead of us.
            this.viaFabricPlus$sendInputPacket(i);
        } else {
            instance.sendPacket(packet);
        }
    }

    @Redirect(method = "tickMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;applyMovementSpeedFactors(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/util/math/Vec2f;"))
    private Vec2f moveMovementSpeedFactors(ClientPlayerEntity instance, Vec2f input) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return input;
        } else {
            return this.applyMovementSpeedFactors(input);
        }
    }

    @Redirect(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;applyDirectionalMovementSpeedFactors(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/util/math/Vec2f;"))
    private Vec2f moveMovementSpeedFactors(Vec2f vec) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return vec;
        } else {
            return applyDirectionalMovementSpeedFactors(vec);
        }
    }

    @Redirect(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;multiply(F)Lnet/minecraft/util/math/Vec2f;", ordinal = 0))
    private Vec2f moveMovementSpeedFactors(Vec2f instance, float value) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return instance;
        } else {
            return instance.multiply(value);
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onMovement(Lnet/minecraft/client/input/Input;)V", shift = At.Shift.AFTER))
    private void moveMovementSpeedFactors(CallbackInfo ci) {
        //... and also add this hotfix back
        if (ProtocolTranslator.getTargetVersion().equals(ProtocolVersion.v1_21_4) && this.shouldStopSprinting()) {
            this.setSprinting(false);
        }
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            this.input.movementVector = this.applyMovementSpeedFactors(this.input.movementVector);
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

    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V", shift = At.Shift.AFTER))
    private void sendSneakingAfterSprinting(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            this.viaFabricPlus$sendSneakingPacket();
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void storeSprintingSneakingState(CallbackInfo ci, @Share("sneakSprint") LocalBooleanRef ref) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            ref.set(!this.input.playerInput.sneak() && !this.viaFabricPlus$isWalking1_21_4());
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canStartSprinting()Z"))
    private boolean changeCanStartSprintingConditions(ClientPlayerEntity instance, @Share("sneakSprint") LocalBooleanRef ref) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final boolean canStartSprinting = this.canStartSprinting();
            final boolean onGround = this.hasVehicle() ? this.getVehicle().isOnGround() : this.isOnGround();
            if ((onGround || this.isSubmergedInWater()) && ref.get() && canStartSprinting) {
                if (this.ticksLeftToDoubleTapSprint <= 0 && !this.client.options.sprintKey.isPressed()) {
                    this.ticksLeftToDoubleTapSprint = 7;
                } else {
                    this.setSprinting(true);
                }
            }

            if ((!this.isTouchingWater() || this.isSubmergedInWater()) && canStartSprinting && this.client.options.sprintKey.isPressed()) {
                this.setSprinting(true);
            }
            return false;
        } else {
            return this.canStartSprinting();
        }
    }

    @Inject(method = "canStartSprinting", at = @At("HEAD"), cancellable = true)
    private void changeCanStartSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        final ProtocolVersion version = ProtocolTranslator.getTargetVersion();
        if (version.olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(!this.isSprinting()
                && (version.olderThanOrEqualTo(ProtocolVersion.v1_21_4) ? this.viaFabricPlus$isWalking1_21_4() : this.input.hasForwardMovement())
                && this.canSprint()
                && !this.isUsingItem()
                && !this.hasBlindnessEffect()
                && (!(version.newerThan(ProtocolVersion.v1_19_3) && this.hasVehicle()) || this.canVehicleSprint(this.getVehicle()))
                && (!(version.newerThan(ProtocolVersion.v1_19_3) && this.isGliding()) || this.isSubmergedInWater())
                && (!(this.shouldSlowDown() && version.equals(ProtocolVersion.v1_21_4)) || (this.isSubmergedInWater() && version.equals(ProtocolVersion.v1_21_4)))
                && (!version.olderThanOrEqualTo(ProtocolVersion.v1_21_4) && (!this.isTouchingWater() || this.isSubmergedInWater()) || version.olderThanOrEqualTo(ProtocolVersion.v1_21_4)));
        }
    }

    @Inject(method = "shouldStopSwimSprinting", at = @At("HEAD"), cancellable = true)
    private void changeStopSwimSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        // Not needed, but for consistency and in case a mod uses this method
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            cir.setReturnValue(!this.isOnGround() && !this.input.playerInput.sneak() && this.viaFabricPlus$shouldCancelSprinting() || !this.isTouchingWater());
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(this.hasBlindnessEffect() || this.hasVehicle() && !this.canVehicleSprint(this.getVehicle())
                || !this.isTouchingWater() || !this.input.hasForwardMovement() && !this.isOnGround() && !this.input.playerInput.sneak() || !this.canSprint());
        }
    }

    @Inject(method = "shouldStopSprinting", at = @At("HEAD"), cancellable = true)
    private void changeStopSprintingConditions(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final boolean ridingCamel = getVehicle() != null && getVehicle().getType() == EntityType.CAMEL;
            cir.setReturnValue(this.isGliding() || this.hasBlindnessEffect() || this.shouldSlowDown() || this.hasVehicle() && !ridingCamel || this.isUsingItem() && !this.hasVehicle() && !this.isSubmergedInWater());
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            cir.setReturnValue(this.hasBlindnessEffect() || this.hasVehicle() && !this.canVehicleSprint(this.getVehicle()) || !this.input.hasForwardMovement() || !this.canSprint() || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater());
        }
    }

    @Redirect(method = {"shouldStopSprinting", "canStartSprinting"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canSprint(Z)Z"))
    private boolean allowNonSwimWaterSprinting(ClientPlayerEntity instance, boolean allowTouchingWater) {
        return allowTouchingWater || ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest);
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
    private boolean removeSprintingPacket(ClientPlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_19_3);
    }

    @Redirect(method = "canSprint()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    private boolean dontAllowSprintingAsPassenger(ClientPlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_19_1) && instance.hasVehicle();
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick()V"))
    private void removeSneakingConditions(CallbackInfo ci) { // Allows sneaking while flying, inside blocks and vehicles
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            this.inSneakingPose = this.isSneaking() && !this.isSleeping();
        }
    }

    @Unique
    private void viaFabricPlus$sendSneakingPacket() {
        final boolean sneaking = this.isSneaking();
        if (sneaking == this.viaFabricPlus$lastSneaking) {
            return;
        }

        final PacketWrapper sneakingPacket = PacketWrapper.create(ServerboundPackets1_21_5.PLAYER_COMMAND, ProtocolTranslator.getPlayNetworkUserConnection());
        sneakingPacket.write(Types.VAR_INT, getId());
        sneakingPacket.write(Types.VAR_INT, sneaking ? 0 : 1);
        sneakingPacket.write(Types.VAR_INT, 0); // No data
        sneakingPacket.scheduleSendToServer(Protocol1_21_5To1_21_6.class);
        this.viaFabricPlus$lastSneaking = sneaking;
    }

    @Unique
    private void viaFabricPlus$sendInputPacket(final PlayerInput playerInput) {
        byte flags = 0;
        flags = (byte) (flags | (playerInput.forward() ? 0x1 : 0));
        flags = (byte) (flags | (playerInput.backward() ? 0x2 : 0));
        flags = (byte) (flags | (playerInput.left() ? 0x4 : 0));
        flags = (byte) (flags | (playerInput.right() ? 0x8 : 0));
        flags = (byte) (flags | (playerInput.jump() ? 0x10 : 0));
        flags = (byte) (flags | (playerInput.sneak() ? 0x20 : 0));
        flags = (byte) (flags | (playerInput.sprint() ? 0x40 : 0));

        final PacketWrapper inputPacket = PacketWrapper.create(ServerboundPackets1_21_5.PLAYER_INPUT, ProtocolTranslator.getPlayNetworkUserConnection());
        inputPacket.write(Types.BYTE, flags);
        inputPacket.scheduleSendToServer(Protocol1_21_5To1_21_6.class);
    }

    @Unique
    private boolean viaFabricPlus$shouldCancelSprinting() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_1)) {
            return !(this.input.movementVector.y >= 0.8F) || !this.canSprint(); // Disables sprint sneaking
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
