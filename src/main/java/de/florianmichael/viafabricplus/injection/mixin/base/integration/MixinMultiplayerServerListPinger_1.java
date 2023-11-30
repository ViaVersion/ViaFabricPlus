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

package de.florianmichael.viafabricplus.injection.mixin.base.integration;

import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1")
public abstract class MixinMultiplayerServerListPinger_1 implements ClientQueryPacketListener {

    @Final
    @Shadow
    ServerInfo field_3776;

    @Shadow
    @Final
    ClientConnection field_3774;

    @Inject(method = "onResponse(Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;)V", at = @At("HEAD"))
    private void trackTranslatingState(QueryResponseS2CPacket packet, CallbackInfo ci) {
        // If ViaVersion is translating the current connection, we track the target version, and it's state in the server info
        // So we can later draw this information when hovering over the ping bar in the server list
        if (field_3774 instanceof IClientConnection mixinClientConnection) {
            ((IServerInfo) field_3776).viaFabricPlus$setTranslatingVersion(mixinClientConnection.viaFabricPlus$getTargetVersion());
        }
    }

    @Inject(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER))
    private void setProtocolVersion(CallbackInfo ci) {
        final VersionEnum version = ((IClientConnection) this.field_3774).viaFabricPlus$getTargetVersion();

        // ViaVersion is not translating the current connection, so we don't need to do anything
        if (version == null) {
            return;
        }

        final boolean isCompatible;
        if (version.isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            // Because of ViaVersion not supporting legacy minecraft versions where protocol ids are overlapping, ViaLegacy
            // has its own protocol id offset, where realVersion = -(ViaLegacyVersion >> 2). Normally ViaVersion sends the client
            // version to the client so its detection doesn't break when checking for serverVersion == clientVersion, but since
            // ViaLegacy doesn't do that, we have to do it ourselves
            isCompatible = LegacyProtocolVersion.getRealProtocolVersion(version.getVersion()) == this.field_3776.protocolVersion;
        } else if (version.equals(VersionEnum.bedrockLatest)) {
            // Bedrock edition doesn't have a protocol id like the Java edition, ViaBedrock also has its own protocol id offset
            // Which we need to remove to get the real protocol id
            isCompatible = version.getVersion() - BedrockProtocolVersion.PROTOCOL_ID_OVERLAP_PREVENTION_OFFSET == this.field_3776.protocolVersion;
        } else {
            return;
        }

        // If the server is compatible with the client, we set the protocol version to the client version
        if (isCompatible) {
            this.field_3776.protocolVersion = SharedConstants.getProtocolVersion();
        }
    }

}
