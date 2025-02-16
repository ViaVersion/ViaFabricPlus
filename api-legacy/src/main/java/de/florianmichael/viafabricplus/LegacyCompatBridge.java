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

package de.florianmichael.viafabricplus;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.base.event.EventFactoryImpl;

import java.util.function.Function;

public class LegacyCompatBridge {

    private static boolean warned = false;

    public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
        warn();
        return EventFactoryImpl.createArrayBacked(type, invokerFactory);
    }

    public static void warn() {
        if (!warned) {
            warned = true;
            ViaFabricPlus.INSTANCE.getLogger().error("===========================================");
            ViaFabricPlus.INSTANCE.getLogger().error("A mod is using deprecated ViaFabricPlus internals which will be removed in the future.");
            ViaFabricPlus.INSTANCE.getLogger().error("Please contact the mod author to update their code to use the general API point added in 4.0.0.");
            ViaFabricPlus.INSTANCE.getLogger().error("The error will point to the mod calling the deprecated API:", new Exception());
            ViaFabricPlus.INSTANCE.getLogger().error("===========================================");
        }
    }

}
