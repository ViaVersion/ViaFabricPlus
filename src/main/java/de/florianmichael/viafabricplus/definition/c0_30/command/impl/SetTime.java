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
package de.florianmichael.viafabricplus.definition.c0_30.command.impl;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.definition.c0_30.command.ICommand;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.storage.TimeLockStorage;

public class SetTime implements ICommand {
    @Override
    public String name() {
        return "settime";
    }

    @Override
    public String description() {
        return "<Time (Long)>";
    }

    @Override
    public void execute(String[] args) throws Exception {
        final UserConnection connection = currentViaConnection();
        if (!connection.has(TimeLockStorage.class)) {
            this.sendFeedback(Formatting.RED + "This command is only for <=" + LegacyProtocolVersion.a1_0_16toa1_0_16_2.getName());
            return;
        }
        try {
            if (args.length == 1) {
                final long time = Long.parseLong(args[0]) % 24_000L;
                connection.get(TimeLockStorage.class).setTime(time);
                this.sendFeedback(Formatting.GREEN + "Time has been set to " + Formatting.GOLD + time);
            } else {
                this.sendUsage();
            }
        } catch (Throwable ignored) {
            this.sendUsage();
        }
    }
}
