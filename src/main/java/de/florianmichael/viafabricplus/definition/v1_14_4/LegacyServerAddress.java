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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import de.florianmichael.vialoadingbase.platform.ProtocolRange;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

public class LegacyServerAddress {
    private final static ProtocolRange SRV_RANGE = new ProtocolRange(LegacyProtocolVersion.r1_3_1tor1_3_2, ProtocolVersion.v1_16_4);

    public static ServerAddress parse(ComparableProtocolVersion version, String address) {
        if (version == null) version = ViaLoadingBase.getClassWrapper().getTargetVersion();
        final ServerAddress mc = ServerAddress.parse(address);
        if (SRV_RANGE.contains(version) || version.isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            if (!mc.equals(ServerAddress.INVALID)) {
                return AllowedAddressResolver.DEFAULT.redirectResolver.lookupRedirect(mc).orElse(mc);
            }
        }
        return mc;
    }
}
