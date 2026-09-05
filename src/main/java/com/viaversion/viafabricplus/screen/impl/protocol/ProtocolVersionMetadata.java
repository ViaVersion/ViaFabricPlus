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

package com.viaversion.viafabricplus.screen.impl.protocol;

import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public record ProtocolVersionMetadata(@Nullable String title, LocalDate releaseDate, Identifier icon) {

    private static final Identifier UNKNOWN_ICON = Identifier.withDefaultNamespace("textures/item/barrier.png");

    private static final Map<String, ProtocolVersionMetadata> BY_VERSION_NAME = load();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.US);

    public static @Nullable ProtocolVersionMetadata of(final ProtocolVersion version) {
        return BY_VERSION_NAME.get(version.getName());
    }

    public static Identifier icon(final @Nullable ProtocolVersion version) {
        final ProtocolVersionMetadata metadata = version == null ? null : of(version);
        return metadata != null ? metadata.icon() : UNKNOWN_ICON;
    }

    public Component formattedReleaseDate() {
        return Component.nullToEmpty(this.releaseDate.format(DATE_FORMAT));
    }

    private static Map<String, ProtocolVersionMetadata> load() {
        final JsonObject data = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("version-metadata.json");
        final Map<String, ProtocolVersionMetadata> metadata = new HashMap<>(data.size());
        for (final String versionName : data.keySet()) {
            final JsonObject entry = data.getAsJsonObject(versionName);
            final String title = entry.has("title") ? entry.get("title").getAsString() : null;

            // Vanilla textures are blitted directly as item stacks can't be created before a server bound their components
            final Identifier icon = Identifier.withDefaultNamespace("textures/" + entry.get("icon").getAsString() + ".png");
            metadata.put(versionName, new ProtocolVersionMetadata(title, LocalDate.parse(entry.get("release_date").getAsString()), icon));
        }
        return metadata;
    }

}
