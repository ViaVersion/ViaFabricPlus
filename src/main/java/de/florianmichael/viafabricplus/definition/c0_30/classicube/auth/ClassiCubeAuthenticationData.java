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
package de.florianmichael.viafabricplus.definition.c0_30.classicube.auth;

import javax.annotation.Nullable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Contains the authentication data that will be used in the URL parameters of the /api/login request.
 * Taken from <a href="https://www.classicube.net/api/">the official ClassiCube API documentation</a>
 */
public class ClassiCubeAuthenticationData {
    private final String username;
    private final String password;
    private final String previousToken;
    @Nullable private final String loginCode;

    public ClassiCubeAuthenticationData(String username, String password, String previousToken) {
        this(username, password, previousToken, null);
    }

    public ClassiCubeAuthenticationData(String username, String password, String previousToken, @Nullable String loginCode) {
        this.username = username;
        this.password = password;
        this.previousToken = previousToken;
        this.loginCode = loginCode;
    }

    public ClassiCubeAuthenticationData getWithLoginToken(final String loginCode) {
        return new ClassiCubeAuthenticationData(this.username, this.password, this.previousToken, loginCode);
    }

    public String getRequestBody() {
        final StringBuilder builder = new StringBuilder("username=").append(URLEncoder.encode(username, StandardCharsets.UTF_8));

        builder.append("&password=");
        builder.append(URLEncoder.encode(password, StandardCharsets.UTF_8));
        builder.append("&token=");
        builder.append(URLEncoder.encode(previousToken, StandardCharsets.UTF_8));

        if (loginCode != null) {
            builder.append("&login_code=");
            builder.append(URLEncoder.encode(loginCode, StandardCharsets.UTF_8));
        }

        return builder.toString();
    }
}
