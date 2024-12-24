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

package com.viaversion.viafabricplus.injection.mixin.base.connection;

import com.viaversion.viafabricplus.injection.access.base.IClientConnection;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.storage.ProtocolMetadataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientLoginNetworkHandler.class)
public abstract class MixinClientLoginNetworkHandler {

    @Shadow
    @Final
    private ClientConnection connection;

    @Inject(method = "joinServerSession", at = @At("HEAD"), cancellable = true)
    public void onlyVerifySessionInOnlineMode(String serverId, CallbackInfoReturnable<Text> cir) {
        final IClientConnection mixinClientConnection = (IClientConnection) connection;
        if (mixinClientConnection.viaFabricPlus$getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // We are in the 1.7 -> 1.6 protocol, so we need to skip the joinServer call
            // if the server is in offline mode, due the packet changes <-> networking changes
            // Minecraft's networking code is bad for us.
            if (!mixinClientConnection.viaFabricPlus$getUserConnection().get(ProtocolMetadataStorage.class).authenticate) {
                cir.setReturnValue(null);
            }
        }
    }

}
