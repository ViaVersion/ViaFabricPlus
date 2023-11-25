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

package de.florianmichael.viafabricplus.protocolhack.provider.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import net.minecraft.client.MinecraftClient;

public class ViaFabricPlusPlayerAbilitiesProvider extends PlayerAbilitiesProvider {

    @Override
    public float getFlyingSpeed(UserConnection connection) {
        return MinecraftClient.getInstance().player.getAbilities().getFlySpeed();
    }

    @Override
    public float getWalkingSpeed(UserConnection connection) {
        return MinecraftClient.getInstance().player.getAbilities().getWalkSpeed();
    }
}
