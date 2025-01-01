/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.visuals.features.filter_non_existing_characters;

import java.util.Map;

public final class LanguageUtil {

    private static final int NON_ASCII_THRESHOLD = 256;

    public static boolean isUnicodeFont1_12_2(final Map<String, String> translations) {
        int nonAsciiCharacters = 0;
        int totalCharacters = 0;

        for (String value : translations.values()) {
            totalCharacters += value.length();
            for (int i = 0; i < value.length(); ++i) {
                if (value.charAt(i) >= NON_ASCII_THRESHOLD) {
                    nonAsciiCharacters++;
                }
            }
        }

        return (float) nonAsciiCharacters / totalCharacters > 0.1;
    }

}
