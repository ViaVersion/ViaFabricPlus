/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.networking.remove_signed_commands;

import com.viaversion.viafabricplus.util.NotificationUtil;
import com.viaversion.viaversion.api.minecraft.GameMode;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ClientboundPacket1_21_5;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPacket1_21_5;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPackets1_21_5;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.Protocol1_21_5To1_21_6;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ClientboundPacket1_21_6;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ServerboundPacket1_21_6;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ServerboundPackets1_21_6;
import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocol1_21_5To1_21_6.class, remap = false)
public abstract class MixinProtocol1_21_5To1_21_6 extends AbstractProtocol<ClientboundPacket1_21_5, ClientboundPacket1_21_6, ServerboundPacket1_21_5, ServerboundPacket1_21_6> {

    public MixinProtocol1_21_5To1_21_6(final Class<ClientboundPacket1_21_5> unmappedClientboundPacketType, final Class<ClientboundPacket1_21_6> mappedClientboundPacketType, final Class<ServerboundPacket1_21_5> mappedServerboundPacketType, final Class<ServerboundPacket1_21_6> unmappedServerboundPacketType) {
        super(unmappedClientboundPacketType, mappedClientboundPacketType, mappedServerboundPacketType, unmappedServerboundPacketType);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    private void cancelInvalidPackets(CallbackInfo ci) {
        registerServerbound(ServerboundPackets1_21_6.CHANGE_GAME_MODE, ServerboundPackets1_21_5.CHAT_COMMAND, wrapper -> {
            if (wrapper.user().getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
                // In VFP, unsigned commands are not allowed to be sent in VV protocols as we implemented signing on a client level
                // where we can't reach and fixup signatures in all cases.
                NotificationUtil.warnIncompatibilityPacket("1.21.6", "CHANGE_GAME_MODE", null, null);
                wrapper.cancel();
                return;
            }

            final int gameMode = wrapper.read(Types.VAR_INT);
            final GameMode mode = GameMode.getById(gameMode);
            wrapper.write(Types.STRING, "gamemode " + mode.name().toLowerCase(Locale.ROOT));
        }, true);
    }

}
