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

package de.florianmichael.viafabricplus.fixes;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_20_5to1_20_3.packet.ClientboundPackets1_20_5;
import com.viaversion.viaversion.protocols.protocol1_20_5to1_20_3.packet.ServerboundPackets1_20_5;
import com.viaversion.viaversion.util.Key;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.HashMap;
import java.util.Map;

// Protocol to handle error handling changes in older protocols, always last element of the pipeline
public class VFPProtocol extends AbstractSimpleProtocol {

    public static final VFPProtocol INSTANCE = new VFPProtocol();

    private final Map<String, Pair<ProtocolVersion, PacketReader>> payloadDiff = new HashMap<>();

    public VFPProtocol() {
        registerMapping(BrandCustomPayload.ID, LegacyProtocolVersion.c0_0_15a_1, wrapper -> wrapper.passthrough(Type.STRING));
        registerMapping(DebugGameTestAddMarkerCustomPayload.ID, ProtocolVersion.v1_14, wrapper -> {
            wrapper.passthrough(Type.POSITION1_14);
            wrapper.passthrough(Type.INT);
            wrapper.passthrough(Type.STRING);
            wrapper.passthrough(Type.INT);
        });
        registerMapping(DebugGameTestClearCustomPayload.ID, ProtocolVersion.v1_14, wrapper -> {});
    }

    @Override
    protected void registerPackets() {
        registerClientbound(State.PLAY, getPluginMessagePacket().getId(), getPluginMessagePacket().getId(), wrapper -> {
            final String channel = Key.namespaced(wrapper.passthrough(Type.STRING));
            if (!channel.startsWith(Identifier.DEFAULT_NAMESPACE)) {
                // Mods might add custom payloads that we don't want to filter, so we check for the namespace.
                // Mods should NEVER use the default namespace of the game, not only to not break this code,
                // but also to not break other mods and the game itself.
                return;
            }

            final ProtocolVersion version = wrapper.user().getProtocolInfo().serverProtocolVersion();
            if (!payloadDiff.containsKey(channel) || version.olderThan(payloadDiff.get(channel).getLeft())) {
                // Technically it's wrong to just drop all payloads, but ViaVersion doesn't translate them and the server can't detect if
                // we handled the payload or not, so dropping them is easier than adding a bunch of useless translations for payloads
                // which doesn't do anything on the client anyway.
                wrapper.cancel();
                return;
            }

            if (version.olderThanOrEqualTo(ProtocolVersion.v1_20)) {
                // Skip remaining bytes after reading the payload and cancel if the payload fails to read
                final PacketReader reader = payloadDiff.get(channel).getRight();
                try {
                    reader.read(wrapper);
                    wrapper.read(Type.REMAINING_BYTES);
                } catch (Exception ignored) {
                    wrapper.cancel();
                }
            }
        });
    }

    private void registerMapping(final CustomPayload.Id<?> id, final ProtocolVersion version, final PacketReader reader) {
        payloadDiff.put(id.id().toString(), new Pair<>(version, reader));
    }

    public static ServerboundPacketType getCreativeInventoryActionPacket() {
        return ServerboundPackets1_20_5.CREATIVE_INVENTORY_ACTION;
    }

    public static ClientboundPacketType getPluginMessagePacket() {
        return ClientboundPackets1_20_5.PLUGIN_MESSAGE;
    }

    @FunctionalInterface
    interface PacketReader {

        void read(PacketWrapper wrapper) throws Exception;

    }
}
