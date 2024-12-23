/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

import com.viaversion.viaversion.protocols.v1_10to1_11.Protocol1_10To1_11;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemUtil {

    private static final String VV_IDENTIFIER = "VV|" + Protocol1_10To1_11.class.getSimpleName(); // ItemRewriter#nbtTagName

    /**
     * Returns the actual amount of items in the stack, versions older or equal to 1.10 can have negative stack sizes
     * which are not represented by {@link ItemStack#getCount()}.
     *
     * @param stack The stack to get the count from
     * @return The actual amount of items in the stack
     */
    public static int getCount(final ItemStack stack) {
        final NbtCompound tag = getTagOrNull(stack);
        if (tag != null && tag.contains(VV_IDENTIFIER)) {
            return tag.getInt(VV_IDENTIFIER);
        } else {
            return stack.getCount();
        }
    }

    // ViaVersion's 1.20.5 -> 1.20.3 protocol will save the original item nbt inside custom data to later restore
    // it for creative clients, we can use this to get nbt stored in older protocols as well
    public static NbtCompound getTagOrNull(final ItemStack stack) {
        final NbtComponent tag = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (tag != null) {
            return tag.copyNbt();
        } else {
            return null;
        }
    }

}
