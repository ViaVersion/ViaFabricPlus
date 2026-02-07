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

import com.viaversion.viaversion.api.protocol.Protocol;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public final class ItemUtil {

    // ViaVersion's 1.20.5 -> 1.20.3 protocol will save the original item nbt inside custom data to later restore
    // it for creative clients, we can use this to get nbt stored in older protocols as well
    public static CompoundTag getTagOrNull(final ItemStack stack) {
        final CustomData tag = stack.get(DataComponents.CUSTOM_DATA);
        if (tag != null) {
            return tag.copyTag();
        } else {
            return null;
        }
    }

    // See ItemRewriter#nbtTagName for the format

    public static String vvNbtName(final Class<? extends Protocol<?, ?, ?, ?>> protocolClass) {
        return "VV|" + protocolClass.getSimpleName();
    }

    public static String vvNbtName(final Class<? extends Protocol<?, ?, ?, ?>> protocolClass, final String name) {
        return "VV|" + protocolClass.getSimpleName() + "|" + name;
    }

}
