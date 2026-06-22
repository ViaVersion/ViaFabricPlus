/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.protocoltranslator.protocol;

import com.google.common.collect.Lists;
import com.viaversion.viafabricplus.features.entity.metadata.WolfHealthTracker1_14_4;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.packet.provider.PacketTypesProvider;
import com.viaversion.viaversion.api.protocol.packet.provider.SimplePacketTypesProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPacket26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPackets26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPacket26_1;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ServerboundPackets26_1;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ClientboundConfigurationPackets1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundConfigurationPackets1_21_9;
import com.viaversion.viaversion.util.Key;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.beta.b1_8_0_1tor1_0_0_1.types.Typesb1_8_0_1;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.types.Types1_2_4;
import net.raphimc.vialegacy.protocol.release.r1_4_2tor1_4_4_5.types.Types1_4_2;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.types.Types1_7_6;

import static com.viaversion.viaversion.util.ProtocolUtil.packetTypeMap;

public final class ViaFabricPlusProtocol extends AbstractProtocol<ClientboundPacket26_1, ClientboundPacket26_1, ServerboundPacket26_1, ServerboundPacket26_1> {

    public static final ViaFabricPlusProtocol INSTANCE = new ViaFabricPlusProtocol();

    private static final NavigableMap<ProtocolVersion, ItemTypes> ITEM_TYPES = new TreeMap<>();

    static {
        ITEM_TYPES.put(LegacyProtocolVersion.b1_8tob1_8_1, new ItemTypes(Types1_4_2.NBTLESS_ITEM, Typesb1_8_0_1.CREATIVE_ITEM));
        ITEM_TYPES.put(LegacyProtocolVersion.r1_2_4tor1_2_5, ItemTypes.same(Types1_2_4.NBT_ITEM));
        ITEM_TYPES.put(ProtocolVersion.v1_7_6, ItemTypes.same(Types1_7_6.ITEM));
        ITEM_TYPES.put(ProtocolVersion.v1_12_2, ItemTypes.same(Types.ITEM1_8));
        ITEM_TYPES.put(ProtocolVersion.v1_13_1, ItemTypes.same(Types.ITEM1_13));
        ITEM_TYPES.put(ProtocolVersion.v1_20, ItemTypes.same(Types.ITEM1_13_2));
        ITEM_TYPES.put(ProtocolVersion.v1_20_3, ItemTypes.same(Types.ITEM1_20_2));
        ITEM_TYPES.put(ProtocolVersion.v1_20_5, ItemTypes.same(VersionedTypes.V1_20_5.item));
        ITEM_TYPES.put(ProtocolVersion.v1_21, ItemTypes.same(VersionedTypes.V1_21.item));
        ITEM_TYPES.put(ProtocolVersion.v1_21_2, ItemTypes.same(VersionedTypes.V1_21_2.item));
        ITEM_TYPES.put(ProtocolVersion.v1_21_4, ItemTypes.same(VersionedTypes.V1_21_4.item));
        ITEM_TYPES.put(ProtocolVersion.v1_21_5, new ItemTypes(VersionedTypes.V1_21_5.item, VersionedTypes.V1_21_5.lengthPrefixedItem));
        ITEM_TYPES.put(ProtocolVersion.v1_21_7, new ItemTypes(VersionedTypes.V1_21_6.item, VersionedTypes.V1_21_6.lengthPrefixedItem));
        ITEM_TYPES.put(ProtocolVersion.v1_21_9, new ItemTypes(VersionedTypes.V1_21_9.item, VersionedTypes.V1_21_9.lengthPrefixedItem));
        ITEM_TYPES.put(ProtocolVersion.v1_21_11, new ItemTypes(VersionedTypes.V1_21_11.item, VersionedTypes.V1_21_11.lengthPrefixedItem));
        ITEM_TYPES.put(ProtocolVersion.v26_1, new ItemTypes(VersionedTypes.V26_1.item, VersionedTypes.V26_1.lengthPrefixedItem));
        ITEM_TYPES.put(ProtocolVersion.v26_2, new ItemTypes(VersionedTypes.V26_2.item, VersionedTypes.V26_2.lengthPrefixedItem));

        if (!ITEM_TYPES.containsKey(ProtocolTranslator.NATIVE_VERSION)) {
            throw new IllegalStateException("Missing item type for native version");
        }
    }

