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
package de.florianmichael.viafabricplus.definition.c0_30.classicube.request.server;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.ClassiCubeRequest;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.server.ClassiCubeServerInfoResponse;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClassiCubeServerListRequest extends ClassiCubeRequest {
    public ClassiCubeServerListRequest(ClassiCubeAccount account) {
        super(account);
    }

    public CompletableFuture<ClassiCubeServerInfoResponse> send() {
        return CompletableFuture.supplyAsync(() -> {
            final HttpRequest request = this.buildWithCookies(HttpRequest.newBuilder()
                    .GET()
                    .uri(SERVER_LIST_INFO_URI));
            final HttpResponse<String> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .join();

            updateCookies(response);

            final String body = response.body();

            return ClassiCubeServerInfoResponse.fromJson(body);
        });
    }
}
