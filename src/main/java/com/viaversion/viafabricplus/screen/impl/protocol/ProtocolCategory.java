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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public enum ProtocolCategory {

    MODERN,
    LEGACY,
    BETA,
    ALPHA,
    CLASSIC,
    APRIL_FOOLS;

    private final Component title;

    ProtocolCategory() {
        this.title = Component.translatable("protocol_selection.viafabricplus." + name().toLowerCase(Locale.ROOT));
    }

    public static ProtocolCategory of(final ProtocolVersion version) {
        // c0.30 CPE is registered as a special version and therefore doesn't report the classic version type
        if (version.equals(LegacyProtocolVersion.c0_30cpe)) {
            return CLASSIC;
        }

        return switch (version.getVersionType()) {
            case CLASSIC -> CLASSIC;
            case ALPHA_INITIAL, ALPHA_LATER -> ALPHA;
            case BETA_INITIAL, BETA_LATER -> BETA;
            case RELEASE_INITIAL -> LEGACY;
            case RELEASE -> version.olderThan(ProtocolVersion.v1_16) ? LEGACY : MODERN;
            case SPECIAL -> APRIL_FOOLS;
        };
    }

    public Component title() {
        return this.title;
    }

}
