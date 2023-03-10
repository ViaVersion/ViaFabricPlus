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

import de.florianmichael.viafabricplus.definition.c0_30.command.ClassicProtocolCommands;
import de.florianmichael.viafabricplus.definition.c0_30.command.ICommand;
import net.minecraft.util.Formatting;

public class Help implements ICommand {

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void execute(String[] args) {
        sendFeedback(Formatting.GREEN + " Loaded " + Formatting.GOLD + ClassicProtocolCommands.commands.size() + Formatting.GREEN + " commands");

        for (ICommand command : ClassicProtocolCommands.commands) {
            if (command.name().equals(name())) continue;
            command.sendUsage();
        }
    }
}
