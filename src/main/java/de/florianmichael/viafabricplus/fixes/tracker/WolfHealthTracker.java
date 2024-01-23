/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.fixes.tracker;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;

public class WolfHealthTracker extends StoredObject {

    private final Int2FloatMap healthDataMap = new Int2FloatOpenHashMap();

    public WolfHealthTracker(UserConnection user) {
        super(user);
    }

    public float getWolfHealth(final int entityId, final float fallback) {
        return this.healthDataMap.getOrDefault(entityId, fallback);
    }

    public void setWolfHealth(final int entityId, final float wolfHealth) {
        this.healthDataMap.put(entityId, wolfHealth);
    }

    public static WolfHealthTracker get(final UserConnection userConnection) {
        var tracker = userConnection.get(WolfHealthTracker.class);
        if (tracker == null) {
            userConnection.put(tracker = new WolfHealthTracker(userConnection));
        }
        return tracker;
    }

}
