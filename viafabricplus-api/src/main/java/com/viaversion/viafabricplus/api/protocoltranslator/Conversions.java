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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Protocol translator conversions.
 */
public interface Conversions {

    /**
     * Converts a Minecraft item stack {@link ItemStack} to a ViaVersion item {@link Item}
     *
     * @param stack         The Minecraft item stack to convert {@link ItemStack}
     * @param targetVersion The target version to convert to (e.g., v1.13) {@link ProtocolVersion}
     * @return The ViaVersion item for the target version {@link Item}
     */
    @Nullable Item translateItem(final ItemStack stack, final ProtocolVersion targetVersion);

    /**
     * Converts a ViaVersion item {@link Item} to a Minecraft item stack {@link ItemStack}
     *
     * @param item          The ViaVersion item to convert {@link Item}
     * @param sourceVersion The source version of the item (e.g., b1.8) {@link ProtocolVersion}
     * @return The Minecraft item stack for the source version {@link ItemStack}
     */
    @NotNull ItemStack translateItem(final Item item, final ProtocolVersion sourceVersion);

    /**
     * Creates a dummy user connection for the given client and server versions. Packets passed through this
     * will not send any outgoing packets to the actual connection. Useful for when implementing translators using
     * existing Minecraft packets.
     *
     * @param clientVersion The client version
     * @param serverVersion The server version
     * @return The dummy user connection
     */
    UserConnection createDummyUserConnection(final ProtocolVersion clientVersion, final ProtocolVersion serverVersion);

}
