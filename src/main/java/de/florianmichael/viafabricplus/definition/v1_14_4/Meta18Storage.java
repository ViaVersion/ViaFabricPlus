package de.florianmichael.viafabricplus.definition.v1_14_4;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.HashMap;
import java.util.Map;

public class Meta18Storage extends StoredObject {

    private final Map<Integer, Float> healthDataMap = new HashMap<>();

    public Meta18Storage(UserConnection user) {
        super(user);
    }

    public Map<Integer, Float> getHealthDataMap() {
        return healthDataMap;
    }
}
