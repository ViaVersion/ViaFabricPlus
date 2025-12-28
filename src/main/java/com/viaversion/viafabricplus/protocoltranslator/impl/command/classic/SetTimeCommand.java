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

package com.viaversion.viafabricplus.protocoltranslator.impl.command.classic;

import com.viaversion.viafabricplus.protocoltranslator.impl.command.VFPViaSubCommand;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import net.minecraft.ChatFormatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.alpha.a1_0_16_2toa1_0_17_1_0_17_4.storage.TimeLockStorage;

public final class SetTimeCommand implements VFPViaSubCommand {

    @Override
    public String name() {
        return "settime";
    }

    @Override
    public String description() {
        return "Changes the time (Only for <= " + LegacyProtocolVersion.a1_0_16toa1_0_16_2.getName() + ")";
    }

    @Override
    public String usage() {
        return name() + " " + "<Time (Long)>";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        if (getUser() == null || !getUser().has(TimeLockStorage.class)) {
            sendMessage(sender, ChatFormatting.RED + "Only for <= " + LegacyProtocolVersion.a1_0_16toa1_0_16_2.getName());
            return true;
        }
        try {
            if (args.length == 1) {
                final long time = Long.parseLong(args[0]) % 24_000L;
                getUser().get(TimeLockStorage.class).setTime(time);
                sendMessage(sender, ChatFormatting.GREEN + "Time has been set to " + ChatFormatting.GOLD + time);
            } else {
                return false;
            }
        } catch (Throwable ignored) {
            return false;
        }
        return true;
    }

}
