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

package com.viaversion.viafabricplus.util;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class NotificationUtil {

    public static void warnIncompatibilityPacket(final String version, final String packet, final @Nullable String yarnMethod, final @Nullable String mojmapMethod) {
        final Logger logger = ViaFabricPlusImpl.INSTANCE.getLogger();
        logger.error("===========================================");
        logger.error("The {} packet (>= {}) could not be remapped without breaking content!", packet, version);
        logger.error("Try disabling mods one by one or using a binary search method to identify the problematic mod.");
        if (yarnMethod != null && mojmapMethod != null) {
            logger.error("Mods authors should use {} (Yarn) or {} (Mojmap) instead of sending packets directly.", yarnMethod, mojmapMethod);
        } else {
            logger.error("Mod authors should not send this packet directly.");
        }
        logger.error("Need help? Join our Discord: https://discord.gg/viaversion");
        logger.error("===========================================");
    }

}
