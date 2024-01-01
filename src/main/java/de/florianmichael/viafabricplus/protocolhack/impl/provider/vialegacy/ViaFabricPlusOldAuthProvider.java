/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.impl.provider.vialegacy;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.AuthenticationSettings;
import de.florianmichael.viafabricplus.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;

public class ViaFabricPlusOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        if (!AuthenticationSettings.global().verifySessionForOnlineModeServers.getValue()) return;

        try {
            final var mc = MinecraftClient.getInstance();

            mc.getSessionService().joinServer(mc.getSession().getUuidOrNull(), mc.getSession().getAccessToken(), serverId);
        } catch (Exception e) {
            user.getChannel().attr(ProtocolHack.CLIENT_CONNECTION_ATTRIBUTE_KEY).get().disconnect(ChatUtil.prefixText(Text.translatable("authentication.viafabricplus.failed_to_verify_session")));
            ViaFabricPlus.global().getLogger().error("Error occurred while calling join server to verify session", e);
        }
    }

}
