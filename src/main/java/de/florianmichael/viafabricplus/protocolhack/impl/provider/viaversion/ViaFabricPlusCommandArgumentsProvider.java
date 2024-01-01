/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider;
import com.viaversion.viaversion.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.SignedArgumentList;

import java.util.Collections;
import java.util.List;

public class ViaFabricPlusCommandArgumentsProvider extends SignableCommandArgumentsProvider {

    @Override
    public List<Pair<String, String>> getSignableArguments(String command) {
        final var network = MinecraftClient.getInstance().getNetworkHandler();
        if (network != null) {
            return SignedArgumentList.of(
                    network.getCommandDispatcher().parse(command, network.getCommandSource())).
                    arguments().stream().
                    map(function -> new Pair<>(function.getNodeName(), function.value())).
                    toList();

        }
        return Collections.emptyList();
    }

}
