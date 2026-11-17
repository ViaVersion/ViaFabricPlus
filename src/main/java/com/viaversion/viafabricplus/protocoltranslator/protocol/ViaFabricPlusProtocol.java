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
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation;
import com.viaversion.viafabricplus.features.v1_21_5.WolfHealthTracker1_14_4;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslationImpl;
import com.viaversion.viafabricplus.protocoltranslator.protocol.storage.HorizonSessionStorage;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
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
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ClientboundConfigurationPackets26_3;
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ClientboundPacket26_3;
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ClientboundPackets26_3;
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ServerboundPacket26_3;
import com.viaversion.viaversion.protocols.v26_2to26_3.packet.ServerboundPackets26_3;
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

public final class ViaFabricPlusProtocol extends AbstractProtocol<ClientboundPacket26_3, ClientboundPacket26_3, ServerboundPacket26_3, ServerboundPacket26_3> {

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
        ITEM_TYPES.put(ProtocolVersion.v26_3, new ItemTypes(VersionedTypes.V26_3.item, VersionedTypes.V26_3.lengthPrefixedItem));
        ITEM_TYPES.put(ProtocolTranslation.HORIZON_26_3, new ItemTypes(VersionedTypes.V26_3.item, VersionedTypes.V26_3.lengthPrefixedItem));

