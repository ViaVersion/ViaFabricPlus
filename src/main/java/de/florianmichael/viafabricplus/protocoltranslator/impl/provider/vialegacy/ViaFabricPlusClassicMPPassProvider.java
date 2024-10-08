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

package de.florianmichael.viafabricplus.protocoltranslator.impl.provider.vialegacy;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.classic4j.BetaCraftHandler;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.settings.impl.AuthenticationSettings;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider;

public class ViaFabricPlusClassicMPPassProvider extends ClassicMPPassProvider {

    public static String classicubeMPPass;

    @Override
    public String getMpPass(UserConnection user) {
        if (classicubeMPPass != null) {
            final String mpPass = classicubeMPPass;
            classicubeMPPass = null;
            return mpPass;
        }

        if (AuthenticationSettings.global().useBetaCraftAuthentication.getValue()) {
            // Doesn't use the MPPass system anymore, but still kept here for simplicity
            BetaCraftHandler.authenticate(serverId -> {
                try {
                    Via.getManager().getProviders().get(OldAuthProvider.class).sendAuthRequest(user, serverId);
                } catch (Throwable e) {
                    ViaFabricPlus.global().getLogger().error("Error occurred while verifying session", e);
                }
            }, throwable -> ViaFabricPlus.global().getLogger().error("Error occurred while requesting the MP-Pass to verify session", throwable));
        }

        return super.getMpPass(user);
    }

}
