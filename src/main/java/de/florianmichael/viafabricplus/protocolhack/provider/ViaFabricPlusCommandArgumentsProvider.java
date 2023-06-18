/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.protocolhack.provider;

import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viafabricplus.definition.signatures.v1_19_0.provider.CommandArgumentsProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.argument.SignedArgumentList;

import java.util.List;

public class ViaFabricPlusCommandArgumentsProvider extends CommandArgumentsProvider {

    @Override
    public List<Pair<String, String>> getSignedArguments(String command) {
        final ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();

        if (clientPlayNetworkHandler != null) {
            return SignedArgumentList.of(clientPlayNetworkHandler.getCommandDispatcher().parse(command, clientPlayNetworkHandler.getCommandSource())).arguments().stream().map(function -> new Pair<>(function.getNodeName(), function.value())).toList();
        }
        return super.getSignedArguments(command);
    }
}
