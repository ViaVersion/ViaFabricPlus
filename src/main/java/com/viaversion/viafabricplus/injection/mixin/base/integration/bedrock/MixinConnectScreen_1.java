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

package com.viaversion.viafabricplus.injection.mixin.base.integration.bedrock;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.injection.access.base.IClientConnection;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.io.IOException;
import java.security.KeyPair;
import java.util.UUID;
import net.minecraft.network.Connection;
import net.raphimc.minecraftauth.bedrock.BedrockAuthManager;
import net.raphimc.minecraftauth.bedrock.model.MinecraftCertificateChain;
import net.raphimc.minecraftauth.bedrock.model.MinecraftMultiplayerToken;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.storage.AuthData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;syncUninterruptibly()Lio/netty/channel/ChannelFuture;", remap = false, shift = At.Shift.AFTER))
    private void setupBedrockAccount(CallbackInfo ci, @Local Connection clientConnection) throws IOException {
        final UserConnection connection = ((IClientConnection) clientConnection).viaFabricPlus$getUserConnection();

        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            final BedrockAuthManager bedrockSession = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount();
            if (bedrockSession != null) {
                final MinecraftMultiplayerToken multiplayerToken = bedrockSession.getMinecraftMultiplayerToken().refresh();
                final MinecraftCertificateChain certificateChain = bedrockSession.getMinecraftCertificateChain().refresh();
                final KeyPair sessionKeyPair = bedrockSession.getSessionKeyPair();
                final UUID deviceId = bedrockSession.getDeviceId();

                connection.put(new AuthData(certificateChain.getMojangJwt(), certificateChain.getIdentityJwt(), multiplayerToken.getToken(), sessionKeyPair, deviceId));
            } else {
                ViaFabricPlusImpl.INSTANCE.getLogger().warn("Could not get Bedrock account. Joining online mode servers will not work!");
            }
        }
    }

}
