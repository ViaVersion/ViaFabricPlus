/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_13to1_12_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.providers.BlockConnectionProvider;
import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ConnectionData.class, remap = false)
public class MixinConnectionData {

    @Shadow private static Int2ObjectMap<ConnectionHandler> connectionHandlerMap;

    @Shadow public static BlockConnectionProvider blockConnectionProvider;

    /**
     * @author FlorianMichael
     * @reason prevent block spam
     */
    @Overwrite
    public static void update(UserConnection user, Position position) {
        for (BlockFace face : BlockFace.values()) {
            Position pos = position.getRelative(face);
            int blockState = blockConnectionProvider.getBlockData(user, pos.x(), pos.y(), pos.z());
            ConnectionHandler handler = connectionHandlerMap.get(blockState);
            if (handler == null) continue;

            final int newBlockState = handler.connect(user, pos, blockState);
            if (DebugSettings.getClassWrapper().cancelEqualBlockChangeUpdates.getValue()) {
                if (blockState == newBlockState) continue;
            }
            PacketWrapper blockUpdatePacket = PacketWrapper.create(ClientboundPackets1_13.BLOCK_CHANGE, null, user);
            blockUpdatePacket.write(Type.POSITION, pos);
            blockUpdatePacket.write(Type.VAR_INT, newBlockState);
            try {
                blockUpdatePacket.send(Protocol1_13To1_12_2.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
