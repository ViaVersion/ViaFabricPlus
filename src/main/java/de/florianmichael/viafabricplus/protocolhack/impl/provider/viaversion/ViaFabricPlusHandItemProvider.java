/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.impl.provider.viaversion;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import de.florianmichael.viafabricplus.protocolhack.translator.ItemTranslator;
import net.minecraft.item.ItemStack;
import net.raphimc.vialoader.util.VersionEnum;

public class ViaFabricPlusHandItemProvider extends HandItemProvider {
    public static ItemStack lastUsedItem = null;

    @Override
    public Item getHandItem(UserConnection info) {
        if (lastUsedItem == null || lastUsedItem.isEmpty()) {
            return null;
        }

        return ItemTranslator.mcToVia(lastUsedItem, VersionEnum.r1_8);
    }

}
