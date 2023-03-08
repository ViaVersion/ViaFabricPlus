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
package de.florianmichael.viafabricplus.definition.v1_18_0;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_18_2to1_18.Protocol1_18_2To1_18;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;

public class SpawnPositionTracker extends StoredObject {

    private Position spawnPosition = new Position(8, 64, 8);
    private float angle = 0F;

    public SpawnPositionTracker(final UserConnection user) {
        super(user);
    }

    public void setSpawnPosition(final Position spawnPosition, final float angle) {
        this.spawnPosition = spawnPosition;
        this.angle = angle;
    }

    public void sendSpawnPosition() throws Exception {
        final PacketWrapper spawnPosition = PacketWrapper.create(ClientboundPackets1_18.SPAWN_POSITION, this.getUser());
        spawnPosition.write(Type.POSITION1_14, this.spawnPosition); // position
        spawnPosition.write(Type.FLOAT, this.angle); // angle

        spawnPosition.send(Protocol1_18_2To1_18.class);
    }
}
