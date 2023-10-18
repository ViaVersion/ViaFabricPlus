/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.integration;

import com.mojang.authlib.exceptions.AuthenticationException;
import de.florianmichael.classic4j.api.JoinServerInterface;
import de.florianmichael.classic4j.model.classicube.CCError;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Classic4JImpl {
    public final static JoinServerInterface JOIN_SERVER_CALL = serverId -> {
        final MinecraftClient mc = MinecraftClient.getInstance();

        try {
            mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getAccessToken(), serverId);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    };

    public static Text fromError(final CCError error) {
        switch (error) {
            case TOKEN -> {
                return Text.translatable("classicube.viafabricplus.error.token");
            }
            case USERNAME -> {
                return Text.translatable("classicube.viafabricplus.error.username");
            }
            case PASSWORD -> {
                return Text.translatable("classicube.viafabricplus.error.password");
            }
            case VERIFICATION -> {
                return Text.translatable("classicube.viafabricplus.error.verification");
            }
            case LOGIN_CODE -> {
                return Text.translatable("classicube.viafabricplus.error.logincode");
            }
        }
        return Text.empty();
    }
}
