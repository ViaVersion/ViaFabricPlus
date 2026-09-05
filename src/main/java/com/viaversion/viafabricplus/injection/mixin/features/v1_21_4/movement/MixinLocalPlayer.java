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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_4.movement;

import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {

    @Shadow
    protected abstract Vec2 modifyInput(final Vec2 input);

    @Shadow
    private static Vec2 modifyInputSpeedForSquareMovement(final Vec2 vec) {
        return null;
    }

    @Shadow
    protected abstract boolean shouldStopRunSprinting();

    @Shadow
    public ClientInput input;

    public MixinLocalPlayer(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "applyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;modifyInput(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;"))
    private Vec2 moveMovementSpeedFactors(LocalPlayer instance, Vec2 input) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return input;
        } else {
            return this.modifyInput(input);
        }
    }

    @Redirect(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;modifyInputSpeedForSquareMovement(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec2;"))
    private Vec2 moveMovementSpeedFactors(Vec2 vec) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return vec;
        } else {
            return modifyInputSpeedForSquareMovement(vec);
        }
    }

    @Redirect(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;scale(F)Lnet/minecraft/world/phys/Vec2;", ordinal = 0))
    private Vec2 moveMovementSpeedFactors(Vec2 instance, float s) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return instance;
        } else {
            return instance.scale(s);
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/ClientInput;)V", shift = At.Shift.AFTER))
    private void moveMovementSpeedFactors(CallbackInfo ci) {
        //... and also add this hotfix back
        if (ViaFabricPlus.api().targetVersion().equals(ProtocolVersion.v1_21_4) && this.shouldStopRunSprinting()) {
            this.setSprinting(false);
        }
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            this.input.moveVector = this.modifyInput(this.input.moveVector);
        }
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;backward()Z"))
    private boolean dontResetDoubleTapTicks(Input instance) {
        return ViaFabricPlus.api().targetVersion().newerThan(ProtocolVersion.v1_21_4) && instance.backward();
    }

}
