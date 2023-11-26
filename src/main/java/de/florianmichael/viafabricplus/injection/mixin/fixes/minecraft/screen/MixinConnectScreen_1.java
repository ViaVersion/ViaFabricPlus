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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.minecraft.signature.storage.ChatSession1_19_0;
import com.viaversion.viaversion.api.minecraft.signature.storage.ChatSession1_19_1;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.injection.access.ILegacyKeySignatureStorage;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.raphimc.viabedrock.protocol.storage.AuthChainData;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.UUID;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @Final
    @Shadow
    ServerAddress field_33737;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;", remap = false))
    private String getRealAddress(InetSocketAddress instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_17)) {
            return field_33737.getAddress();
        }

        return instance.getHostName();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I", remap = false))
    private int getRealPort(InetSocketAddress instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_17)) {
            return field_33737.getPort();
        }

        return instance.getPort();
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;syncUninterruptibly()Lio/netty/channel/ChannelFuture;", shift = At.Shift.AFTER))
    private void setupConnectionSessions(CallbackInfo ci, @Local ClientConnection clientConnection) {
        final UserConnection userConnection = ((IClientConnection) clientConnection).viaFabricPlus$getUserConnection();
        if (userConnection == null) return;

        final VersionEnum targetVersion = VersionEnum.fromUserConnection(userConnection);

        if (targetVersion.isBetweenInclusive(VersionEnum.r1_19, VersionEnum.r1_19_1tor1_19_2)) {
            final var keyPair = MinecraftClient.getInstance().getProfileKeys().fetchKeyPair().join().orElse(null);
            if (keyPair != null) {
                final PlayerPublicKey.PublicKeyData publicKeyData = keyPair.publicKey().data();

                final UUID playerUuid = MinecraftClient.getInstance().getSession().getUuidOrNull();

                userConnection.put(new ChatSession1_19_1(playerUuid, keyPair.privateKey(), new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), publicKeyData.keySignature())));
                if (targetVersion == VersionEnum.r1_19) {
                    final var legacyKey = ((ILegacyKeySignatureStorage) (Object) publicKeyData).viafabricplus$getLegacyPublicKeySignature();
                    if (legacyKey != null) {
                        userConnection.put(new ChatSession1_19_0(playerUuid, keyPair.privateKey(), new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), legacyKey)));
                    } else {
                        ViaFabricPlus.global().getLogger().error("Failed to fetch legacy key, can't setup ChatSession");
                    }
                }
            } else {
                ViaFabricPlus.global().getLogger().error("Failed to fetch keyPair, can't setup ChatSession");
            }
        } else if (targetVersion == VersionEnum.bedrockLatest) {
            var bedrockSession = ViaFabricPlus.global().getSaveManager().getAccountsSave().refreshAndGetBedrockAccount();
            if (bedrockSession != null) {
                final var deviceId = bedrockSession.getMcChain().getXblXsts().getInitialXblSession().getXblDeviceToken().getId();
                final var playFabId = bedrockSession.getPlayFabToken().getPlayFabId();
                final var mcChain = bedrockSession.getMcChain();

                userConnection.put(new AuthChainData(mcChain.getMojangJwt(), mcChain.getIdentityJwt(), mcChain.getPublicKey(), mcChain.getPrivateKey(), deviceId, playFabId));
            }
        }
    }

}
