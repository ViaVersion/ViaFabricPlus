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

package com.viaversion.viafabricplus.api;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.entrypoint.BootstrapEntrypoint;
import com.viaversion.viafabricplus.api.events.ChangeProtocolVersionEvent;
import com.viaversion.viafabricplus.api.events.LoadingCycleEvent;
import com.viaversion.viafabricplus.api.protocoltranslator.Conversions;
import com.viaversion.viafabricplus.api.protocoltranslator.Limitations;
import com.viaversion.viafabricplus.api.screen.Screens;
import com.viaversion.viafabricplus.api.settings.Settings;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import java.nio.file.Path;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.Nullable;

/**
 * General API point for mods. Get an instance via {@link ViaFabricPlus#api()}.
 */
public interface ViaFabricPlusAPI {

    /**
     * Returns an <b>internally based API version</b> incremented with meaningful API changes.
     * This includes breaking changes to the existing API and larger additions.
     *
     * @return API version incremented with meaningful API changes
     */
    default int apiVersion() {
        return 7;
    }

    /**
     * The version of the mod displayed in ModMenu and other places (e.g., 5.0.0)
     *
     * @return the version of the mod
     */
    String version();

    @Deprecated(forRemoval = true)
    default String getVersion() {
        return this.version();
    }

    /**
     * The implementation version of the mod formatted as "git-ViaFabricPlus-{@link #version()}:{commit hash}".
     *
     * @return the implementation version of the mod
     */
    String implVersion();

    @Deprecated(forRemoval = true)
    default String getImplVersion() {
        return this.implVersion();
    }

    /**
     * The root path inside the config folder where configuration files are stored.
     *
     * @return The root path of the mod
     */
    Path path();

    @Deprecated(forRemoval = true)
    default Path getPath() {
        return this.path();
    }

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

    @Deprecated
    default ProtocolVersion getTargetVersion() {
        return this.targetVersion();
    }

    @Deprecated(forRemoval = true)
    default ProtocolVersion getTargetVersion(final Connection connection) {
        return this.targetVersion(connection);
    }

    @Deprecated(forRemoval = true)
    default ProtocolVersion getTargetVersion(final Channel channel) {
        return this.targetVersion(channel);
    }

    /**
     * Sets the global target version permanently.
     *
     * @param targetVersion the target version
     */
    void setTargetVersion(final ProtocolVersion targetVersion);

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

    @Deprecated(forRemoval = true)
    default UserConnection getPlayNetworkUserConnection() {
        return this.userConnection();
    }

    @Deprecated(forRemoval = true)
    default UserConnection getUserConnection(final Connection connection) {
        return this.userConnection(connection);
    }

    /**
     * The pre-server version of the server. This is only valid if the user has set a specific version for the given server.
     *
     * @param serverData the server data
     * @return the pre-server version of the server
     */
    @Nullable ProtocolVersion serverVersion(final ServerData serverData);

    @Deprecated(forRemoval = true)
    default ProtocolVersion getServerVersion(final ServerData serverData) {
        return this.serverVersion(serverData);
    }

    /**
     * Register an event for when the user changes the target version in the screen, or if the user joins a server with a different version.
     *
     * @param event the event
     */
    void addChangeProtocolVersionEvent(final ChangeProtocolVersionEvent event);

    /**
     * Register an event for the loading cycle which covers most of the loading process of the mod. Intended to be used
     * inside {@link BootstrapEntrypoint} implementations.
     *
     * @param event the event
     */
    void addLoadingCycleEvent(final LoadingCycleEvent event);

    /**
     * Retrieves the settings associated with the mod.
     *
     * @return the {@link Settings} instance providing access to configuration options.
     */
    Settings settings();

    /**
     * Accesses various conversion methods between Minecraft and ViaVersion types.
     *
     * @return the {@link Conversions} instance providing conversion methods.
     */
    Conversions conversions();

    /**
     * Versioned limitations as the max chat message length for a specific version.
     *
     * @return the {@link Limitations} instance providing versioned limitations.
     */
    Limitations limitations();

    /**
     * ViaFabricPlus screens.
     *
     * @return the {@link Screens} instance providing access to ViaFabricPlus screens.
     */
    Screens screens();

}
