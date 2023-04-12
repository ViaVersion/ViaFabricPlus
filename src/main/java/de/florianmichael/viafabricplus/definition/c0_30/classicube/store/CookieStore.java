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
package de.florianmichael.viafabricplus.definition.c0_30.classicube.store;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CookieStore {
    private final Map<String, String> values;

    public CookieStore() {
        this.values = new HashMap<>();
    }

    public CookieStore(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(values);
    }

    public void merge(CookieStore cookieStore) {
        this.values.putAll(cookieStore.getMap());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        int i = 0;

        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (i != 0) {
                builder.append(";");
            }

            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            i++;
        }

        return builder.toString();
    }

    public void mergeFromResponse(HttpResponse<?> response) {
        final Optional<String> setCookieHeaderOptional = response.headers()
                .firstValue("set-cookie");

        if (setCookieHeaderOptional.isEmpty()) {
            return;
        }

        final String setCookieHeader = setCookieHeaderOptional.get();
        final CookieStore store = CookieStore.parse(setCookieHeader);

        this.merge(store);
    }


    public HttpRequest.Builder appendCookies(HttpRequest.Builder builder) {
        builder.header("Cookie", this.toString());

        return builder;
    }

    public static CookieStore parse(final String value) {
        final char[] characters = value.toCharArray();
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean key = true;
        final Map<String, String> values = new HashMap<>();

        for (final char character : characters) {
            if (character == '=' && key) {
                currentValue = new StringBuilder();
                key = false;
                continue;
            }

            if (character == ';' && !key) {
                key = true;
                values.put(currentKey.toString(), currentValue.toString());
                continue;
            }

            if (key) {
                currentKey.append(character);
            } else {
                currentValue.append(character);
            }
        }

        return new CookieStore(values);
    }
}
