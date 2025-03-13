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

package com.viaversion.viafabricplus.injection.mixin.features.movement.slowdown;

import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow
    public Input input;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldSlowDown()Z"))
    private boolean removeSlowdownCondition(ClientPlayerEntity instance) {
        return instance.shouldSlowDown() && ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_4);
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    private boolean removeSlowdownCondition2(ClientPlayerEntity instance) {
        return instance.isSubmergedInWater() && ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_4);
    }

//    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldSlowDown()Z"))
//    private boolean changeSneakSlowdownCondition(ClientPlayerEntity instance) {
//        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
//            return instance.input.playerInput.sneak();
//        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
//            return !MinecraftClient.getInstance().player.isSpectator() && (instance.input.playerInput.sneak() || instance.shouldSlowDown());
//        } else {
//            return instance.shouldSlowDown();
//        }
//    }
//TODO UPDATE-1.21.5
//    @Inject(method = "tickMovement()V",
//            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PlayerInput;sneak()Z", ordinal = 0))
//    private void undoSneakSlowdownForFly(CallbackInfo ci) {
//        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_9, ProtocolVersion.v1_14_4) && this.input.playerInput.sneak()) {
//            this.input.movementSideways = (float) ((double) this.input.movementSideways / 0.3D);
//            this.input.getMovementInput().y = (float) ((double) this.input.getMovementInput().y / 0.3D);
//
//            this.input.getMovementInput().multiply(1 / 0.3F);
//        }
//    }

}
