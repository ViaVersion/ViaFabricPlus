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

package com.viaversion.viafabricplus.protocoltranslator;

import com.viaversion.viafabricplus.api.protocoltranslator.Limitations;
import com.viaversion.viafabricplus.features.limitation.max_chat_length.MaxChatLength;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;

public final class LimitationsImpl implements Limitations {

    @Override
    public int getMaxChatLength(final ProtocolVersion version) {
        return MaxChatLength.getChatLength();
    }

    @Override
    public boolean itemExists(final Item item, final ProtocolVersion version) {
        return false;
    }

    @Override
    public boolean enchantmentExists(final ResourceKey<Enchantment> enchantment, final ProtocolVersion version) {
        return false;
    }

    @Override
    public boolean effectExists(final Holder<MobEffect> effect, final ProtocolVersion version) {
        return false;
    }

    @Override
    public boolean bannerPatternExists(final ResourceKey<BannerPattern> pattern, final ProtocolVersion version) {
        return false;
    }

    @Override
    public boolean itemExistsInConnection(final Item item) {
        return false;
    }

    @Override
    public boolean itemExistsInConnection(final ItemStack stack) {
        return false;
    }

    @Override
    public int getStackCount(final ItemStack stack) {
        return 0;
    }
}
