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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import net.raphimc.mcauth.util.MicrosoftConstants;
import net.raphimc.vialoader.util.VersionEnum;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.account.BedrockAccountHandler;
import de.florianmichael.viafabricplus.definition.account.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import de.florianmichael.viafabricplus.definition.signatures.v1_19_0.ChatSession1_19_0;
import de.florianmichael.viafabricplus.definition.signatures.v1_19_2.ChatSession1_19_2;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.base.settings.groups.AuthenticationSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.raphimc.mcauth.step.bedrock.StepMCChain;
import net.raphimc.viabedrock.protocol.storage.AuthChainData;
import org.apache.http.impl.client.CloseableHttpClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Final
    @Shadow
    ServerAddress field_33737;

    @Final
    @Shadow
    ConnectScreen field_2416;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;", ordinal = 0))
    public String replaceAddress(InetSocketAddress instance) {
        if (ProtocolHack.getTargetVersion(instance).isOlderThanOrEqualTo(VersionEnum.r1_17) || ProtocolHack.getTargetVersion(instance) == VersionEnum.bedrockLatest) {
            return field_33737.getAddress();
        }
        return instance.getHostName();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I"))
    public int replacePort(InetSocketAddress instance) {
        if (ProtocolHack.getTargetVersion(instance).isOlderThanOrEqualTo(VersionEnum.r1_17) || ProtocolHack.getTargetVersion(instance) == VersionEnum.bedrockLatest) {
            return field_33737.getPort();
        }
        return instance.getPort();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V"))
    public void spoofUserName(ClientConnection instance, Packet<?> packet) {
        if (AuthenticationSettings.INSTANCE.spoofUserNameIfUsingClassiCube.getValue() && ViaFabricPlusClassicMPPassProvider.classiCubeMPPass != null && ClassiCubeAccountHandler.INSTANCE.getAccount() != null) {
            instance.send(new LoginHelloC2SPacket(ClassiCubeAccountHandler.INSTANCE.getAccount().username(), MinecraftClient.getInstance().getSession().getUuidOrNull()));
            return;
        }
        instance.send(packet);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.BEFORE))
    public void setupConnectionSessions(CallbackInfo ci) {
        final ClientConnection connection = field_2416.connection;
        if (connection == null || connection.channel == null) return;

        final UserConnection userConnection = connection.channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get();
        if (userConnection == null) return;

        final VersionEnum targetVersion = ProtocolHack.getTargetVersion(connection.channel);

        if (targetVersion == VersionEnum.bedrockLatest) {
            StepMCChain.MCChain account = BedrockAccountHandler.INSTANCE.getMcChain();
            if (account == null) return;
            final UUID deviceId = account.prevResult().initialXblSession().prevResult2().id();
            final String playFabId = BedrockAccountHandler.INSTANCE.getPlayFabToken().playFabId();

            try (final CloseableHttpClient httpClient = MicrosoftConstants.createHttpClient()) {
                account = new StepMCChain(null).applyStep(httpClient, account.prevResult());
            } catch (Exception e) {
                ViaFabricPlus.LOGGER.error("Failed to refresh Bedrock chain data. Please re-login to Bedrock!", e);
                return;
            }

            userConnection.put(new AuthChainData(account.mojangJwt(), account.identityJwt(), account.publicKey(), account.privateKey(), deviceId, playFabId));
            ViaFabricPlus.LOGGER.info("Created AuthChainData for Bedrock authentication!");
            return;
        }

        if (targetVersion.isOlderThan(VersionEnum.r1_19)) {
            return; // This disables the chat session emulation for all versions <= 1.18.2
        }
        if (targetVersion.isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            MinecraftClient.getInstance().getProfileKeys().fetchKeyPair().thenAcceptAsync(optional -> optional.ifPresentOrElse(playerKeyPair -> {
                final PlayerPublicKey.PublicKeyData publicKeyData = playerKeyPair.publicKey().data();

                userConnection.put(new ChatSession1_19_2(userConnection, new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), publicKeyData.keySignature()), playerKeyPair.privateKey()));

                if (targetVersion == VersionEnum.r1_19) {
                    final var legacyKey = ((IPublicKeyData) (Object) publicKeyData).viafabricplus_getV1Key();
                    if (legacyKey != null) {
                        userConnection.put(new ChatSession1_19_0(userConnection, new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), legacyKey.array()), playerKeyPair.privateKey()));
                    } else {
                        ViaFabricPlus.LOGGER.error("Failed to get v1 key, can't setup ChatSession_0");
                    }
                }
            }, () -> ViaFabricPlus.LOGGER.error("Failed to fetch keyPair, can't setup ChatSession")));
        }
    }
}
