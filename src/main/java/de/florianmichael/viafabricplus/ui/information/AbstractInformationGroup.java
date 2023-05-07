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
package de.florianmichael.viafabricplus.ui.information;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialoadingbase.model.ProtocolRange;

import java.util.List;

public abstract class AbstractInformationGroup {
    private final ProtocolRange protocolRange;

    public AbstractInformationGroup(final ProtocolRange protocolRange) {
        this.protocolRange = protocolRange;
    }

    public abstract void applyInformation(final UserConnection userConnection, final List<String> output);

    public String format(double a) {
        return String.format("%.2f", a);
    }

    public String formatBytes(long value) {
        if (value < 1024L)
            return value + " B";
        else if (value < 1024L * 1024L)
            return format(((double) value / 1024.0)) + " Kb";
        else if (value < 1024L * 1024L * 1024L)
            return format(((double) value / 1024.0 / 1024.0)) + " Mb";
        else if (value < 1024L * 1024L * 1024L * 1024L)
            return format(((double) value / 1024.0 / 1024.0 / 1024.0)) + " Gb";
        else
            return format(((double) value / 1024.0 / 1024.0 / 1024.0 / 1024.0)) + " Tb";
    }

    public ProtocolRange getProtocolRange() {
        return protocolRange;
    }
}
