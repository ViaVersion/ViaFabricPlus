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

package com.viaversion.viafabricplus.api.protocoltranslator;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;

public interface Limitations {

    /**
     * Calculates the maximum chat length for given {@link ProtocolVersion} instance.
     *
     * @return The maximum chat length
     */
    int getMaxChatLength(final ProtocolVersion version);

    /**
     * @param item    The item to check
     * @param version The version to check for
     * @return true if the item exists in the given version, false otherwise; this will also check for CPE items (CustomBlocks V1 extension)
     */
    boolean itemExists(final net.minecraft.world.item.Item item, final ProtocolVersion version);

    /**
     * @param enchantment The enchantment to check
     * @param version     The version to check for
     * @return true if the enchantment exists in the given version, false otherwise
     */
    boolean enchantmentExists(final ResourceKey<Enchantment> enchantment, final ProtocolVersion version);

    /**
     * @param effect  The status effect to check
     * @param version The version to check for
     * @return true if the status effect exists in the given version, false otherwise
     */
    boolean effectExists(final Holder<MobEffect> effect, final ProtocolVersion version);

    /**
     * @param pattern The banner pattern to check
     * @param version The version to check for
     * @return true if the banner pattern exists in the given version, false otherwise
     */
    boolean bannerPatternExists(final ResourceKey<BannerPattern> pattern, final ProtocolVersion version);

    /**
     * Similar to {@link #itemExists(net.minecraft.world.item.Item, ProtocolVersion)}, but takes in the current connection details (e.g., classic protocol extensions being loaded)
     *
     * @param item The item to check
     * @return true if the item exists in the current connection, false otherwise
     */
    boolean itemExistsInConnection(final net.minecraft.world.item.Item item);

    /**
     * Same as {@link #itemExists(net.minecraft.world.item.Item, ProtocolVersion)}, but for item stacks. This also compares against certain data components like enchantments or banner patterns.
     *
     * @param stack The item stack to check
     * @return true if the item stack exists in the given version, false otherwise
     */
    boolean itemExistsInConnection(final ItemStack stack);

    /**
     * Similar to {@link ItemStack#getCount()}, but also handles negative item counts in pre 1.11 versions
     *
     * @param stack The item stack to get the count of
     * @return the count of the item stack can be negative in pre 1.11 versions
     */
    int getStackCount(final ItemStack stack);

}