        if (!ITEM_TYPES.containsKey(ProtocolTranslationImpl.NATIVE_VERSION)) {
            throw new IllegalStateException("Missing item type for native version");
        }
    }

    public ViaFabricPlusProtocol() {
        super(ClientboundPacket26_3.class, ClientboundPacket26_3.class, ServerboundPacket26_3.class, ServerboundPacket26_3.class);
    }

    private static boolean isHorizon(final UserConnection user) {
        return ProtocolTranslation.HORIZON_26_3.equals(user.getProtocolInfo().serverProtocolVersion());
    }

    private static void sendConfigurationCustomPayload(final UserConnection user, final String channel, final byte[] data) {
        final PacketWrapper payload = PacketWrapper.create(ServerboundConfigurationPackets1_21_9.CUSTOM_PAYLOAD, user);
        payload.write(Types.STRING, channel);
        payload.write(Types.REMAINING_BYTES, data);
        payload.sendToServer(ViaFabricPlusProtocol.class);
    }

    /**
     * Rewrites client brand to {@code "horizon"} and cancels all non-Horizon serverbound custom payloads.
     */
    private static void handleHorizonServerboundCustomPayload(final PacketWrapper wrapper) {
        final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
        wrapper.read(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA);
        if (channel.equals("minecraft:brand")) {
            wrapper.write(Types.STRING, "horizon");
        } else {
            wrapper.cancel();
        }
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundConfigurationPackets26_3.FINISH_CONFIGURATION, wrapper -> {
            if (isHorizon(wrapper.user())) {
                wrapper.user().getProtocolInfo().setServerState(State.PLAY);
            }
        });
        this.registerServerbound(ServerboundConfigurationPackets1_21_9.FINISH_CONFIGURATION, wrapper -> {
            if (isHorizon(wrapper.user())) {
                wrapper.user().getProtocolInfo().setClientState(State.PLAY);
            }
        });
        this.registerClientbound(ClientboundPackets26_3.START_CONFIGURATION, wrapper -> {
            if (isHorizon(wrapper.user())) {
                wrapper.user().getProtocolInfo().setServerState(State.CONFIGURATION);
                final HorizonSessionStorage storage = wrapper.user().get(HorizonSessionStorage.class);
                if (storage != null) {
                    storage.resetConfiguration();
                }
            }
        });
        this.registerServerbound(ServerboundPackets26_3.CONFIGURATION_ACKNOWLEDGED, wrapper -> {
            if (isHorizon(wrapper.user())) {
                wrapper.user().getProtocolInfo().setClientState(State.CONFIGURATION);
            }
        });

        // Fixes an issue where the Fabric Particle API causes disconnects when both the client and server have the mod installed and both are 1.21.5+.
        // See https://github.com/ViaVersion/ViaFabric/issues/428
        this.registerServerbound(ServerboundConfigurationPackets1_21_9.CUSTOM_PAYLOAD, wrapper -> {
            if (isHorizon(wrapper.user())) {
                handleHorizonServerboundCustomPayload(wrapper);
                return;
            }

            final ProtocolVersion serverVersion = wrapper.user().getProtocolInfo().serverProtocolVersion();
            if (serverVersion.newerThanOrEqualTo(ProtocolVersion.v1_21_5) && !serverVersion.equals(wrapper.user().getProtocolInfo().protocolVersion())) {
                final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
                if (channel.equals("minecraft:register") || channel.equals("minecraft:unregister")) {
                    final List<String> channels = Lists.newArrayList(new String(wrapper.passthrough(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA), StandardCharsets.UTF_8).split("\0"));
                    if (channels.remove("fabric:extended_block_particle_option_sync")) {
                        if (!channels.isEmpty()) {
                            wrapper.set(Types.SERVERBOUND_CUSTOM_PAYLOAD_DATA, 0, String.join("\0", channels).getBytes(StandardCharsets.UTF_8));
                        } else {
                            wrapper.cancel();
                        }
                    }
                }
            }
        });

        this.registerServerbound(ServerboundConfigurationPackets1_21_9.SELECT_KNOWN_PACKS, wrapper -> {
            if (isHorizon(wrapper.user())) {
                final HorizonSessionStorage storage = wrapper.user().get(HorizonSessionStorage.class);
                if (storage != null) {
                    if (storage.markPayloadsOfferSent()) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:payloads", new byte[]{1, 1, 0});
                    }
                    if (storage.markModListSent()) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:mod_list", new byte[]{0});
                    }
                }
            }
        });

        this.registerClientbound(ClientboundConfigurationPackets26_3.CUSTOM_PAYLOAD, wrapper -> {
            if (isHorizon(wrapper.user())) {
                final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
                final HorizonSessionStorage storage = wrapper.user().get(HorizonSessionStorage.class);
                if (channel.equals("horizon:payloads")) {
                    final byte[] data = wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                    if (data.length >= 2 && data[0] == 0 && storage != null && storage.markPayloadsOfferSent()) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:payloads", new byte[]{1, data[1], 0});
                    }
                } else if (channel.equals("horizon:mod_list")) {
                    final byte[] data = wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                    if (storage != null && storage.markModListSent()) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:mod_list", data.length > 0 ? data : new byte[]{0});
                    }
                } else if (channel.equals("horizon:block_registry")) {
                    final byte[] data = wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                    if (data.length == 32) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:block_registry", data);
                    }
                } else if (channel.equals("horizon:levels")) {
                    final byte[] data = wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                    if (data.length >= 2 && data[0] == 0) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:levels", new byte[]{1, data[1], 0});
                    }
                } else if (channel.equals("horizon:menu_extra")) {
                    final byte[] data = wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                    if (data.length >= 2 && data[0] == 0) {
                        sendConfigurationCustomPayload(wrapper.user(), "horizon:menu_extra", new byte[]{1, data[1]});
                    }
                } else if (channel.startsWith("horizon:")) {
                    wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                }
            }
        });

        this.registerClientbound(ClientboundPackets26_3.CUSTOM_PAYLOAD, wrapper -> {
            if (isHorizon(wrapper.user())) {
                final String channel = Key.namespaced(wrapper.passthrough(Types.STRING));
                if (channel.startsWith("horizon:")) {
                    wrapper.read(Types.REMAINING_BYTES);
                    wrapper.cancel();
                }
            }
        });

        this.registerServerbound(ServerboundPackets26_3.CUSTOM_PAYLOAD, wrapper -> {
            if (isHorizon(wrapper.user())) {
                handleHorizonServerboundCustomPayload(wrapper);
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        // UserConnection#getProtocolInfo is not initialized yet
        final ProtocolVersion serverVersion = ViaFabricPlus.api().protocolTranslation().targetVersion(connection.getChannel());
        if (serverVersion.olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            connection.put(new WolfHealthTracker1_14_4());
        } else if (serverVersion.equals(ProtocolTranslation.HORIZON_26_3)) {
            connection.put(new HorizonSessionStorage());
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
    protected PacketTypesProvider<ClientboundPacket26_3, ClientboundPacket26_3, ServerboundPacket26_3, ServerboundPacket26_3> createPacketTypesProvider() {
        return new SimplePacketTypesProvider<>(
            packetTypeMap(unmappedClientboundPacketType, ClientboundPackets26_3.class, ClientboundConfigurationPackets26_3.class),
            packetTypeMap(mappedClientboundPacketType, ClientboundPackets26_3.class, ClientboundConfigurationPackets26_3.class),
            packetTypeMap(mappedServerboundPacketType, ServerboundPackets26_3.class, ServerboundConfigurationPackets1_21_9.class),
            packetTypeMap(unmappedServerboundPacketType, ServerboundPackets26_3.class, ServerboundConfigurationPackets1_21_9.class)
        );
    }

    private record ItemTypes(Type<Item> clientbound, Type<Item> serverbound) {

        static ItemTypes same(final Type<Item> type) {
            return new ItemTypes(type, type);
        }

    }

}
