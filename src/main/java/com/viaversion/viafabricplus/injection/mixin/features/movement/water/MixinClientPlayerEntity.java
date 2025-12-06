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

package com.viaversion.viafabricplus.injection.mixin.features.movement.water;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(LocalPlayer.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayer {

    @Shadow
    public ClientInput input;

    public MixinClientPlayerEntity(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean easierUnderwaterSprinting(ClientInput instance, Operation<Boolean> original) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return this.viaFabricPlus$isWalking1_21_4();
        } else {
            return original.call(instance);
        }
    }

    @Redirect(method = "aiStep",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z")),
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSwimming()Z", ordinal = 0))
    private boolean dontAllowSneakingWhileSwimming(LocalPlayer instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_14_1) && instance.isSwimming();
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isInWater()Z"))
    private boolean disableWaterRelatedMovement(LocalPlayer self) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2) && self.isInWater();
    }

    @Unique
    private boolean viaFabricPlus$isWalking1_21_4() {
        final boolean submergedInWater = ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_14_1) && isUnderWater();
        return submergedInWater ? this.input.hasForwardImpulse() : this.input.moveVector.y >= 0.8;
    }

}
