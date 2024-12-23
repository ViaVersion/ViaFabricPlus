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

package com.viaversion.viafabricplus.protocoltranslator.translator;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_17_1to1_18.packet.ClientboundPackets1_18;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockStateTranslator {

    private static final UserConnection DUMMY_USER_CONNECTION = ProtocolTranslator.createDummyUserConnection(ProtocolTranslator.NATIVE_VERSION, ProtocolVersion.v1_18_2);

    /**
     * Converts a 1.18.2-block state to a native block state (the current version)
     *
     * @param blockStateId The block state id
     * @return The native block state
     */
    public static BlockState via1_18_2toMc(final int blockStateId) {
        try {
            final PacketWrapper levelEvent = PacketWrapper.create(ClientboundPackets1_18.LEVEL_EVENT, DUMMY_USER_CONNECTION);
            levelEvent.write(Types.INT, 2001); // eventId
            levelEvent.write(Types.BLOCK_POSITION1_14, new BlockPosition(0, 0, 0)); // position
            levelEvent.write(Types.INT, blockStateId); // data
            levelEvent.write(Types.BOOLEAN, false); // global

            levelEvent.resetReader();
            levelEvent.user().getProtocolInfo().getPipeline().transform(Direction.CLIENTBOUND, State.PLAY, levelEvent);

            levelEvent.read(Types.INT); // eventId
            levelEvent.read(Types.BLOCK_POSITION1_14); // position
            return Block.getStateFromRawId(levelEvent.read(Types.INT)); // data
        } catch (Throwable t) {
            ViaFabricPlus.global().getLogger().error("Error converting ViaVersion 1.18.2 block state to native block state", t);
            return Blocks.AIR.getDefaultState();
        }
    }

}
