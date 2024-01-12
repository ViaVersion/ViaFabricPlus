/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.WorldPackets;
import de.florianmichael.viafabricplus.fixes.versioned.classic.WorldHeightSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings({"unchecked", "rawtypes"})
@Mixin(value = WorldPackets.class, remap = false)
public abstract class MixinWorldPackets1_17 {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_17to1_16_4/Protocol1_17To1_16_4;registerClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;Lcom/viaversion/viaversion/api/protocol/remapper/PacketHandler;)V"))
    private static void handleClassicWorldHeight(Protocol1_17To1_16_4 instance, ClientboundPacketType packetType, PacketHandler packetHandler) {
        if (packetType == ClientboundPackets1_16_2.CHUNK_DATA) packetHandler = WorldHeightSupport.handleChunkData(packetHandler);
        if (packetType == ClientboundPackets1_16_2.UPDATE_LIGHT) packetHandler = WorldHeightSupport.handleUpdateLight(packetHandler);

        ((Protocol) instance).registerClientbound(packetType, packetHandler);
    }

}
