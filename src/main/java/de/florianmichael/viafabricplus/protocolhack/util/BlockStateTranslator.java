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
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;

public class BlockStateTranslator {
    private final static UserConnection DUMMY_USER_CONNECTION = ProtocolHack.createFakerUserConnection(null);

    public static int translateBlockState1_18(int oldId) {
        final List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(ProtocolHack.NATIVE_VERSION.getVersion(), ProtocolVersion.v1_18_2.getVersion());
        if (protocolPath == null) return oldId;

        final PacketByteBuf inputData = new PacketByteBuf(Unpooled.buffer());
        inputData.writeBlockPos(new BlockPos(0, 0, 0));
        inputData.writeVarInt(oldId);

        try {
            var wrapper = PacketWrapper.create(ClientboundPackets1_18.BLOCK_CHANGE, inputData.asByteBuf(), DUMMY_USER_CONNECTION);
            wrapper.apply(Direction.CLIENTBOUND, State.PLAY, 0, protocolPath.stream().map(ProtocolPathEntry::protocol).collect(Collectors.toList()), true);

            wrapper.read(Type.POSITION1_14);
            return wrapper.read(Type.VAR_INT);
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to translate block state " + oldId + " to 1.18.2", e);
        }

        return oldId;
    }
}