    public ViaFabricPlusProtocol() {
        super(ClientboundPacket26_1.class, ClientboundPacket26_1.class, ServerboundPacket26_1.class, ServerboundPacket26_1.class);
    }

    @Override
    protected void registerPackets() {
        // Fixes an issue where the Fabric Particle API causes disconnects when both the client and server have the mod installed and both are 1.21.5+.
        // See https://github.com/ViaVersion/ViaFabric/issues/428
        this.registerServerbound(ServerboundConfigurationPackets1_21_9.CUSTOM_PAYLOAD, wrapper -> {
            final ProtocolVersion serverVersion = wrapper.user().getProtocolInfo().serverProtocolVersion();
            if (serverVersion.newerThanOrEqualTo(ProtocolVersion.v1_21_5) && !serverVersion.equals(wrapper.user().getProtocolInfo().protocolVersion())) {
                final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
                if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                    final List<String> channels = Lists.newArrayList(new String(wrapper.passthrough(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA), StandardCharsets.UTF_8).split("\0"));
                    if (channels.remove("fabric:extended_block_state_particle_effect_sync")) {
                        if (!channels.isEmpty()) {
                            wrapper.set(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA, 0, String.join("\0", channels).getBytes(StandardCharsets.UTF_8));
                        } else {
                            wrapper.cancel();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        final ProtocolVersion serverVersion = ProtocolTranslator.getTargetVersion(connection.getChannel()); // UserConnection#getProtocolInfo is not initialized yet
        if (serverVersion.olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            connection.put(new WolfHealthTracker1_14_4());
        }
    }

    @Override
    protected void applySharedRegistrations() {
        // Not for us, protocols will already track states down the line
    }

    public ClientboundPacketType getClientboundCustomPayloadPacketType() {
        return packetTypesProvider.unmappedClientboundType(State.PLAY, "CUSTOM_PAYLOAD");
    }

    public ServerboundPacketType getCustomPayloadPacketType() {
        return packetTypesProvider.unmappedServerboundType(State.PLAY, "CUSTOM_PAYLOAD");
    }

    public ServerboundPacketType getSetCreativeModeSlot() {
        return packetTypesProvider.unmappedServerboundType(State.PLAY, "SET_CREATIVE_MODE_SLOT");
    }

    public Type<Item> getServerboundItemType(final ProtocolVersion targetVersion) {
        final Map.Entry<ProtocolVersion, ItemTypes> entry = ITEM_TYPES.ceilingEntry(targetVersion);
        return entry.getValue().serverbound();
    }

    public Type<Item> getClientboundItemType(final ProtocolVersion targetVersion) {
        final Map.Entry<ProtocolVersion, ItemTypes> entry = ITEM_TYPES.ceilingEntry(targetVersion);
        return entry.getValue().clientbound();
    }

    @Override
    protected PacketTypesProvider<ClientboundPacket26_1, ClientboundPacket26_1, ServerboundPacket26_1, ServerboundPacket26_1> createPacketTypesProvider() {
        return new SimplePacketTypesProvider<>(
            packetTypeMap(unmappedClientboundPacketType, ClientboundPackets26_1.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedClientboundPacketType, ClientboundPackets26_1.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedServerboundPacketType, ServerboundPackets26_1.class, ServerboundConfigurationPackets1_21_9.class),
            packetTypeMap(unmappedServerboundPacketType, ServerboundPackets26_1.class, ServerboundConfigurationPackets1_21_9.class)
        );
    }

    private record ItemTypes(Type<Item> clientbound, Type<Item> serverbound) {

        static ItemTypes same(final Type<Item> type) {
            return new ItemTypes(type, type);
        }

    }

}
