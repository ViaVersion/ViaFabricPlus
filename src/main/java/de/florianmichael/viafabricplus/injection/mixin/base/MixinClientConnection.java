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
package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.event.DisconnectConnectionCallback;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.protocolhack.constants.PreNettyConstants;
import de.florianmichael.viafabricplus.protocolhack.constants.BedrockRakNetConstants;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.PipelineInjector;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.event.PipelineReorderEvent;
import io.netty.channel.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.util.Lazy;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.*;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection extends SimpleChannelInboundHandler<Packet<?>> implements IClientConnection {

    @Shadow private Channel channel;

    @Shadow private boolean encrypted;

    @Shadow public abstract void channelActive(ChannelHandlerContext context) throws Exception;

    @Unique
    private Cipher viafabricplus_decryptionCipher;

    @Unique
    private Cipher viafabricplus_encryptionCipher;

    @Unique
    private boolean viafabricplus_compressionEnabled = false;

    @Unique
    private InetSocketAddress viafabricplus_capturedAddress;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    private void storeEncryptionCiphers(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            ci.cancel();
            this.viafabricplus_decryptionCipher = decryptionCipher;
            this.viafabricplus_encryptionCipher = encryptionCipher;
        }
    }

    @Inject(method = "connect", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Lazy;get()Ljava/lang/Object;", shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void captureAddress(InetSocketAddress address, boolean useEpoll, CallbackInfoReturnable<ClientConnection> cir, final ClientConnection clientConnection, Class class_, Lazy lazy) {
        ((IClientConnection) clientConnection).viafabricplus_captureAddress(address);

        if (ProtocolHack.getForcedVersions().containsKey(address) ? (ProtocolHack.getForcedVersions().get(address).getVersion() == BedrockProtocolVersion.bedrockLatest.getVersion()) : ProtocolHack.getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            PipelineInjector.connectRakNet(clientConnection, address, lazy, class_);
            cir.setReturnValue(clientConnection);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        if (ProtocolHack.getRakNetPingSessions().contains(viafabricplus_capturedAddress)) {
            channelActive(ctx);
            ProtocolHack.getRakNetPingSessions().remove(viafabricplus_capturedAddress);
        }
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    public void resetStorages(Text disconnectReason, CallbackInfo ci) {
        ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(ViaLoadingBase.getInstance().getTargetVersion());
        DisconnectConnectionCallback.EVENT.invoker().onDisconnect();
    }

    @Override
    public void viafabricplus_setupPreNettyEncryption() {
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_DECODER_NAME, "decrypt", new PacketDecryptor(this.viafabricplus_decryptionCipher));
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_ENCODER_NAME, "encrypt", new PacketEncryptor(this.viafabricplus_encryptionCipher));
    }

    @Override
    public InetSocketAddress viafabricplus_capturedAddress() {
        return viafabricplus_capturedAddress;
    }

    @Override
    public void viafabricplus_captureAddress(InetSocketAddress socketAddress) {
        viafabricplus_capturedAddress = socketAddress;
    }

    @Override
    public void viafabricplus_enableZLibCompression() {
        if (this.viafabricplus_compressionEnabled) throw new IllegalStateException("Compression is already enabled");
        this.viafabricplus_compressionEnabled = true;

        this.channel.pipeline().addBefore(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.COMPRESSION_HANDLER_NAME, new ZLibCompression());
    }

    @Override
    public void viafabricplus_enableSnappyCompression() {
        if (this.viafabricplus_compressionEnabled) throw new IllegalStateException("Compression is already enabled");
        this.viafabricplus_compressionEnabled = true;

        this.channel.pipeline().addBefore(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.COMPRESSION_HANDLER_NAME, new SnappyCompression());
    }

    @Override
    public void viafabricplus_enableAesGcmEncryption(final SecretKey secretKey) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (this.encrypted) throw new IllegalStateException("Encryption is already enabled");
        this.encrypted = true;

        this.channel.pipeline().addAfter(BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, BedrockRakNetConstants.ENCRYPTION_HANDLER_NAME, new AesEncryption(secretKey));
    }
}
