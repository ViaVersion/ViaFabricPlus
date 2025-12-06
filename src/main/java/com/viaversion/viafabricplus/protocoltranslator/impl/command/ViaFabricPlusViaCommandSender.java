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

package com.viaversion.viafabricplus.protocoltranslator.impl.command;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import java.util.UUID;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public final class ViaFabricPlusViaCommandSender implements ViaCommandSender {

    private final SharedSuggestionProvider source;

    public ViaFabricPlusViaCommandSender(final SharedSuggestionProvider source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(String s) {
        return true;
    }

    @Override
    public void sendMessage(String s) {
        ((FabricClientCommandSource) source).sendFeedback(Component.nullToEmpty(s.replace("/viaversion", "/viafabricplus"))); // ViaVersion doesn't support changing the root command name, so we have to do it ourselves
    }

    @Override
    public UUID getUUID() {
        return ((FabricClientCommandSource) source).getPlayer().getUUID();
    }

    @Override
    public String getName() {
        return ((FabricClientCommandSource) source).getPlayer().getName().getString();
    }

}
