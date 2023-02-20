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
