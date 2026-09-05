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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_2;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {

    @Shadow
    public abstract Connection getConnection();

    @Unique
    private Packet<?> viaFabricPlus$teleportConfirmPacket;

    @Redirect(method = "handleMoveVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"))
    private double allowSmallValues(Vec3 instance, Vec3 vec) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            return Integer.MAX_VALUE;
        } else {
            return instance.distanceTo(vec);
        }
    }

    @WrapWithCondition(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    private boolean changePacketOrder(Connection instance, Packet<?> packet) {
        final boolean cancel = ViaFabricPlus.api().targetVersion().equalTo(ProtocolVersion.v1_21_2);
        if (cancel) {
            this.viaFabricPlus$teleportConfirmPacket = packet;
        }
        return !cancel;
    }

    @Inject(method = "handleMovePlayer", at = @At("RETURN"))
    private void changePacketOrder(ClientboundPlayerPositionPacket packet, CallbackInfo ci) {
        if (viaFabricPlus$teleportConfirmPacket != null) {
            this.getConnection().send(viaFabricPlus$teleportConfirmPacket);
            viaFabricPlus$teleportConfirmPacket = null;
        }
    }

    @WrapWithCondition(method = "handleMoveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/VecDeltaCodec;setBase(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private boolean dontHandleEntityPositionChange(VecDeltaCodec instance, Vec3 base) {
        return ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_2);
    }

    @Inject(method = "hasClientLoaded", at = @At("HEAD"), cancellable = true)
    private void alwaysLoadPlayer(CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            cir.setReturnValue(true);
        }
    }

}
