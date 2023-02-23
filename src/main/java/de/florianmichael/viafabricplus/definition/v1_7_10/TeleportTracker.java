package de.florianmichael.viafabricplus.definition.v1_7_10;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public class TeleportTracker extends StoredObject {

    private Boolean onGround = null;

    public TeleportTracker(UserConnection user) {
        super(user);
    }

    public Boolean getPending() {
        return onGround;
    }

    public void setPending(Boolean onGround) {
        this.onGround = onGround;
    }
}
