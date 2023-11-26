/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network;

import com.google.common.collect.ImmutableMap;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.util.Identifier;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinCustomPayloadS2CPacket {

    @Unique
    private static final Map<Identifier, VersionEnum> PAYLOAD_DIFF = ImmutableMap.<Identifier, VersionEnum>builder()
            .put(BrandCustomPayload.ID, VersionEnum.c0_0_15a_1)
            .put(DebugGameTestAddMarkerCustomPayload.ID, VersionEnum.r1_14)
            .put(DebugGameTestClearCustomPayload.ID, VersionEnum.r1_14)
            .build();

    @Redirect(method = "readPayload", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private static Object filterAllowedCustomPayloads(Map<?, ?> instance, Object identifier) {
        if (instance.containsKey(identifier)) {
            if (!PAYLOAD_DIFF.containsKey(identifier) || ProtocolHack.getTargetVersion().isOlderThan(PAYLOAD_DIFF.get(identifier))) {
                return null;
            }

            final PacketByteBuf.PacketReader<? extends CustomPayload> reader = (PacketByteBuf.PacketReader<? extends CustomPayload>) instance.get(identifier);
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
                return (PacketByteBuf.PacketReader<? extends CustomPayload>) packetByteBuf -> {
                    final CustomPayload result = reader.apply(packetByteBuf);
                    packetByteBuf.skipBytes(packetByteBuf.readableBytes());
                    return result;
                };
            } else {
                return reader;
            }
        }

        return null;
    }

}
