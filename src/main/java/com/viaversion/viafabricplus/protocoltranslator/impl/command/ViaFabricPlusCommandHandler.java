/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

package com.viaversion.viafabricplus.protocoltranslator.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.viaversion.viafabricplus.protocoltranslator.impl.command.classic.ListExtensionsCommand;
import com.viaversion.viafabricplus.protocoltranslator.impl.command.classic.SetTimeCommand;
import java.util.concurrent.CompletableFuture;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class ViaFabricPlusCommandHandler extends ViaCommandHandler {

    public ViaFabricPlusCommandHandler() {
        super(false);

        this.removeSubCommand("list");
        this.removeSubCommand("player");
        this.removeSubCommand("pps");

        this.registerSubCommand(new ListExtensionsCommand());
        this.registerSubCommand(new SetTimeCommand());
        this.registerSubCommand(new SettingsCommand());
    }

    public int execute(final CommandContext<FabricClientCommandSource> ctx) {
        String[] args = new String[0];
        try {
            args = StringArgumentType.getString(ctx, "args").split(" ");
        } catch (IllegalArgumentException ignored) {
        }
        onCommand(new ViaFabricPlusCommandSender(ctx.getSource()), args);
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
        onTabComplete(new ViaFabricPlusCommandSender(ctx.getSource()), args).stream().map(it -> {
            final SuggestionsBuilder b = new SuggestionsBuilder(builder.getInput(), prefix.length() + builder.getStart());
            b.suggest(it);
            return b;
        }).forEach(builder::add);
        return builder.buildFuture();
    }

}
