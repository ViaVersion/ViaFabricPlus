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

package com.viaversion.viafabricplus.fixes.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.packet.ServerboundPackets1_21_4;
import com.viaversion.viaversion.protocols.v1_21to1_21_2.packet.ClientboundPackets1_21_2;
import com.viaversion.viaversion.util.Key;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.HashMap;
import java.util.Map;

// Protocol to handle error handling changes in older protocols, always last element of the pipeline
public class ViaFabricPlusProtocol extends AbstractSimpleProtocol {

    public static final ViaFabricPlusProtocol INSTANCE = new ViaFabricPlusProtocol();

    private final Map<String, Pair<ProtocolVersion, PacketReader>> payloadDiff = new HashMap<>();

    public ViaFabricPlusProtocol() {
        registerMapping(BrandCustomPayload.ID, LegacyProtocolVersion.c0_0_15a_1, wrapper -> wrapper.passthrough(Types.STRING));
        registerMapping(DebugGameTestAddMarkerCustomPayload.ID, ProtocolVersion.v1_14, wrapper -> {
            wrapper.passthrough(Types.BLOCK_POSITION1_14);
            wrapper.passthrough(Types.INT);
            wrapper.passthrough(Types.STRING);
            wrapper.passthrough(Types.INT);
        });
        registerMapping(DebugGameTestClearCustomPayload.ID, ProtocolVersion.v1_14, wrapper -> {
        });
    }

    @Override
    protected void registerPackets() {
        registerClientbound(State.PLAY, getCustomPayload().getId(), getCustomPayload().getId(), wrapper -> {
            final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
            if (!channel.startsWith(Identifier.DEFAULT_NAMESPACE)) {
                // Mods might add custom payloads that we don't want to filter, so we check for the namespace.
                // Mods should NEVER use the default namespace of the game, not only to not break this code,
                // but also to not break other mods and the game itself.
                return;
            }

            final ProtocolVersion version = wrapper.user().getProtocolInfo().serverProtocolVersion();
            if (!payloadDiff.containsKey(channel) || version.olderThan(payloadDiff.get(channel).getLeft())) {
                // Technically, it's wrong to just drop all payloads. However, ViaVersion doesn't translate them and the server can't detect if
                // we handled the payload or not, so dropping them is easier than adding a bunch of useless translations for payloads
                // which don't do anything on the client anyway.
                wrapper.cancel();
                return;
            }

            if (version.olderThanOrEqualTo(ProtocolVersion.v1_20)) {
                // Skip all remaining bytes after reading the payload and cancel if the payload fails to read
                final PacketReader reader = payloadDiff.get(channel).getRight();
                try {
                    reader.read(wrapper);
                    wrapper.read(Types.REMAINING_BYTES);
                } catch (Exception ignored) {
                    wrapper.cancel();
                }
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        final ProtocolVersion serverVersion = ProtocolTranslator.getTargetVersion(connection.getChannel());

        // Add storages we need for different fixes here
        if (serverVersion.equals(BedrockProtocolVersion.bedrockLatest)) {
            connection.put(new BedrockJoinGameTracker());
        } else {
            if (serverVersion.olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
                connection.put(new WolfHealthTracker1_14_4());
            }
            if (serverVersion.olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
                connection.put(new TeleportTracker1_7_6_10());
            }
        }
    }

    private void registerMapping(final CustomPayload.Id<?> id, final ProtocolVersion version, final PacketReader reader) {
        payloadDiff.put(id.id().toString(), new Pair<>(version, reader));
    }

    public static ServerboundPacketType getSetCreativeModeSlot() {
        return ServerboundPackets1_21_4.SET_CREATIVE_MODE_SLOT;
    }

    public static ClientboundPacketType getCustomPayload() {
        return ClientboundPackets1_21_2.CUSTOM_PAYLOAD;
    }

    @FunctionalInterface
    interface PacketReader {

        void read(PacketWrapper wrapper);

    }

}
