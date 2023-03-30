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
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.definition.bedrock.BedrockAccountHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import de.florianmichael.viafabricplus.definition.v1_19_0.storage.ChatSession1_19_0;
import de.florianmichael.viafabricplus.definition.v1_19_2.storage.ChatSession1_19_2;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.settings.groups.MPPassSettings;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.model.ComparableProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.raphimc.mcauth.step.bedrock.StepMCChain;
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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Final
    @Shadow
    ServerAddress field_33737;

    @Final
    @Shadow
    ConnectScreen field_2416;

    @Final
    @Shadow
    ServerInfo field_40415;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;", ordinal = 0))
    public String replaceAddress(InetSocketAddress instance) {
        if (ProtocolHack.isOlderThanOrEqualToOrForced(instance, ProtocolVersion.v1_17) || ProtocolHack.isEqualToOrForced(instance, BedrockProtocolVersion.bedrockLatest)) {
            return field_33737.getAddress();
        }
        return instance.getHostName();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I"))
    public int replacePort(InetSocketAddress instance) {
        if (ProtocolHack.isOlderThanOrEqualToOrForced(instance, ProtocolVersion.v1_17) || ProtocolHack.isEqualToOrForced(instance, BedrockProtocolVersion.bedrockLatest)) {
            return field_33737.getPort();
        }
        return instance.getPort();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1))
    public void spoofUserName(ClientConnection instance, Packet<?> packet) {
        if (MPPassSettings.INSTANCE.spoofUserNameIfUsingClassiCube.getValue() && ViaFabricPlusClassicMPPassProvider.classiCubeMPPass != null && ClassiCubeAccountHandler.INSTANCE.getAccount() != null) {
            instance.send(new LoginHelloC2SPacket(ClassiCubeAccountHandler.INSTANCE.getAccount().username, Optional.ofNullable(MinecraftClient.getInstance().getSession().getUuidOrNull())));
            return;
        }

        instance.send(packet);
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public Object mapSocketAddress(Optional<InetSocketAddress> instance) {
        final InetSocketAddress address = instance.get();
        final ComparableProtocolVersion forcedVersion = ((IServerInfo) field_40415).viafabricplus_forcedVersion();
        if (forcedVersion != null) {
            ProtocolHack.getForcedVersions().put(address, forcedVersion);
            ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(forcedVersion);
        }
        return address;
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1, shift = At.Shift.BEFORE))
    public void setupConnectionSessions(CallbackInfo ci) {
        final ClientConnection connection = field_2416.connection;
        if (connection == null || connection.channel == null) return;
        final UserConnection userConnection = connection.channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get();

        if (userConnection == null) {
            ViaLoadingBase.LOGGER.log(Level.WARNING, "ViaVersion userConnection is null");
            return;
        }
        if (ProtocolHack.getTargetVersion(connection.channel).isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            final StepMCChain.MCChain account = BedrockAccountHandler.INSTANCE.getAccount();

            if (account != null) {
                userConnection.put(new AuthChainData(userConnection, account.mojangJwt(), account.identityJwt(), account.publicKey(), account.privateKey()));
            }
            return;
        }

        if (ProtocolHack.getTargetVersion().isOlderThan(ProtocolVersion.v1_19)) {
            return; // This disables the chat session emulation for all versions <= 1.18.2
        }
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            try {
                final PlayerKeyPair playerKeyPair = MinecraftClient.getInstance().getProfileKeys().fetchKeyPair().get().orElse(null);
                if (playerKeyPair != null) {
                    final PlayerPublicKey.PublicKeyData publicKeyData = playerKeyPair.publicKey().data();
                    final ProfileKey profileKey = new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), publicKeyData.keySignature());

                    userConnection.put(new ChatSession1_19_2(userConnection, profileKey, playerKeyPair.privateKey()));

                    if (ProtocolHack.getTargetVersion().isEqualTo(ProtocolVersion.v1_19)) {
                        final byte[] legacyKey = ((IPublicKeyData) (Object) publicKeyData).viafabricplus_getV1Key().array();
                        if (legacyKey != null) {
                            userConnection.put(new ChatSession1_19_0(userConnection, profileKey, playerKeyPair.privateKey(), legacyKey));
                        } else {
                            ViaLoadingBase.LOGGER.log(Level.WARNING, "Mojang removed the legacy key");
                        }
                    }
                } else {
                    ViaLoadingBase.LOGGER.log(Level.WARNING, "Failed to fetch the key pair");
                }
            } catch (InterruptedException | ExecutionException e) {
                ViaLoadingBase.LOGGER.log(Level.WARNING, "Failed to fetch the key pair");
            }
        }
    }
}
