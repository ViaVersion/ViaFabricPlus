/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.vialoadingbase.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.groups.MPPassSettings;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.viafabricplus.vialoadingbase.ViaLoadingBaseStartup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.protocols.release.protocol1_3_1_2to1_2_4_5.providers.OldAuthProvider;

public class ViaFabricPlusOldAuthProvider extends OldAuthProvider {

    @Override
    public void sendAuthRequest(UserConnection user, String serverId) throws Throwable {
        if (!MPPassSettings.getClassWrapper().allowViaLegacyToCallJoinServerToVerifySession.getValue()) return;

        final MinecraftClient mc = MinecraftClient.getInstance();

        try {
            mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getAccessToken(), serverId);
        } catch (Exception e) {
            if (MPPassSettings.getClassWrapper().disconnectIfJoinServerCallFails.getValue()) {
                user.getChannel().attr(ViaLoadingBaseStartup.LOCAL_MINECRAFT_CONNECTION).get().disconnect(Text.literal(ScreenUtil.prefixedMessage("ViaLegacy fails to verify your session! Please log in into an Account or disable the BetaCraft authentication in the ViaFabricPlus Settings")));
            } else {
                e.printStackTrace();
            }
        }
    }
}
