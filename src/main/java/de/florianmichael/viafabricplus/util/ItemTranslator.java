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
package de.florianmichael.viafabricplus.util;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.List;
import java.util.stream.Collectors;

public class ItemTranslator {

    public static Item minecraftToViaVersion(final UserConnection user, final ItemStack stack, final int targetVersion) {
        final List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(ViaLoadingBase.getClassWrapper().getNativeVersion(), targetVersion);
        if (protocolPath == null) return null;

        final CreativeInventoryActionC2SPacket dummyPacket = new CreativeInventoryActionC2SPacket(36, stack);
        final PacketByteBuf emptyBuf = new PacketByteBuf(Unpooled.buffer());
        dummyPacket.write(emptyBuf);

        final Integer id = NetworkState.PLAY.getPacketId(NetworkSide.SERVERBOUND, dummyPacket);
        if (id == null) return null;

        final PacketWrapper wrapper = new PacketWrapperImpl(id, emptyBuf, user);
        try {
            wrapper.apply(Direction.SERVERBOUND, State.PLAY, 0, protocolPath.stream().map(ProtocolPathEntry::protocol).collect(Collectors.toList()));

            wrapper.read(Type.SHORT);
            return wrapper.read(Type.ITEM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
