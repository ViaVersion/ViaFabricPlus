/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.api.protocoltranslator;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import java.util.function.BiConsumer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.Nullable;

/**
 * Protocol translator.
 */
public interface ProtocolTranslation {

    /**
     * The target version either selected by the user in the global menu or set per server. Note that this method
     * only works after the initial connection has been established.
     *
     * @return the target version
     */
    ProtocolVersion targetVersion();

    /**
     * The target version for the given connection. This method is useful for retrieving the version before the global
     * version has been set ({@link #targetVersion()}) and might work in earlier stages of the connection. Note that
     * there is no guarantee for the value returned here.
     *
     * @param connection the connection
     * @return the target version
     */
    ProtocolVersion targetVersion(final Connection connection);

    /**
     * Same as {@link #targetVersion(Connection)} but for the given channel.
     *
     * @param channel the channel
     * @return the target version
     */
    ProtocolVersion targetVersion(final Channel channel);

    /**
     * Sets the global target version permanently.
     *
     * @param targetVersion the target version
     */
    default void setTargetVersion(final ProtocolVersion targetVersion) {
        this.setTargetVersion(targetVersion, false);
    }

    /**
     * Sets the global target version. If the client disconnects, the version will be reverted to the previous version.
     *
     * @param targetVersion      the target version
     * @param revertOnDisconnect whether to revert the version on disconnect
     */
    void setTargetVersion(final ProtocolVersion targetVersion, final boolean revertOnDisconnect);

    /**
     * Adds a listener for when the protocol version changes.
     *
     * @param listener The listener to add, which will be called when the protocol version changes.
     */
    void addChangeProtocolVersionListener(final BiConsumer<ProtocolVersion, ProtocolVersion> listener);

    /**
     * ViaVersion's user connection object. This is set after the initial connection has been established. Useful
     * for when sending raw packets or modifying the client's state. Only valid as soon as the client switched its
     * network state to PLAY (!)
     *
     * @return the user connection
     */
    @Nullable UserConnection userConnection();

    /**
     * Similar to {@link #userConnection()}, but for the given connection. This method also works in earlier stages
     * than PLAY (e.g., LOGIN; CONFIGURATION).
     *
     * @param connection the connection
     * @return the user connection
     */
    @Nullable UserConnection userConnection(final Connection connection);

    /**
     * The pre-server version of the server. This is only valid if the user has set a specific version for the given server.
     *
     * @param serverData the server data
     * @return the pre-server version of the server
     */
    @Nullable ProtocolVersion serverVersion(final ServerData serverData);

}
