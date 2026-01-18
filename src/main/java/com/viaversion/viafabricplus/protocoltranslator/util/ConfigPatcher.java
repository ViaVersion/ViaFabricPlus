/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.protocoltranslator.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigPatcher {

    public static void patch(final Path path) throws IOException {
        final Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.notExists(path)) {
            Files.createFile(path);
        }

        String content = Files.readString(path, StandardCharsets.UTF_8);

        content = appendIfMissing(content, "fix-infested-block-breaking", "false");
        content = appendIfMissing(content, "shield-blocking", "false");
        content = appendIfMissing(content, "no-delay-shield-blocking", "true");
        content = appendIfMissing(content, "handle-invalid-item-count", "true");
        content = appendIfMissing(content, "chunk-border-fix", "true");
        content = appendIfMissing(content, "send-player-details", "false");

        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    private static String appendIfMissing(final String content, final String key, final String value) {
        for (final String line : content.split("\n", -1)) {
            final String trimmed = line.stripLeading();
            if (!trimmed.startsWith("#") && trimmed.startsWith(key + ":")) {
                return content; // key already present
            }
        }

        final StringBuilder replacement = new StringBuilder(content);
        if (!content.isEmpty() && !content.endsWith("\n")) {
            replacement.append('\n');
        }
        replacement.append(key).append(": ").append(value).append('\n');
        return replacement.toString();
    }

}
