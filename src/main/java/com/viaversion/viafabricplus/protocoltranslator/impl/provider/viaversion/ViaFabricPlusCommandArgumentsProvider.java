/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion;

import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider;
import com.viaversion.viaversion.util.Pair;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.SignableCommand;

public final class ViaFabricPlusCommandArgumentsProvider extends SignableCommandArgumentsProvider {

    @Override
    public List<Pair<String, String>> getSignableArguments(String command) {
        final ClientPacketListener network = Minecraft.getInstance().getConnection();

        if (network != null) {
            return SignableCommand.of(
                    network.getCommands().parse(command, network.getSuggestionsProvider())).
                arguments().stream().
                map(function -> new Pair<>(function.name(), function.value())).
                toList();
        } else {
            return Collections.emptyList();
        }
    }

}
