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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import de.florianmichael.viafabricplus.injection.access.IProtocol1_13To1_12_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1$3", remap = false)
public class MixinProtocol1_12To1_11_1_3 {

    @Redirect(method = "lambda$register$1", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;create(Lcom/viaversion/viaversion/api/protocol/packet/PacketType;Lcom/viaversion/viaversion/api/protocol/remapper/PacketHandler;)Lcom/viaversion/viaversion/api/protocol/packet/PacketWrapper;"))
    private static PacketWrapper writeRecipes(PacketWrapper instance, PacketType packetType, PacketHandler handler) throws Exception {
        return instance.create(packetType, wrapper -> {
            final IProtocol1_13To1_12_2 protocol = (IProtocol1_13To1_12_2) wrapper.user().getProtocolInfo().getPipeline().getProtocol(Protocol1_13To1_12_2.class);
            if (protocol == null) {
                Via.getPlatform().getLogger().warning("Cannot write recipes! 1.13 -> 1.12.2 protocol not found!");
                return;
            }

            protocol.viafabricplus_writeDeclareRecipes(wrapper);
        });
    }
}
