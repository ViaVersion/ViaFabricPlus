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

package com.viaversion.viafabricplus.features.entity.metadata_handling;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.connection.StorableObject;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import net.minecraft.entity.LivingEntity;

public final class WolfHealthTracker1_14_4 implements StorableObject {

    private final Int2FloatMap healthDataMap = new Int2FloatOpenHashMap();

    public static float getWolfHealth(final LivingEntity entity) {
        ProtocolTranslator.getPlayNetworkUserConnection().get(WolfHealthTracker1_14_4.class).getWolfHealth(entity.getId(), entity.getHealth());
    }

    public float getWolfHealth(final int entityId, final float fallback) {
        return this.healthDataMap.getOrDefault(entityId, fallback);
    }

    public void setWolfHealth(final int entityId, final float wolfHealth) {
        this.healthDataMap.put(entityId, wolfHealth);
    }

}
