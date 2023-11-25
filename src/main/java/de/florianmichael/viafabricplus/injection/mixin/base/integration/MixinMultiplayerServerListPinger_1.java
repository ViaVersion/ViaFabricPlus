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

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
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
        final UserConnection userConnection = ((IClientConnection) field_3774).viaFabricPlus$getUserConnection();

        // If ViaVersion is translating the current connection, we track the target version, and it's state in the server info
        // So we can later draw this information when hovering over the ping bar in the server list
        if (userConnection != null) {
            ((IServerInfo) field_3776).viaFabricPlus$enable();
            ((IServerInfo) field_3776).viaFabricPlus$setTranslatingVersion(userConnection.getProtocolInfo().getServerProtocolVersion());
        }
    }

    @Inject(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerMetadata;version()Ljava/util/Optional;", shift = At.Shift.AFTER))
    private void setProtocolVersion(CallbackInfo ci) {
        final boolean isCompatible;
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_6_4)) {
            // Because of ViaVersion not supporting legacy minecraft versions where protocol ids are overlapping, ViaLegacy
            // has its own protocol id offset, where realVersion = -(ViaLegacyVersion >> 2). Normally ViaVersion sends the client
            // version to the client so its detection doesn't break when checking for serverVersion == clientVersion, but since
            // ViaLegacy doesn't do that, we have to do it ourselves
            isCompatible = LegacyProtocolVersion.getRealProtocolVersion(((IClientConnection) this.field_3774).viaFabricPlus$getTargetVersion().getVersion()) == this.field_3776.protocolVersion;
        } else if (ProtocolHack.getTargetVersion().equals(VersionEnum.bedrockLatest)) {
            // Bedrock edition doesn't have a protocol id like the Java edition, ViaBedrock also has its own protocol id offset
            // Which we need to remove to get the real protocol id
            isCompatible = ((IClientConnection) this.field_3774).viaFabricPlus$getTargetVersion().getVersion() - BedrockProtocolVersion.PROTOCOL_ID_OVERLAP_PREVENTION_OFFSET == this.field_3776.protocolVersion;
        } else {
            return;
        }

        // If the server is compatible with the client, we set the protocol version to the client version
        if (isCompatible) {
            this.field_3776.protocolVersion = SharedConstants.getProtocolVersion();
        }
    }

}
