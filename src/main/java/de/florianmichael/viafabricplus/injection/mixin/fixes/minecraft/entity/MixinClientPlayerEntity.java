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

import com.mojang.authlib.GameProfile;
import de.florianmichael.viafabricplus.definition.ClientsideFixes;
import net.minecraft.world.GameMode;
import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.base.settings.groups.DebugSettings;
import de.florianmichael.viafabricplus.injection.access.IClientPlayerEntity;
import de.florianmichael.viafabricplus.base.settings.groups.VisualSettings;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantValue")
@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    @Shadow
    public Input input;

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    private boolean lastOnGround;

    @Shadow
    private int ticksSinceLastPositionPacketSent;

    @Unique
    private boolean viafabricplus_areSwingCanceledThisTick = false;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow protected abstract void sendSprintingPacket();

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;square(D)D"))
    public double changeMagnitude(double n) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18_2)) {
            n = 9.0E-4D;
        }
        return MathHelper.square(n);
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;ticksSinceLastPositionPacketSent:I", ordinal = 0))
    public int revertLastPositionPacketSentIncrementor(ClientPlayerEntity instance) {
        // Mixin calls the redirector and sets the original field to the return value of the redirector + 1, therefore the -1 results, so we truncate the + 1 again and the field does not change
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            return ticksSinceLastPositionPacketSent - 1;
        }
        return ticksSinceLastPositionPacketSent;
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    public void incrementLastPositionPacketSentCounter(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            ++this.ticksSinceLastPositionPacketSent;
        }
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastOnGround:Z", ordinal = 0))
    public boolean sendIdlePacket(ClientPlayerEntity instance) {
        if (DebugSettings.INSTANCE.sendIdlePacket.isEnabled()) {
            return !isOnGround();
        }
        return lastOnGround;
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8) && viafabricplus_areSwingCanceledThisTick) {
            ci.cancel();
        }

        viafabricplus_areSwingCanceledThisTick = false;
    }

    @Inject(
            method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0)
    )
    private void injectTickMovement(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_4)) {
            if (this.input.sneaking) {
                this.input.movementSideways = (float) ((double) this.input.movementSideways / 0.3D);
                this.input.movementForward = (float) ((double) this.input.movementForward / 0.3D);
            }
        }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    public boolean redirectIsSneakingWhileSwimming(ClientPlayerEntity _this) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_1)) {
            return false;
        } else {
            return _this.isSwimming();
        }
    }

    @Redirect(method = "isWalking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    public boolean easierUnderwaterSprinting(ClientPlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_1)) {
            return false;
        }
        return instance.isSubmergedInWater();
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_1)) {
            return input.movementForward >= 0.8F;
        }

        return input.hasForwardMovement();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean redirectTickMovement(ClientPlayerEntity self) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            return false; // Disable all water related movement
        }

        return self.isTouchingWater();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
    public void removeSprintingPacket(ClientPlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_3)) {
            sendSprintingPacket();
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isClimbing()Z"))
    public boolean alwaysSendPacket(ClientPlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_15_1)) {
            return false;
        }
        return isClimbing();
    }

    @Redirect(method = "autoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;inverseSqrt(F)F"))
    public float useFastInverse(float x) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3)) {
            x = Float.intBitsToFloat(1597463007 - (Float.floatToIntBits(x) >> 1));

            return x * (1.5F - (0.5F * x) * x * x);
        }
        return MathHelper.inverseSqrt(x);
    }

    @Override
    public boolean isCreative() {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_7_6tor1_7_10)) {
            if (client.interactionManager == null) return super.isCreative(); // Fixes https://github.com/ViaVersion/ViaFabricPlus/issues/216
            return client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE;
        }
        return super.isCreative();
    }

    @Override
    public int getArmor() {
        if (VisualSettings.INSTANCE.emulateArmorHud.isEnabled()) {
            return client.player.getInventory().armor.stream().mapToInt(ClientsideFixes::getLegacyArmorPoints).sum();
        }
        return super.getArmor();
    }

    @Override
    public void viafabricplus_cancelSwingOnce() {
        viafabricplus_areSwingCanceledThisTick = true;
    }
}
