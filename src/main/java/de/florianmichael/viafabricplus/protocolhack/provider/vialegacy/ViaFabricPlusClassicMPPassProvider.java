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

package de.florianmichael.viafabricplus.protocolhack.provider.vialegacy;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.classic4j.BetaCraftHandler;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.impl.AuthenticationSettings;
import net.minecraft.client.MinecraftClient;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.storage.HandshakeStorage;

public class ViaFabricPlusClassicMPPassProvider extends ClassicMPPassProvider {
    public static String classiCubeMPPass;

    @Override
    public String getMpPass(UserConnection user) {
        if (classiCubeMPPass != null) {
            return classiCubeMPPass;
        }

        if (AuthenticationSettings.INSTANCE.useBetaCraftAuthentication.getValue()) {
            final var handshakeStorage = user.get(HandshakeStorage.class);
            if (handshakeStorage == null) {
                return super.getMpPass(user);
            }

            return BetaCraftHandler.requestMPPass(user.getProtocolInfo().getUsername(), handshakeStorage.getHostname(), handshakeStorage.getPort(), serverId -> {
                final var mc = MinecraftClient.getInstance();

                try {
                    mc.getSessionService().joinServer(mc.getSession().getUuidOrNull(), mc.getSession().getAccessToken(), serverId);
                } catch (AuthenticationException e) {
                    ViaFabricPlus.global().getLogger().error("Error occurred while verifying session", e);
                }
            }, throwable -> ViaFabricPlus.global().getLogger().error("Error occurred while requesting the MP-Pass to verify session", throwable));
        } else {
            return super.getMpPass(user);
        }
    }
}
