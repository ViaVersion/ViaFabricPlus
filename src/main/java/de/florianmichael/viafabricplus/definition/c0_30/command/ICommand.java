/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.definition.c0_30.command;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicCustomCommandProvider;

@SuppressWarnings("DataFlowIssue")
public interface ICommand {

    String name();
    String description();

    default void sendFeedback(final String message) {
        try {
            Via.getManager().getProviders().get(ClassicCustomCommandProvider.class).sendFeedback(currentViaConnection(), ScreenUtil.prefixedMessage(message));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default void sendUsage() {
        sendFeedback(Formatting.RED + "Use: " + ClassicProtocolCommands.COMMAND_PREFIX + name() + (description() != null ? " " + description() : ""));
    }

    default UserConnection currentViaConnection() {
        return MinecraftClient.getInstance().getNetworkHandler().getConnection().channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get();
    }

    void execute(String[] args) throws Exception;
}
