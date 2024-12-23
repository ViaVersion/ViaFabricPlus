/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.fixes.vialegacy;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import com.viaversion.viafabricplus.features.viaversion.TeleportTracker1_7_6_10;
import net.raphimc.vialegacy.protocol.release.r1_7_2_5tor1_7_6_10.packet.ClientboundPackets1_7_2;
import net.raphimc.vialegacy.protocol.release.r1_7_2_5tor1_7_6_10.packet.ServerboundPackets1_7_2;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.Protocolr1_7_6_10Tor1_8;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocolr1_7_6_10Tor1_8.class, remap = false)
public abstract class MixinProtocolr1_7_6_10Tor1_8 extends AbstractProtocol<ClientboundPackets1_7_2, ClientboundPackets1_8, ServerboundPackets1_7_2, ServerboundPackets1_8> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    private void addTeleportTracker(CallbackInfo ci) {
        this.registerClientbound(ClientboundPackets1_7_2.PLAYER_POSITION, ClientboundPackets1_8.PLAYER_POSITION, new PacketHandlers() {
            @Override
            public void register() {
                map(Types.DOUBLE); // x
                map(Types.DOUBLE, Types.DOUBLE, stance -> stance - 1.62F); // y
                map(Types.DOUBLE); // z
                map(Types.FLOAT); // yaw
                map(Types.FLOAT); // pitch
                handler(wrapper -> {
                    final boolean onGround = wrapper.read(Types.BOOLEAN); // On Ground
                    final TeleportTracker1_7_6_10 teleportTracker = wrapper.user().get(TeleportTracker1_7_6_10.class);
                    if (teleportTracker != null) {
                        teleportTracker.setPending(onGround);
                    }

                    wrapper.write(Types.BYTE, (byte) 0); // flags
                });
            }
        }, true);
        this.registerServerbound(ServerboundPackets1_8.MOVE_PLAYER_POS_ROT, ServerboundPackets1_7_2.MOVE_PLAYER_POS_ROT, new PacketHandlers() {
            @Override
            public void register() {
                map(Types.DOUBLE); // x
                map(Types.DOUBLE); // y
                handler(wrapper -> wrapper.write(Types.DOUBLE, wrapper.get(Types.DOUBLE, 1) + 1.62)); // stance
                map(Types.DOUBLE); // z
                map(Types.FLOAT); // yaw
                map(Types.FLOAT); // pitch
                map(Types.BOOLEAN); // onGround
                handler(wrapper -> {
                    final TeleportTracker1_7_6_10 teleportTracker = wrapper.user().get(TeleportTracker1_7_6_10.class);
                    if (teleportTracker != null) {
                        Boolean pendingTeleport = teleportTracker.getPending();
                        if (pendingTeleport != null) {
                            wrapper.set(Types.BOOLEAN, 0, pendingTeleport);
                            teleportTracker.setPending(null);
                        }
                    }
                });
            }
        }, true);
    }

}
