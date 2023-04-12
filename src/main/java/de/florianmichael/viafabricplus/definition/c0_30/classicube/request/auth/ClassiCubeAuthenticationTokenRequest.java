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
package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.auth;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.auth.ClassiCubeAuthenticationResponse;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeAuthenticationTokenRequest extends ClassiCubeAuthenticationRequest {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public ClassiCubeAuthenticationTokenRequest(ClassiCubeAccount account) {
        super(account);
    }

    @Override
    public CompletableFuture<ClassiCubeAuthenticationResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(ClassiCubeRequest.AUTHENTICATION_URI)
                    .build();

            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            this.updateCookies(response);
            final String responseBody = response.body();
            return ClassiCubeAuthenticationResponse.fromJson(responseBody);
        });
    }
}
