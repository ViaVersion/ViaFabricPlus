/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.injection.access.ILegacyKeySignatureStorage;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.raphimc.minecraftauth.step.bedrock.StepMCChain;
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.storage.AuthChainData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.UUID;

@Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @Final
    @Shadow
    ServerAddress field_33737;

    @Shadow
    @Final
    MinecraftClient field_33738;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;", remap = false))
    private String getRealAddress(InetSocketAddress instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_17)) {
            return field_33737.getAddress();
        } else {
            return instance.getHostName();
        }
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I", remap = false))
    private int getRealPort(InetSocketAddress instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_17)) {
            return field_33737.getPort();
        } else {
            return instance.getPort();
        }
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelFuture;syncUninterruptibly()Lio/netty/channel/ChannelFuture;", remap = false, shift = At.Shift.AFTER))
    private void setupConnectionSessions(CallbackInfo ci, @Local ClientConnection clientConnection) {
        final UserConnection userConnection = ((IClientConnection) clientConnection).viaFabricPlus$getUserConnection();

        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_19, ProtocolVersion.v1_19_1)) {
            final PlayerKeyPair keyPair = MinecraftClient.getInstance().getProfileKeys().fetchKeyPair().join().orElse(null);
            if (keyPair != null) {
                final PlayerPublicKey.PublicKeyData publicKeyData = keyPair.publicKey().data();
                final PrivateKey privateKey = keyPair.privateKey();
                final long expiresAt = publicKeyData.expiresAt().toEpochMilli();
                final byte[] publicKey = publicKeyData.key().getEncoded();
                final UUID uuid = this.field_33738.getSession().getUuidOrNull();

                userConnection.put(new ChatSession1_19_1(uuid, privateKey, new ProfileKey(expiresAt, publicKey, publicKeyData.keySignature())));
                if (ProtocolTranslator.getTargetVersion() == ProtocolVersion.v1_19) {
                    final byte[] legacyKeySignature = ((ILegacyKeySignatureStorage) (Object) publicKeyData).viafabricplus$getLegacyPublicKeySignature();
                    if (legacyKeySignature != null) {
                        userConnection.put(new ChatSession1_19_0(uuid, privateKey, new ProfileKey(expiresAt, publicKey, legacyKeySignature)));
                    }
                }
            } else {
                ViaFabricPlus.global().getLogger().error("Could not get public key signature. Joining servers with enforce-secure-profiles enabled will not work!");
            }
        } else if (ProtocolTranslator.getTargetVersion() == BedrockProtocolVersion.bedrockLatest) {
            final StepFullBedrockSession.FullBedrockSession bedrockSession = ViaFabricPlus.global().getSaveManager().getAccountsSave().refreshAndGetBedrockAccount();
            if (bedrockSession != null) {
                final StepMCChain.MCChain mcChain = bedrockSession.getMcChain();
                final UUID deviceId = mcChain.getXblXsts().getInitialXblSession().getXblDeviceToken().getId();
                final String playFabId = bedrockSession.getPlayFabToken().getPlayFabId();

                userConnection.put(new AuthChainData(mcChain.getMojangJwt(), mcChain.getIdentityJwt(), mcChain.getPublicKey(), mcChain.getPrivateKey(), deviceId, playFabId));
            } else {
                ViaFabricPlus.global().getLogger().warn("Could not get Bedrock account. Joining online mode servers will not work!");
            }
        }
    }

}
