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

package com.viaversion.viafabricplus.features.versioned.classic.command;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viafabricplus.injection.access.IExtensionProtocolMetadataStorage;
import com.viaversion.viafabricplus.protocoltranslator.impl.command.VFPViaSubCommand;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.classic.c0_30cpetoc0_28_30.storage.ExtensionProtocolMetadataStorage;

public class ListExtensionsCommand implements VFPViaSubCommand {

    @Override
    public String name() {
        return "listextensions";
    }

    @Override
    public String description() {
        return "Shows all classic extensions (only for " + LegacyProtocolVersion.c0_30cpe.getName() + ")";
    }

    @Override
    public boolean execute(ViaCommandSender sender, String[] args) {
        if (getUser() == null || !getUser().has(ExtensionProtocolMetadataStorage.class)) {
            sendMessage(sender, Formatting.RED + "Only for " + LegacyProtocolVersion.c0_30cpe.getName());
            return true;
        }
        ((IExtensionProtocolMetadataStorage) getUser().get(ExtensionProtocolMetadataStorage.class)).viaFabricPlus$getServerExtensions().forEach((extension, version) -> {
            sendMessage(sender, Formatting.GREEN + extension.getName() + Formatting.GOLD + " v" + version);
        });
        return true;
    }

}
