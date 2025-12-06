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

package com.viaversion.viafabricplus.features.item.negative_item_count;

import com.viaversion.viafabricplus.util.ItemUtil;
import com.viaversion.viaversion.protocols.v1_10to1_11.Protocol1_10To1_11;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public final class NegativeItemUtil {

    /**
     * Returns the actual amount of items in the stack, versions older or equal to 1.10 can have negative stack sizes
     * which are not represented by {@link ItemStack#getCount()}.
     *
     * @param stack The stack to get the count from
     * @return The actual amount of items in the stack
     */
    public static int getCount(final ItemStack stack) {
        final CompoundTag tag = ItemUtil.getTagOrNull(stack);
        if (tag != null) {
            return tag.getIntOr(ItemUtil.vvNbtName(Protocol1_10To1_11.class), stack.getCount());
        } else {
            return stack.getCount();
        }
    }

}
