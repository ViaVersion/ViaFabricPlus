/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.features.global;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.util.JsonSave;
import com.viaversion.viafabricplus.util.LegacySaveMigrator;
import de.florianreuth.classic4j.ClassiCubeHandler;
import de.florianreuth.classic4j.api.LoginProcessHandler;
import de.florianreuth.classic4j.model.classicube.account.CCAccount;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

public final class ClassiCubeAccount {

    private static CCAccount account;
    private static boolean authenticated;

    public static void init() {
        final Path directory = ViaFabricPlusImpl.impl().path();
        LegacySaveMigrator.migrateClassiCubeAccount(directory);

        final Path path = directory.resolve("classicube.json");
        JsonSave.load(path, jsonObject -> account = CCAccount.fromJson(jsonObject), () -> {
            if (account != null) {
                return account.asJson();
            } else {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    ViaFabricPlusImpl.impl().logger().error("Failed to delete classicube.json!", e);
                }
                return null;
            }
        });
    }

    public static void authenticate(final @Nullable String loginCode, final LoginProcessHandler handler) {
        ClassiCubeHandler.requestAuthentication(account, loginCode, new LoginProcessHandler() {

            @Override
            public void handleSuccessfulLogin(final CCAccount account) {
                authenticated = true;
                handler.handleSuccessfulLogin(account);
            }

            @Override
            public void handleMfa(final CCAccount account) {
                handler.handleMfa(account);
            }

            @Override
            public void handleException(final Throwable throwable) {
                handler.handleException(throwable);
            }
        });
    }

    public static boolean authenticated() {
        return authenticated;
    }

    public static void set(final CCAccount account) {
        ClassiCubeAccount.account = account;
        authenticated = false;
    }

    public static CCAccount get() {
        return account;
    }

}
