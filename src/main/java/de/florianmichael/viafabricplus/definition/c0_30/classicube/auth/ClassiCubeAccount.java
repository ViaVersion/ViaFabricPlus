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

import com.google.gson.JsonObject;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.process.ILoginProcessHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth.ClassiCubeAuthenticationLoginRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth.ClassiCubeAuthenticationTokenRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.store.CookieStore;

import javax.security.auth.login.LoginException;

public class ClassiCubeAccount {
    public final CookieStore cookieStore = new CookieStore();

    public String token;
    public String username;
    public String password;

    public ClassiCubeAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ClassiCubeAccount(String token, String username, String password) {
        this(username, password);
        this.token = token;
    }

    public JsonObject toJson() {
        final JsonObject object = new JsonObject();

        object.addProperty("token", this.token);
        object.addProperty("username", this.username);
        object.addProperty("password", this.password);

        return object;
    }

    public static ClassiCubeAccount fromJson(JsonObject jsonObject) {
        String token = null;
        if (jsonObject.has("token")) token = jsonObject.get("token").getAsString();

        return new ClassiCubeAccount(token, jsonObject.get("username").getAsString(), jsonObject.get("password").getAsString());
    }

    public void login(ILoginProcessHandler processHandler, String loginCode) {
        final ClassiCubeAuthenticationTokenRequest initialTokenRequest = new ClassiCubeAuthenticationTokenRequest(this);
        initialTokenRequest.send().whenComplete((initialTokenResponse, throwable) -> {
            if (throwable != null) {
                processHandler.handleException(throwable);
                return;
            }
            // There should NEVER be any errors on the initial token response!
            if (initialTokenResponse.shouldError()) {
                final String errorDisplay = initialTokenResponse.getErrorDisplay();
                processHandler.handleException(new LoginException(errorDisplay));
                return;
            }
            this.token = initialTokenResponse.token;

            final ClassiCubeAuthenticationLoginRequest loginRequest = new ClassiCubeAuthenticationLoginRequest(initialTokenResponse, this, loginCode);
            loginRequest.send().whenComplete((loginResponse, throwable1) -> {
                if (throwable1 != null) {
                    processHandler.handleException(throwable1);
                    return;
                }
                if (loginResponse.mfaRequired()) {
                    processHandler.handleMfa(this);
                    return;
                }
                if (loginResponse.shouldError()) {
                    final String errorDisplay = loginResponse.getErrorDisplay();
                    processHandler.handleException(new LoginException(errorDisplay));
                    return;
                }
                processHandler.handleSuccessfulLogin(this);
            });
        });
    }
}
