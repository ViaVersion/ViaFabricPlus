/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.translator;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.raphimc.vialoader.util.VersionEnum;

public class BlockStateTranslator {

    private static final UserConnection DUMMY_USER_CONNECTION = ProtocolHack.createDummyUserConnection(ProtocolHack.NATIVE_VERSION, VersionEnum.r1_18_2);

    /**
     * Converts a 1.18.2 block state to a native block state (the current version)
     *
     * @param blockStateId The block state id
     * @return The native block state
     */
    public static BlockState via1_18_2toMc(final int blockStateId) {
        try {
            final PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_18.EFFECT, DUMMY_USER_CONNECTION);
            wrapper.write(Type.INT, 2001); // eventId
            wrapper.write(Type.POSITION1_14, new Position(0, 0, 0)); // position
            wrapper.write(Type.INT, blockStateId); // data
            wrapper.write(Type.BOOLEAN, false); // global

            wrapper.resetReader();
            wrapper.user().getProtocolInfo().getPipeline().transform(Direction.CLIENTBOUND, State.PLAY, wrapper);

            wrapper.read(Type.INT); // eventId
            wrapper.read(Type.POSITION1_14); // position
            return Block.getStateFromRawId(wrapper.read(Type.INT)); // data
        } catch (Throwable t) {
            ViaFabricPlus.global().getLogger().error("Error converting ViaVersion 1.18.2 block state to native block state", t);
            return Blocks.AIR.getDefaultState();
        }
    }

}
