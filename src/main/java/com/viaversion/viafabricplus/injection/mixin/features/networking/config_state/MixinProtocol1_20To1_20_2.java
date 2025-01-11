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

package com.viaversion.viafabricplus.injection.mixin.features.networking.config_state;

import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.v1_19_3to1_19_4.packet.ServerboundPackets1_19_4;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.Protocol1_20To1_20_2;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.packet.ServerboundPackets1_20_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocol1_20To1_20_2.class, remap = false)
public abstract class MixinProtocol1_20To1_20_2 {

    @Inject(method = "lambda$queueServerboundPacket$12", at = @At("HEAD"), cancellable = true)
    private static void dontQueueConfigPackets(ServerboundPackets1_20_2 packetType, PacketWrapper wrapper, CallbackInfo ci) {
        if (!DebugSettings.INSTANCE.queueConfigPackets.getValue()) {
            ci.cancel();
            switch (packetType) {
                case CUSTOM_PAYLOAD -> wrapper.setPacketType(ServerboundPackets1_19_4.CUSTOM_PAYLOAD);
                case KEEP_ALIVE -> wrapper.setPacketType(ServerboundPackets1_19_4.KEEP_ALIVE);
                case PONG -> wrapper.setPacketType(ServerboundPackets1_19_4.PONG);
                default -> throw new IllegalStateException("Unexpected packet type: " + packetType);
            }
        }
    }

}
