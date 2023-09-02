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
package de.florianmichael.viafabricplus.definition.tracker;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;

import java.util.HashMap;
import java.util.Map;

public class WolfHealthTracker extends StoredObject {

    private final Map<Integer, Float> healthDataMap = new HashMap<>();

    public WolfHealthTracker(UserConnection user) {
        super(user);
    }

    public Map<Integer, Float> getHealthDataMap() {
        return healthDataMap;
    }

    public static WolfHealthTracker get() {
        final var connection = ProtocolHack.getMainUserConnection();
        if (connection == null) return null;

        if (!connection.has(WolfHealthTracker.class)) {
            connection.put(new WolfHealthTracker(connection));
        }
        return connection.get(WolfHealthTracker.class);
    }
}
