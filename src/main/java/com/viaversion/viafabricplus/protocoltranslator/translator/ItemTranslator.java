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
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_12to1_12_1.packet.ClientboundPackets1_12_1;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;

public final class ItemTranslator {

    public static Item mcToVia(final ItemStack stack, final ProtocolVersion targetVersion) {
        final UserConnection connection = ProtocolTranslator.createDummyUserConnection(ProtocolTranslator.NATIVE_VERSION, targetVersion);

        try {
            final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), Minecraft.getInstance().getConnection().registryAccess());
            buf.writeShort(0); // slot
            ItemStack.OPTIONAL_UNTRUSTED_STREAM_CODEC.encode(buf, stack); // item

            final PacketWrapper setCreativeModeSlot = PacketWrapper.create(ViaFabricPlusProtocol.INSTANCE.getSetCreativeModeSlot(), buf, connection);
            connection.getProtocolInfo().getPipeline().transform(Direction.SERVERBOUND, State.PLAY, setCreativeModeSlot);

            setCreativeModeSlot.read(Types.SHORT); // slot
            return setCreativeModeSlot.read(ViaFabricPlusProtocol.INSTANCE.getServerboundItemType(targetVersion)); // item
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
            containerSetSlot.write(ViaFabricPlusProtocol.INSTANCE.getClientboundItemType(sourceVersion), item != null ? item.copy() : null); // item

            containerSetSlot.resetReader();
            containerSetSlot.user().getProtocolInfo().getPipeline().transform(Direction.CLIENTBOUND, State.PLAY, containerSetSlot);
            final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), Minecraft.getInstance().getConnection().registryAccess());
            containerSetSlot.setPacketType(null);
            containerSetSlot.writeToBuffer(buf);

            buf.readUnsignedByte(); // sync id
            buf.readVarInt(); // revision
            buf.readShort(); // slot
            return ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        } catch (Throwable t) {
            ViaFabricPlusImpl.INSTANCE.getLogger().error("Error converting ViaVersion {} item to native item stack", sourceVersion, t);
            return ItemStack.EMPTY;
        }
    }

}
