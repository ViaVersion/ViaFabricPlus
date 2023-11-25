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

package de.florianmichael.viafabricplus.injection.mixin.viaversion;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import net.raphimc.vialoader.util.VersionEnum;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.AbstractFenceConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.GlassConnectionHandler;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GlassConnectionHandler.class, remap = false)
public abstract class MixinGlassConnectionHandler extends AbstractFenceConnectionHandler {

    protected MixinGlassConnectionHandler(String blockConnections) {
        super(blockConnections);
    }

    /**
     * @author ViaVersion, EnZaXD
     * @reason Add support for pre-netty protocols
     */
    @Overwrite
    public byte getStates(UserConnection user, Position position, int blockState) {
        byte states = super.getStates(user, position, blockState);
        if (states != 0) return states;

        ProtocolInfo protocolInfo = user.getProtocolInfo();
        return ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8) && protocolInfo.getServerProtocolVersion() != -1 ? 0xF : states;
    }
}
