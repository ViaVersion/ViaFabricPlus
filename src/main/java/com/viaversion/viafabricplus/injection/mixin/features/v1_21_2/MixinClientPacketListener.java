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
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPackets26_1;
import com.viaversion.viaversion.protocols.v26_2to26_3.Protocol26_2To26_3;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
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
    private ServerboundAcceptTeleportationPacket viaFabricPlus$teleportConfirmPacket;

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
            this.viaFabricPlus$teleportConfirmPacket = (ServerboundAcceptTeleportationPacket) packet;
        }
        return !cancel;
    }

    @Inject(method = "handleMovePlayer", at = @At("RETURN"))
    private void changePacketOrder(ClientboundPlayerPositionPacket packet, CallbackInfo ci) {
        if (viaFabricPlus$teleportConfirmPacket != null) {
            final ServerboundAcceptTeleportationPacket confirm = viaFabricPlus$teleportConfirmPacket;
            viaFabricPlus$teleportConfirmPacket = null;
            this.getConnection().send(new ServerboundMovePlayerPacket.PosRot(confirm.x(), confirm.y(), confirm.z(), confirm.yRot(), confirm.xRot(), false, false));

            // The 26.3 packet also carries the position and would be split into confirm + move again, send the plain 26.2 confirm instead
            final PacketWrapper acceptTeleportation = PacketWrapper.create(ServerboundPackets26_1.ACCEPT_TELEPORTATION, ViaFabricPlus.api().userConnection());
            acceptTeleportation.write(Types.VAR_INT, confirm.id());
            acceptTeleportation.scheduleSendToServer(Protocol26_2To26_3.class);
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
