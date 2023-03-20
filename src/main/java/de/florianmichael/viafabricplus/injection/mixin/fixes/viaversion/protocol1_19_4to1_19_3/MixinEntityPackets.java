/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_19_4to1_19_3;

import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.ClientboundPackets1_19_4;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.Protocol1_19_4To1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_4to1_19_3.packets.EntityPackets;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import de.florianmichael.viafabricplus.definition.v1_19_4.DismountRequestTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPackets.class, remap = false)
public abstract class MixinEntityPackets extends EntityRewriter<ClientboundPackets1_19_3, Protocol1_19_4To1_19_3> {

    public MixinEntityPackets(Protocol1_19_4To1_19_3 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixPlayerPosition(CallbackInfo ci) {
        protocol.registerClientbound(ClientboundPackets1_19_3.PLAYER_POSITION, ClientboundPackets1_19_4.PLAYER_POSITION, new PacketHandlers() {
            @Override
            protected void register() {
                map(Type.DOUBLE); // X
                map(Type.DOUBLE); // Y
                map(Type.DOUBLE); // Z
                map(Type.FLOAT); // Yaw
                map(Type.FLOAT); // Pitch
                map(Type.BYTE); // Relative arguments
                map(Type.VAR_INT); // Id
                handler(wrapper -> {
                    final boolean dismountVehicle = wrapper.read(Type.BOOLEAN);
                    wrapper.user().get(DismountRequestTracker.class).getDismountRequests().add(dismountVehicle);
                });
            }
        }, true);
    }
}
