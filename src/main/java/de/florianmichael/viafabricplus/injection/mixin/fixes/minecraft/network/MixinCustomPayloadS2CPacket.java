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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network;

import com.google.common.collect.ImmutableMap;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.util.Identifier;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(CustomPayloadS2CPacket.class)
public abstract class MixinCustomPayloadS2CPacket {

    @Unique
    private static final Map<Identifier, ProtocolVersion> viaFabricPlus$PAYLOAD_DIFF = ImmutableMap.<Identifier, ProtocolVersion>builder()
            .put(BrandCustomPayload.ID, LegacyProtocolVersion.c0_0_15a_1)
            .put(DebugGameTestAddMarkerCustomPayload.ID, ProtocolVersion.v1_14)
            .put(DebugGameTestClearCustomPayload.ID, ProtocolVersion.v1_14)
            .build();

    @Redirect(method = "readPayload", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private static Object filterAllowedCustomPayloads(Map<?, ?> instance, Object identifier) {
        if (instance.containsKey(identifier)) {
            if (!viaFabricPlus$PAYLOAD_DIFF.containsKey(identifier) || ProtocolTranslator.getTargetVersion().olderThan(viaFabricPlus$PAYLOAD_DIFF.get(identifier))) {
                return null;
            }

            final PacketByteBuf.PacketReader<? extends CustomPayload> reader = (PacketByteBuf.PacketReader<? extends CustomPayload>) instance.get(identifier);
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20)) {
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
