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
package de.florianmichael.viafabricplus.definition.c0_30.classicube.request;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class ClassiCubeRequest {
    private final static URI CLASSICUBE_ROOT_URI = URI.create("https://www.classicube.net");

    protected final static URI AUTHENTICATION_URI = CLASSICUBE_ROOT_URI.resolve("/api/login/");
    protected final static URI SERVER_INFO_URI = CLASSICUBE_ROOT_URI.resolve("/api/server/");
    protected final static URI SERVER_LIST_INFO_URI = CLASSICUBE_ROOT_URI.resolve("/api/servers/");
    protected final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    protected final ClassiCubeAccount account;

    protected ClassiCubeRequest(ClassiCubeAccount account) {
        this.account = account;
    }

    protected HttpRequest buildWithCookies(final HttpRequest.Builder builder) {
        return this.account.cookieStore.appendCookies(builder)
                .build();
    }

    protected void updateCookies(final HttpResponse<?> response) {
        this.account.cookieStore.mergeFromResponse(response);
    }
}
