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

package com.viaversion.viafabricplus.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public final class LegacySaveMigrator {

    private static final String VERSION_KEY = "selected_protocol_version";
    private static final String LEGACY_VERSION_KEY = "selected-protocol-version";

    private static final Set<String> LEGACY_GROUPS = Set.of("bedrock", "authentication", "debug");
    private static final Map<String, String> RENAMED_SETTINGS = Map.of("beta_craft_authentication", "use_beta_craft_authentication");

    private static final String DISABLED = "disabled";

    private static final String LEGACY_ACCOUNTS_FILE = "accounts.json";
    private static final String CLASSICUBE_FILE = "classicube.json";
    private static final String CLASSICUBE_KEY = "classicube";

    public static boolean isLegacySettings(final JsonObject object) {
        return object.has(LEGACY_VERSION_KEY) || LEGACY_GROUPS.stream().anyMatch(object::has);
    }

    public static JsonObject migrateSettings(final JsonObject legacy, final JsonObject current) {
        for (final Map.Entry<String, JsonElement> group : current.entrySet()) {
            if (!group.getValue().isJsonObject()) { // The selected version is no group
                continue;
            }

            final JsonObject settings = group.getValue().getAsJsonObject();
            for (final String key : List.copyOf(settings.keySet())) {
                final JsonPrimitive value = legacyValue(legacy, RENAMED_SETTINGS.getOrDefault(key, key));
                if (value != null) {
                    settings.add(key, migrateValue(settings.get(key).getAsJsonPrimitive(), value));
                }
            }
        }

        if (legacy.has(LEGACY_VERSION_KEY)) {
            current.add(VERSION_KEY, legacy.get(LEGACY_VERSION_KEY));
        }
        return current;
    }

    public static void migrateClassiCubeAccount(final Path directory) {
        final Path legacyPath = directory.resolve(LEGACY_ACCOUNTS_FILE);
        final Path path = directory.resolve(CLASSICUBE_FILE);
        if (!Files.exists(legacyPath) || Files.exists(path)) {
            return;
        }

        JsonSave.read(legacyPath, legacy -> {
            if (legacy.has(CLASSICUBE_KEY) && legacy.get(CLASSICUBE_KEY).isJsonObject()) {
                JsonSave.write(path, () -> legacy.getAsJsonObject(CLASSICUBE_KEY));
            }

            // Dropping the old save keeps a later logout from being undone by migrating it a second time
            try {
                Files.delete(legacyPath);
            } catch (final IOException e) {
                ViaFabricPlusImpl.impl().logger().error("Failed to delete the migrated {}!", LEGACY_ACCOUNTS_FILE, e);
            }
        });
    }

    // The old groups don't line up with the current ones, so a setting is searched for in all of them
    private static @Nullable JsonPrimitive legacyValue(final JsonObject legacy, final String key) {
        for (final Map.Entry<String, JsonElement> group : legacy.entrySet()) {
            if (!group.getValue().isJsonObject()) {
                continue;
            }

            final JsonElement value = group.getValue().getAsJsonObject().get(key);
            if (value != null && value.isJsonPrimitive()) {
                return value.getAsJsonPrimitive();
            }
        }
        return null;
    }

    // The current value tells the type apart, enums are the only settings which aren't stored as a boolean
    private static JsonPrimitive migrateValue(final JsonPrimitive current, final JsonPrimitive legacy) {
        if (!current.isBoolean()) {
            return new JsonPrimitive(legacy.getAsString().toUpperCase(Locale.ROOT));
        }

        // Only the tri-state booleans were written as a string, "auto" and "enabled" both become on
        return legacy.isBoolean() ? legacy : new JsonPrimitive(!DISABLED.equals(legacy.getAsString()));
    }

}
