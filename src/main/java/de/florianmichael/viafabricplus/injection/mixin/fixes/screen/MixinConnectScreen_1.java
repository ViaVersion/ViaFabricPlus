/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import de.florianmichael.viafabricplus.definition.v1_19_0.storage.ChatSession1_19_0;
import de.florianmichael.viafabricplus.definition.v1_19_2.storage.ChatSession1_19_2;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
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

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;", ordinal = 1))
    public String replaceAddress(InetSocketAddress instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_17)) return field_33737.getAddress();

        return instance.getHostString();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I"))
    public int replacePort(InetSocketAddress instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_17)) return field_33737.getPort();

        return instance.getPort();
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V", ordinal = 1, shift = At.Shift.BEFORE))
    public void setupConnectionSessions(CallbackInfo ci) {
        final UserConnection userConnection = field_2416.connection.channel.attr(ViaFabricPlus.LOCAL_USER_CONNECTION).get();

        if (userConnection == null) {
            ViaLoadingBase.LOGGER.log(Level.WARNING, "ViaVersion userConnection is null");
            return;
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThan(ProtocolVersion.v1_19)) {
            return; // This disables the chat session emulation for all versions <= 1.18.2
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            try {
                final PlayerKeyPair playerKeyPair = MinecraftClient.getInstance().getProfileKeys().fetchKeyPair().get().orElse(null);
                if (playerKeyPair != null) {
                    final PlayerPublicKey.PublicKeyData publicKeyData = playerKeyPair.publicKey().data();
                    final ProfileKey profileKey = new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), publicKeyData.keySignature());

                    userConnection.put(new ChatSession1_19_2(userConnection, profileKey, playerKeyPair.privateKey()));

                    if (ViaLoadingBase.getTargetVersion() == ProtocolVersion.v1_19) {
                        final byte[] legacyKey = ((IPublicKeyData) (Object) publicKeyData).protocolhack_get1_19_0Key().array();
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
