/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.protocolhack.util;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.InventoryTracker;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.raphimc.vialegacy.protocols.beta.protocol1_0_0_1tob1_8_0_1.ClientboundPacketsb1_8;
import net.raphimc.vialegacy.protocols.beta.protocol1_0_0_1tob1_8_0_1.types.Typesb1_8_0_1;
import net.raphimc.vialegacy.protocols.release.protocol1_4_4_5to1_4_2.types.Types1_4_2;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.storage.WindowTracker;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.List;
import java.util.stream.Collectors;

public class ItemTranslator {
    private final static UserConnection DUMMY_USER_CONNECTION = new UserConnectionImpl(null, false);

    public static Item MC_TO_VIA_LATEST_TO_TARGET(final ItemStack stack, final VersionEnum targetVersion) {
        final List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(SharedConstants.getProtocolVersion(), targetVersion.getVersion());
        if (protocolPath == null) return null;

        final var dummyPacket = new CreativeInventoryActionC2SPacket(36, stack);
        final var emptyBuf = new PacketByteBuf(Unpooled.buffer());
        dummyPacket.write(emptyBuf);

        final int id = NetworkState.PLAY.getPacketId(NetworkSide.SERVERBOUND, dummyPacket);

        try {
            final var wrapper = new PacketWrapperImpl(id, emptyBuf, DUMMY_USER_CONNECTION);
            wrapper.apply(Direction.SERVERBOUND, State.PLAY, 0, protocolPath.stream().map(ProtocolPathEntry::protocol).collect(Collectors.toList()));

            wrapper.read(Type.SHORT);
            if (targetVersion.isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
                return wrapper.read(Typesb1_8_0_1.CREATIVE_ITEM);
            } else {
                return wrapper.read(Type.ITEM);
            }
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to translate item", e);
        }
        return null;
    }

    public static ItemStack VIA_TO_MC_B1_8_TO_LATEST(final Item item) {
        final List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(SharedConstants.getProtocolVersion(), VersionEnum.b1_8tob1_8_1.getVersion());
        if (protocolPath == null) return null;

        DUMMY_USER_CONNECTION.put(new WindowTracker(DUMMY_USER_CONNECTION));
        DUMMY_USER_CONNECTION.put(new InventoryTracker());

        try {
            final var wrapper = new PacketWrapperImpl(ClientboundPacketsb1_8.SET_SLOT.getId(), null, DUMMY_USER_CONNECTION);
            wrapper.write(Type.BYTE, (byte) 0); // Window ID
            wrapper.write(Type.SHORT, (short) 0); // Slot
            wrapper.write(Types1_4_2.NBTLESS_ITEM, item); // Item

            wrapper.resetReader();

            wrapper.apply(Direction.CLIENTBOUND, State.PLAY, 0, protocolPath.stream().map(ProtocolPathEntry::protocol).collect(Collectors.toList()), true);

            wrapper.read(Type.UNSIGNED_BYTE);
            wrapper.read(Type.VAR_INT);
            wrapper.read(Type.SHORT);

            final var viaItem = wrapper.read(Type.FLAT_VAR_INT_ITEM);
            return new ItemStack(() -> Registries.ITEM.get(viaItem.identifier()), viaItem.amount());
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to translate item", e);
            return null;
        }
    }
}
