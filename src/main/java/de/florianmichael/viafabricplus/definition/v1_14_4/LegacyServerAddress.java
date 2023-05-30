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
package de.florianmichael.viafabricplus.definition.v1_14_4;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

public class LegacyServerAddress {
    private final static VersionRange SRV_RANGE = new VersionRange(VersionEnum.r1_16_4tor1_16_5, VersionEnum.r1_3_1tor1_3_2);

    public static ServerAddress parse(VersionEnum version, String address) {
        if (version == null) version = ProtocolHack.getTargetVersion();
        final ServerAddress mc = ServerAddress.parse(address);
        if (SRV_RANGE.contains(version) || version == VersionEnum.bedrockLatest) {
            if (!mc.equals(ServerAddress.INVALID)) {
                return AllowedAddressResolver.DEFAULT.redirectResolver.lookupRedirect(mc).orElse(mc);
            }
        }
        return mc;
    }
}
