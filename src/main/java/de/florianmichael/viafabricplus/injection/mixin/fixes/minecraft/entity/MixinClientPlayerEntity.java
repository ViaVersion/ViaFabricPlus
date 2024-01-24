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

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.raphimc.vialegacy.protocols.release.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import net.raphimc.vialegacy.protocols.release.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow
    public Input input;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    private boolean lastOnGround;

    @Shadow
    private int ticksSinceLastPositionPacketSent;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", ordinal = 0))
    private boolean removeVehicleRequirement(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_20tor1_20_1) && instance.hasVehicle();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
    private boolean removeSprintingPacket(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_3);
    }

    @Redirect(method = "autoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;inverseSqrt(F)F"))
    private float useFastInverseSqrt(float x) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3)) {
            x = Float.intBitsToFloat(1597463007 - (Float.floatToIntBits(x) >> 1));
            return x * (1.5F - (0.5F * x) * x * x);
        } else {
            return MathHelper.inverseSqrt(x);
        }
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    private boolean removeVehicleCheck(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_19_3) && instance.hasVehicle();
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z"))
    private boolean removeFallFlyingCheck(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_19_3) && instance.isFallFlying();
    }

    @Redirect(method = "canSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    private boolean dontAllowSprintingAsPassenger(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_19_1tor1_19_2) && instance.hasVehicle();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;square(D)D"))
    private double changeMagnitude(double n) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18tor1_18_1)) {
            return 9.0E-4D;
        } else {
            return MathHelper.square(n);
        }
    }

    @Inject(method = "startRiding", at = @At("RETURN"))
    private void setRotationsWhenInBoat(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && entity instanceof BoatEntity && ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18tor1_18_1)) {
            this.prevYaw = entity.getYaw();
            this.setYaw(entity.getYaw());
            this.setHeadYaw(entity.getYaw());
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isClimbing()Z"))
    private boolean allowElytraWhenClimbing(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_15_1) && instance.isClimbing();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", ordinal = 3))
    private boolean allowElytraInVehicle(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_14_4) && instance.hasVehicle();
    }

    @ModifyVariable(method = "tickMovement", at = @At(value = "LOAD", ordinal = 4), ordinal = 4)
    private boolean removeBl8Boolean(boolean value) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_14_4) && value;
    }

    @Inject(method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0))
    private void injectTickMovement(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_4)) {
            if (this.input.sneaking) {
                this.input.movementSideways = (float) ((double) this.input.movementSideways / 0.3D);
                this.input.movementForward = (float) ((double) this.input.movementForward / 0.3D);
            }
        }
    }

    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    private void easierUnderwaterSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_1)) {
            cir.setReturnValue(((ClientPlayerEntity) (Object) this).input.movementForward >= 0.8);
        }
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_1)) {
            return input.movementForward >= 0.8F;
        } else {
            return input.hasForwardMovement();
        }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    private boolean dontAllowSneakingWhileSwimming(ClientPlayerEntity instance) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_14_1) && instance.isSwimming();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean disableWaterRelatedMovement(ClientPlayerEntity self) {
        return ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_12_2) && self.isTouchingWater();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;ticksSinceLastPositionPacketSent:I", ordinal = 0))
    private int moveLastPosPacketIncrement(ClientPlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return ticksSinceLastPositionPacketSent - 1; // Reverting original operation
        } else {
            return ticksSinceLastPositionPacketSent;
        }
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    private void moveLastPosPacketIncrement(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            this.ticksSinceLastPositionPacketSent++;
        }
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastOnGround:Z", ordinal = 0))
    private boolean sendIdlePacket(ClientPlayerEntity instance) {
        if (DebugSettings.global().sendIdlePacket.isEnabled()) {
            return !isOnGround();
        } else {
            return lastOnGround;
        }
    }

    @Redirect(method = "tick", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    private void modifyPositionPacket(ClientPlayNetworkHandler instance, Packet<?> packet) throws Exception {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_5_2)) {
            final PacketWrapper playerPosition = PacketWrapper.create(ServerboundPackets1_5_2.PLAYER_POSITION_AND_ROTATION, ((IClientConnection) this.networkHandler.getConnection()).viaFabricPlus$getUserConnection());
            playerPosition.write(Type.DOUBLE, this.getVelocity().x); // x
            playerPosition.write(Type.DOUBLE, -999.0D); // y
            playerPosition.write(Type.DOUBLE, -999.0D); // stance
            playerPosition.write(Type.DOUBLE, this.getVelocity().z); // z
            playerPosition.write(Type.FLOAT, this.getYaw()); // yaw
            playerPosition.write(Type.FLOAT, this.getPitch()); // pitch
            playerPosition.write(Type.BOOLEAN, this.isOnGround()); // onGround
            playerPosition.scheduleSendToServer(Protocol1_6_1to1_5_2.class);
            return;
        }
        instance.sendPacket(packet);
    }

}
