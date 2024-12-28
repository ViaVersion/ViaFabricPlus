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

package de.florianmichael.viafabricplus.protocoltranslator;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.LegacyCompatBridge;
import io.netty.channel.Channel;

/**
 * Please migrate to the general {@link com.viaversion.viafabricplus.ViaFabricPlus} API point.
 */
@Deprecated
public class ProtocolTranslator {

    @Deprecated
    public static ProtocolVersion getTargetVersion() {
        LegacyCompatBridge.warn();
        return ViaFabricPlus.getImpl().getTargetVersion();
    }

    @Deprecated
    public static ProtocolVersion getTargetVersion(final Channel channel) {
        LegacyCompatBridge.warn();
        return ViaFabricPlus.getImpl().getTargetVersion(channel);
    }

    @Deprecated
    public static void setTargetVersion(final ProtocolVersion newVersion) {
        LegacyCompatBridge.warn();
        ViaFabricPlus.getImpl().setTargetVersion(newVersion);
    }

    @Deprecated
    public static void setTargetVersion(final ProtocolVersion newVersion, final boolean revertOnDisconnect) {
        LegacyCompatBridge.warn();
        ViaFabricPlus.getImpl().setTargetVersion(newVersion, revertOnDisconnect);
    }

    @Deprecated
    public static UserConnection createDummyUserConnection(final ProtocolVersion clientVersion, final ProtocolVersion serverVersion) {
        LegacyCompatBridge.warn();
        return null;
    }

    @Deprecated
    public static UserConnection getPlayNetworkUserConnection() {
        LegacyCompatBridge.warn();
        return ViaFabricPlus.getImpl().getPlayNetworkUserConnection();
    }

}
