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

package com.viaversion.viafabricplus.protocoltranslator.protocol;

import com.viaversion.viafabricplus.features.entity.metadata_handling.WolfHealthTracker1_14_4;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.protocol.storage.BedrockJoinGameTracker;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.packet.provider.PacketTypesProvider;
import com.viaversion.viaversion.api.protocol.packet.provider.SimplePacketTypesProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ServerboundPackets1_21_6;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ClientboundConfigurationPackets1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundConfigurationPackets1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundPacket1_21_9;
import com.viaversion.viaversion.protocols.v1_21_9to1_21_11.packet.ClientboundPacket1_21_11;
import com.viaversion.viaversion.protocols.v1_21_9to1_21_11.packet.ClientboundPackets1_21_11;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.beta.b1_8_0_1tor1_0_0_1.types.Typesb1_8_0_1;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.types.Types1_2_4;
import net.raphimc.vialegacy.protocol.release.r1_4_2tor1_4_4_5.types.Types1_4_2;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.types.Types1_7_6;

import static com.viaversion.viaversion.util.ProtocolUtil.packetTypeMap;

public final class ViaFabricPlusProtocol extends AbstractProtocol<ClientboundPacket1_21_11, ClientboundPacket1_21_11, ServerboundPacket1_21_9, ServerboundPacket1_21_9> {

    public static final ViaFabricPlusProtocol INSTANCE = new ViaFabricPlusProtocol();

    public ViaFabricPlusProtocol() {
        super(ClientboundPacket1_21_11.class, ClientboundPacket1_21_11.class, ServerboundPacket1_21_9.class, ServerboundPacket1_21_9.class);
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        final ProtocolVersion serverVersion = ProtocolTranslator.getTargetVersion(connection.getChannel());

        // Add storages we need for different fixes here
        if (serverVersion.equals(BedrockProtocolVersion.bedrockLatest)) {
            connection.put(new BedrockJoinGameTracker());
        } else if (serverVersion.olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            connection.put(new WolfHealthTracker1_14_4());
        }
    }

    public ServerboundPacketType getSetCreativeModeSlot() {
        return packetTypesProvider.unmappedServerboundType(State.PLAY, "SET_CREATIVE_MODE_SLOT");
    }

    /**
     * Gets the ViaVersion item type for the target version in the serverbound direction (creative inventory action packet)
     *
     * @param targetVersion The target version
     * @return The ViaVersion item type
     */
    public Type<Item> getServerboundItemType(final ProtocolVersion targetVersion) {
        if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Typesb1_8_0_1.CREATIVE_ITEM;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return getClientboundItemType(targetVersion);
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_5)) {
            return VersionedTypes.V1_21_5.lengthPrefixedItem;
        } else {
            return VersionedTypes.V1_21_6.lengthPrefixedItem;
        }
    }

    /**
     * Gets the ViaVersion item type for the target version in the clientbound direction
     *
     * @param targetVersion The target version
     * @return The ViaVersion item type
     */
    public Type<Item> getClientboundItemType(final ProtocolVersion targetVersion) {
        if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Types1_4_2.NBTLESS_ITEM;
        } else if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
            return Types1_2_4.NBT_ITEM;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            return Types1_7_6.ITEM;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return Types.ITEM1_8;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_13_1)) {
            return Types.ITEM1_13;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_20)) {
            return Types.ITEM1_13_2;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            return Types.ITEM1_20_2;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            return VersionedTypes.V1_20_5.item;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            return VersionedTypes.V1_21.item;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            return VersionedTypes.V1_21_2.item;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return VersionedTypes.V1_21_4.item;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_5)) {
            return VersionedTypes.V1_21_5.item;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_7)) {
            return VersionedTypes.V1_21_6.item;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_9)) {
            return VersionedTypes.V1_21_9.item;
        } else {
            return VersionedTypes.V1_21_11.item;
        }
    }

    @Override
    protected PacketTypesProvider<ClientboundPacket1_21_11, ClientboundPacket1_21_11, ServerboundPacket1_21_9, ServerboundPacket1_21_9> createPacketTypesProvider() {
        return new SimplePacketTypesProvider<>(
            packetTypeMap(unmappedClientboundPacketType, ClientboundPackets1_21_11.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedClientboundPacketType, ClientboundPackets1_21_11.class, ClientboundConfigurationPackets1_21_9.class),
            packetTypeMap(mappedServerboundPacketType, ServerboundPackets1_21_6.class, ServerboundConfigurationPackets1_21_9.class),
            packetTypeMap(unmappedServerboundPacketType, ServerboundPackets1_21_6.class, ServerboundConfigurationPackets1_21_9.class)
        );
    }

}
