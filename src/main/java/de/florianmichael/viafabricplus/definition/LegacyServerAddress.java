/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.definition;

import net.minecraft.client.network.ServerAddress;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public class LegacyServerAddress {
    public final static VersionRange SRV_RANGE = VersionRange.andOlder(VersionEnum.r1_2_4tor1_2_5).add(VersionRange.single(VersionEnum.bedrockLatest));

    public static ServerAddress parse(VersionEnum version, String address) {
        final ServerAddress modern = ServerAddress.parse(address);
        if (SRV_RANGE.contains(version) && !modern.equals(ServerAddress.INVALID)) {
            final var addressParts = address.split(":");
            return new ServerAddress(addressParts[0], addressParts.length > 1 ? Integer.parseInt(addressParts[1]) : 25565);
        }
        return modern;
    }
}
