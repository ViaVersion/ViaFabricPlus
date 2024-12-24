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

package com.viaversion.viafabricplus.util;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class ConnectionUtil {

    public static void connect(final String address, final ProtocolVersion version) {
        connect(address, address, version);
    }

    public static void connect(final String name, final String address) {
        connect(name, address, null);
    }

    public static void connect(final String name, final String address, final ProtocolVersion version) {
        final ServerAddress serverAddress = ServerAddress.parse(address);
        final ServerInfo entry = new ServerInfo(name, serverAddress.getAddress(), ServerInfo.ServerType.OTHER);

        if (version != null) {
            ((IServerInfo) entry).viaFabricPlus$forceVersion(version);
        }
        ConnectScreen.connect(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), serverAddress, entry, false, null);
    }

}
