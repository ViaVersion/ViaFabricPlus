/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.util;

import java.text.DecimalFormat;

// Stolen from https://github.com/FlorianMichael/RClasses/blob/main/common/src/main/java/de/florianmichael/rclasses/common/StringUtils.java
public class StringUtil {

    /**
     * Convention: IEC 60027-2
     */
    private final static String[] BYTES_UNIT = {"B", "KiB", "MiB", "GiB", "TiB"};
    private final static DecimalFormat OPTIONAL_FORMAT = new DecimalFormat("#.##");

    /**
     * Formats a value in bytes to a human-readable format
     *
     * @param value The raw value in bytes
     * @return The formatted value in bytes
     */
    public static String formatBytes(final long value) {
        int index = (int) (Math.log(value) / Math.log(1024.0));
        double data = value / Math.pow(1024.0, index);
        if (index < 0) index = 0;
        if (Double.isNaN(data)) data = 0;

        return OPTIONAL_FORMAT.format(data) + " " + BYTES_UNIT[index];
    }

}
