/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;

public final class ConnectionUtil {

    public static void connect(final String address, final ProtocolVersion version) {
        connect(address, address, version);
    }

    public static void connect(final String name, final String address) {
        connect(name, address, null);
    }

    public static void connect(final String name, final String address, final ProtocolVersion version) {
        final ServerAddress serverAddress = ServerAddress.parseString(address);
        final ServerData entry = new ServerData(name, serverAddress.getHost(), ServerData.Type.OTHER);

        if (version != null) {
            ((IServerInfo) entry).viaFabricPlus$forceVersion(version);
        }
        ConnectScreen.startConnecting(Minecraft.getInstance().screen, Minecraft.getInstance(), serverAddress, entry, false, null);
    }

}
