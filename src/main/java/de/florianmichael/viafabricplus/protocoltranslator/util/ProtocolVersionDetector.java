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
package de.florianmichael.viafabricplus.protocoltranslator.util;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.mcping.MCPing;
import net.lenni0451.mcping.responses.MCPingResponse;

import java.net.InetSocketAddress;

/**
 * This class can be used to detect the protocol version of a server without connecting to it.
 */
public class ProtocolVersionDetector {

    private static final int TIMEOUT = 3000;

    /**
     * Detects the protocol version of a server
     *
     * @param serverAddress The address of the server
     * @param clientVersion The version of the client
     * @return The protocol version of the server
     */
    public static ProtocolVersion get(final InetSocketAddress serverAddress, final ProtocolVersion clientVersion) {
        MCPingResponse response = MCPing
                .pingModern(clientVersion.getOriginalVersion())
                .address(serverAddress)
                .noResolve()
                .timeout(TIMEOUT, TIMEOUT)
                .getSync();

        if (response.version.protocol == clientVersion.getOriginalVersion()) { // If the server is on the same version as the client, we can just connect
            return clientVersion;
        } else { // Else ping again with protocol id -1 to get the protocol id of the server
            response = MCPing
                    .pingModern(-1)
                    .address(serverAddress)
                    .noResolve()
                    .timeout(TIMEOUT, TIMEOUT)
                    .getSync();

            if (ProtocolVersion.isRegistered(response.version.protocol)) { // If the protocol is registered, we can use it
                return ProtocolVersion.getProtocol(response.version.protocol);
            } else {
                throw new RuntimeException("Unsupported protocol version: " + response.version.protocol);
            }
        }
    }

}
