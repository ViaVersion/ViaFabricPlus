/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes.classic.command;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.protocolhack.impl.command.VFPViaSubCommand;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.protocols.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.storage.TimeLockStorage;
import net.raphimc.vialoader.util.VersionEnum;

public class SetTimeCommand extends VFPViaSubCommand {

    @Override
    public String name() {
        return "settime";
    }

    @Override
    public String description() {
        return "Changes the time (Only for <= " + VersionEnum.a1_0_16toa1_0_16_2.getName() + ")";
    }

    @Override
    public String usage() {
        return name() + " " + "<Time (Long)>";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        final UserConnection connection = getUser();
        if (!connection.has(TimeLockStorage.class)) {
            sendMessage(sender, Formatting.RED + "Only for <= " + VersionEnum.a1_0_16toa1_0_16_2.getName());
            return true;
        }
        try {
            if (args.length == 1) {
                final long time = Long.parseLong(args[0]) % 24_000L;
                connection.get(TimeLockStorage.class).setTime(time);
                sendMessage(sender, Formatting.GREEN + "Time has been set to " + Formatting.GOLD + time);
            } else {
                return false;
            }
        } catch (Throwable ignored) {
            return false;
        }
        return true;
    }

}
