/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.definition.classic.command.impl;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.definition.classic.command.ClassicViaSubCommand;
import de.florianmichael.viafabricplus.injection.access.IExtensionProtocolMetadataStorage;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtensionProtocolMetadataStorage;
import net.raphimc.vialoader.util.VersionEnum;

public class ListExtensionsCommand extends ClassicViaSubCommand {
    @Override
    public String name() {
        return "listextensions";
    }

    @Override
    public String description() {
        return "Shows all classic extensions (only for " + VersionEnum.c0_30cpe.getName() + ")";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        final UserConnection connection = getUser();
        if (!connection.has(ExtensionProtocolMetadataStorage.class)) {
            sendMessage(sender, Formatting.RED + "Only for " + VersionEnum.c0_30cpe.getName());
            return true;
        }
        ((IExtensionProtocolMetadataStorage) connection.get(ExtensionProtocolMetadataStorage.class)).viaFabricPlus$getServerExtensions().forEach((extension, version) -> {
            sendMessage(sender, Formatting.GREEN + extension.getName() + Formatting.GOLD + " v" + version);
        });
        return true;
    }
}
