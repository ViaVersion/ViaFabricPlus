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

package com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion;

import com.viaversion.viafabricplus.util.NotificationUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.provider.PickItemProvider;

public final class ViaFabricPlusPickItemProvider extends PickItemProvider {

    @Override
    public void pickItemFromBlock(UserConnection connection, BlockPosition blockPosition, boolean includeData) {
        NotificationUtil.warnIncompatibilityPacket("1.21.4", "PICK_ITEM_FROM_BLOCK", "MinecraftClient#doItemPick", "Minecraft#pickBlock");
    }

    @Override
    public void pickItemFromEntity(UserConnection connection, int entityId, boolean includeData) {
        NotificationUtil.warnIncompatibilityPacket("1.21.4", "PICK_ITEM_FROM_ENTITY", "MinecraftClient#doItemPick", "Minecraft#pickBlock");
    }

}
