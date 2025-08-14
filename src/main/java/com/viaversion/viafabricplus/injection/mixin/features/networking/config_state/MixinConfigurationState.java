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

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.protocols.v1_19_3to1_19_4.packet.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.storage.ConfigurationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ConfigurationState.class, remap = false)
public abstract class MixinConfigurationState {

    @Inject(method = "addPacketToQueue", at = @At("HEAD"), cancellable = true)
    private void cancelQueuedPackets(PacketWrapper wrapper, boolean clientbound, CallbackInfo ci) {
        // Dirty workaround for https://github.com/ViaVersion/ViaVersion/issues/4308 where queued packets
        // without set packet type cause a disconnect after sent in play state, even when delayed until the client
        // has transitioned to the play state as well. This is *very* bad as in worst will cause data loss and other
        // issues but /shrug for now.
        if (clientbound && wrapper.getPacketType() == null) {
            final int unmappedId = wrapper.getId();
            if (unmappedId == ClientboundPackets1_19_4.CONTAINER_CLOSE.getId() || unmappedId == ClientboundPackets1_19_4.CHUNKS_BIOMES.getId()) {
                ci.cancel();
            }
        }
    }

}
