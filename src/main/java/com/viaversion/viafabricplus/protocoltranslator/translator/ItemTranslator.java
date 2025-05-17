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

package com.viaversion.viafabricplus.protocoltranslator.translator;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import com.viaversion.viaversion.protocols.v1_12to1_12_1.packet.ClientboundPackets1_12_1;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.beta.b1_8_0_1tor1_0_0_1.types.Typesb1_8_0_1;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.types.Types1_2_4;
import net.raphimc.vialegacy.protocol.release.r1_4_2tor1_4_4_5.types.Types1_4_2;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.types.Types1_7_6;

public final class ItemTranslator {

    public static Item mcToVia(final ItemStack stack, final ProtocolVersion targetVersion) {
        final UserConnection connection = ProtocolTranslator.createDummyUserConnection(ProtocolTranslator.NATIVE_VERSION, targetVersion);

        try {
            final RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), MinecraftClient.getInstance().getNetworkHandler().getRegistryManager());
            buf.writeShort(0); // slot
            ItemStack.LENGTH_PREPENDED_OPTIONAL_PACKET_CODEC.encode(buf, stack); // item

            final PacketWrapper setCreativeModeSlot = PacketWrapper.create(ViaFabricPlusProtocol.getSetCreativeModeSlot(), buf, connection);
            connection.getProtocolInfo().getPipeline().transform(Direction.SERVERBOUND, State.PLAY, setCreativeModeSlot);

            setCreativeModeSlot.read(Types.SHORT); // slot
            return setCreativeModeSlot.read(getServerboundItemType(targetVersion)); // item
        } catch (Throwable t) {
            ViaFabricPlusImpl.INSTANCE.getLogger().error("Error converting native item stack to ViaVersion {} item stack", targetVersion, t);
            return null;
        }
    }

    public static ItemStack viaToMc(final Item item, final ProtocolVersion sourceVersion) {
        final UserConnection connection = ProtocolTranslator.createDummyUserConnection(ProtocolTranslator.NATIVE_VERSION, sourceVersion);

        try {
            final Protocol<?, ?, ?, ?> sourceProtocol = connection.getProtocolInfo().getPipeline().reversedPipes().stream().filter(p -> !p.isBaseProtocol()).findFirst().orElseThrow();
            final PacketWrapper containerSetSlot = PacketWrapper.create(sourceProtocol.getPacketTypesProvider().unmappedClientboundType(State.PLAY, ClientboundPackets1_12_1.CONTAINER_SET_SLOT.getName()), connection);
            if (sourceVersion.newerThanOrEqualTo(ProtocolVersion.v1_8)) {
                containerSetSlot.write(Types.UNSIGNED_BYTE, (short) 0); // window id
            } else {
                containerSetSlot.write(Types.BYTE, (byte) 0); // window id
            }
            containerSetSlot.write(Types.SHORT, (short) 0); // slot
            containerSetSlot.write(getClientboundItemType(sourceVersion), item != null ? item.copy() : null); // item

            containerSetSlot.resetReader();
            containerSetSlot.user().getProtocolInfo().getPipeline().transform(Direction.CLIENTBOUND, State.PLAY, containerSetSlot);
            final RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), MinecraftClient.getInstance().getNetworkHandler().getRegistryManager());
            containerSetSlot.setPacketType(null);
            containerSetSlot.writeToBuffer(buf);

            buf.readUnsignedByte(); // sync id
            buf.readVarInt(); // revision
            buf.readShort(); // slot
            return ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
        } catch (Throwable t) {
            ViaFabricPlusImpl.INSTANCE.getLogger().error("Error converting ViaVersion {} item to native item stack", sourceVersion, t);
            return ItemStack.EMPTY;
        }
    }

    /**
     * Gets the ViaVersion item type for the target version in the serverbound direction (creative inventory action packet)
     *
     * @param targetVersion The target version
     * @return The ViaVersion item type
     */
    public static Type<Item> getServerboundItemType(final ProtocolVersion targetVersion) {
        if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Typesb1_8_0_1.CREATIVE_ITEM;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return getClientboundItemType(targetVersion);
        } else {
            return VersionedTypes.V1_21_5.lengthPrefixedItem;
        }
    }

    /**
     * Gets the ViaVersion item type for the target version in the clientbound direction
     *
     * @param targetVersion The target version
     * @return The ViaVersion item type
     */
    public static Type<Item> getClientboundItemType(final ProtocolVersion targetVersion) {
        if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Types1_4_2.NBTLESS_ITEM;
        } else if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
            return Types1_2_4.NBT_ITEM;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_8)) {
            return Types1_7_6.ITEM;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_13)) {
            return Types.ITEM1_8;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_13_2)) {
            return Types.ITEM1_13;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_20_2)) {
            return Types.ITEM1_13_2;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_20_5)) {
            return Types.ITEM1_20_2;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_21)) {
            return VersionedTypes.V1_20_5.item;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_21_2)) {
            return VersionedTypes.V1_21.item;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_21_4)) {
            return VersionedTypes.V1_21_2.item;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_21_5)) {
            return VersionedTypes.V1_21_4.item;
        } else {
            return VersionedTypes.V1_21_5.item;
        }
    }

}
