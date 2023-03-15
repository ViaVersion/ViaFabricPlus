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
package de.florianmichael.viafabricplus.protocolhack.provider.vialegacy;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.definition.c0_30.command.ClassicProtocolCommands;
import de.florianmichael.viafabricplus.definition.c0_30.command.ICommand;
import de.florianmichael.viafabricplus.settings.groups.GeneralSettings;
import net.raphimc.vialegacy.ViaLegacy;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicCustomCommandProvider;

import java.util.Arrays;
import java.util.logging.Level;

public class ViaFabricPlusClassicCustomCommandProvider extends ClassicCustomCommandProvider {

    @Override
    public boolean handleChatMessage(UserConnection user, String message) {
        if (!GeneralSettings.INSTANCE.allowClassicProtocolCommandUsage.getValue()) return super.handleChatMessage(user, message);

        try {
            if (message.startsWith(ClassicProtocolCommands.COMMAND_PREFIX)) {
                message = message.substring(ClassicProtocolCommands.COMMAND_PREFIX.length());
                final String[] input = message.split(" ");
                if (input.length == 0) return super.handleChatMessage(user, message);

                for (ICommand command : ClassicProtocolCommands.INSTANCE.commands) {
                    if (input[0].equalsIgnoreCase(command.name())) {
                        command.execute(Arrays.copyOfRange(input, 1, input.length));
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            ViaLegacy.getPlatform().getLogger().log(Level.WARNING, "Error handling custom classic command", e);
        }
        return super.handleChatMessage(user, message);
    }
}
