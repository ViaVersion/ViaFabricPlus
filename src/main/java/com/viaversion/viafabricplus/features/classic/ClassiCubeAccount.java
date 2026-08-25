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

package com.viaversion.viafabricplus.features.classic;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.util.JsonSave;
import de.florianreuth.classic4j.model.classicube.account.CCAccount;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClassiCubeAccount {

    private static CCAccount account;

    public static void init() {
        final Path path = ViaFabricPlusImpl.impl().path().resolve("classicube.json");
        JsonSave.read(path, jsonObject -> account = CCAccount.fromJson(jsonObject));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (account != null) {
                JsonSave.write(path, account::asJson);
            } else {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    ViaFabricPlusImpl.impl().logger().error("Failed to delete classicube.json!", e);
                }
            }
        }));
    }

    public static void set(final CCAccount account) {
        ClassiCubeAccount.account = account;
    }

    public static CCAccount get() {
        return account;
    }

}
