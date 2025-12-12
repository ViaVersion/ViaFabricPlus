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

package com.viaversion.viafabricplus.injection.mixin.features.movement.vehicle;

import com.mojang.authlib.GameProfile;
import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.packet.ServerboundPackets1_20_5;
import com.viaversion.viaversion.protocols.v1_21to1_21_2.Protocol1_21To1_21_2;
import com.viaversion.viaversion.protocols.v1_21to1_21_2.storage.ClientVehicleStorage;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.release.r1_5_2tor1_6_1.Protocolr1_5_2Tor1_6_1;
import net.raphimc.vialegacy.protocol.release.r1_5_2tor1_6_1.packet.ServerboundPackets1_5_2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {

    @Shadow
    @Final
    public ClientPacketListener connection;

    public MixinLocalPlayer(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z", ordinal = 0))
    private boolean removeVehicleRequirement(LocalPlayer instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20) && instance.isPassenger();
    }

    @Inject(method = "startRiding", at = @At("RETURN"))
    private void setRotationsWhenInBoat(Entity entity, boolean force, boolean emitEvent, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && entity instanceof Boat && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_18)) {
            this.yRotO = entity.getYRot();
            this.setYRot(entity.getYRot());
            this.setYHeadRot(entity.getYRot());
        }
    }

    @Redirect(method = "tick", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
    private void modifyPositionPacket(ClientPacketListener instance, Packet<?> packet) {
        if (ProtocolTranslator.getTargetVersion().newerThan(LegacyProtocolVersion.r1_5_2)) {
            instance.send(packet);
            return;
        }

        final UserConnection connection = ((IConnection) this.connection.getConnection()).viaFabricPlus$getUserConnection();
        connection.getChannel().eventLoop().execute(() -> {
            final PacketWrapper movePlayerPosRot = PacketWrapper.create(ServerboundPackets1_5_2.MOVE_PLAYER_POS_ROT, connection);
            movePlayerPosRot.write(Types.DOUBLE, this.getDeltaMovement().x); // x
            movePlayerPosRot.write(Types.DOUBLE, -999.0D); // y
            movePlayerPosRot.write(Types.DOUBLE, -999.0D); // stance
            movePlayerPosRot.write(Types.DOUBLE, this.getDeltaMovement().z); // z
            movePlayerPosRot.write(Types.FLOAT, this.getYRot()); // yaw
            movePlayerPosRot.write(Types.FLOAT, this.getXRot()); // pitch
            movePlayerPosRot.write(Types.BOOLEAN, this.onGround()); // onGround
            movePlayerPosRot.sendToServer(Protocolr1_5_2Tor1_6_1.class);

            // Copied from the 1.21->1.21.2 protocol since it's changing the packet order, and we manually send the movement packet here
            final ClientVehicleStorage vehicleStorage = connection.get(ClientVehicleStorage.class);
            if (vehicleStorage == null) {
                return;
            }

            final PacketWrapper playerInput = PacketWrapper.create(ServerboundPackets1_20_5.PLAYER_INPUT, connection);
            playerInput.write(Types.FLOAT, vehicleStorage.sidewaysMovement());
            playerInput.write(Types.FLOAT, vehicleStorage.forwardMovement());
            playerInput.write(Types.BYTE, vehicleStorage.flags());
            playerInput.sendToServer(Protocol1_21To1_21_2.class);
        });
    }

}
