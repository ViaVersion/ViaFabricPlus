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

package com.viaversion.viafabricplus.api;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * General API point for mods. Get instance via {@link ViaFabricPlus#getImpl()}.
 */
public interface ViaFabricPlusBase {

    /**
     * @return an <b>internally based API version</b> incremented with meaningful or breaking changes.
     */
    default int apiVersion() {
        return 1;
    }

    /**
     * Get the logger for this mod.
     *
     * @return The logger
     */
    Logger logger();

    /**
     * Get the root path of the mod.
     *
     * @return The root path
     */
    Path rootPath();

    /**
     * This method is used when you need the target version after connecting to the server.
     *
     * @return the target version
     */
    ProtocolVersion getTargetVersion();

    /**
     * Gets the target version from the channel attribute, can be used in early stages of the connection
     *
     * @param channel the channel
     * @return the target version
     */
    ProtocolVersion getTargetVersion(final Channel channel);

    /**
     * Sets the target version
     *
     * @param newVersion the target version
     */
    void setTargetVersion(final ProtocolVersion newVersion);

    /**
     * Sets the target version
     *
     * @param newVersion         the target version
     * @param revertOnDisconnect if true, the previous version will be set when the player disconnects from the server
     */
    void setTargetVersion(final ProtocolVersion newVersion, final boolean revertOnDisconnect);

    /**
     * @param clientVersion The client version
     * @param serverVersion The server version
     * @return Creates a dummy UserConnection class with a valid protocol pipeline to emulate packets
     */
    UserConnection createDummyUserConnection(final ProtocolVersion clientVersion, final ProtocolVersion serverVersion);

    /**
     * @return the current UserConnection of the connection to the server, if the player isn't connected to a server it will return null
     */
    UserConnection getPlayNetworkUserConnection();

    /**
     * Register a callback for when the user changes the target version in the screen, or if the user joins a server with a different version.
     *
     * @param callback the callback
     */
    void registerOnChangeProtocolVersionCallback(final ChangeProtocolVersionCallback callback);

    /**
     * Register a callback for the loading cycle which covers most of the loading process of the mod.
     *
     * @param callback the callback
     */
    void registerLoadingCycleCallback(final LoadingCycleCallback callback);

    /**
     * Calculates the maximum chat length for given {@link ProtocolVersion} instance.
     *
     * @return The maximum chat length
     */
    int getMaxChatLength(final ProtocolVersion version);

}
