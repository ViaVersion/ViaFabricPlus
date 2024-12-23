/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.viaversion.viafabricplus.fixes.versioned.classic.command.ListExtensionsCommand;
import com.viaversion.viafabricplus.fixes.versioned.classic.command.SetTimeCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.raphimc.vialoader.impl.viaversion.VLCommandHandler;

import java.util.concurrent.CompletableFuture;

public class ViaFabricPlusVLCommandHandler extends VLCommandHandler {

    public ViaFabricPlusVLCommandHandler() {
        super();

        this.removeSubCommand("list");
        this.removeSubCommand("player");
        this.removeSubCommand("pps");

        this.registerSubCommand(new ListExtensionsCommand());
        this.registerSubCommand(new SetTimeCommand());
    }

    public int execute(final CommandContext<FabricClientCommandSource> ctx) {
        String[] args = new String[0];
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ");
        } catch (IllegalArgumentException ignored) {
        }
        onCommand(new ViaFabricPlusViaCommandSender(ctx.getSource()), args);
        return 1;
    }

    public CompletableFuture<Suggestions> suggestion(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
        String[] args;
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ", -1);
        } catch (IllegalArgumentException ignored) {
            args = new String[]{""};
        }
        final String[] pref = args.clone();
        pref[pref.length - 1] = "";

        final String prefix = String.join(" ", pref);
        onTabComplete(new ViaFabricPlusViaCommandSender(ctx.getSource()), args).stream().map(it -> {
            final SuggestionsBuilder b = new SuggestionsBuilder(builder.getInput(), prefix.length() + builder.getStart());
            b.suggest(it);
            return b;
        }).forEach(builder::add);
        return builder.buildFuture();
    }

}
