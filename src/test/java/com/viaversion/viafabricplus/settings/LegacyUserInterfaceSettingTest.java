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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.settings;

import com.google.gson.JsonObject;
import com.viaversion.viafabricplus.settings.impl.GeneralSettingsImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LegacyUserInterfaceSettingTest {

    @Test
    void defaultsToCurrentStyleAndRestoresSavedChoice() {
        final GeneralSettingsImpl settings = new GeneralSettingsImpl();
        assertFalse(settings.legacyUserInterface().isActive());

        settings.legacyUserInterface().setActive(true);
        final JsonObject saved = new JsonObject();
        settings.write(saved);
        assertTrue(saved.getAsJsonObject("general").get("legacy_user_interface").getAsBoolean());

        final GeneralSettingsImpl reloaded = new GeneralSettingsImpl();
        reloaded.read(saved);
        assertTrue(reloaded.legacyUserInterface().isActive());
    }

}
