/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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

import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.protocolhack.constants.PreNettyConstants;
import de.florianmichael.viafabricplus.protocolhack.constants.BedrockRakNetConstants;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.RakNetPingSessions;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.event.PipelineReorderEvent;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.AesGcmEncryption;
import net.raphimc.viabedrock.netty.SnappyCompression;
import net.raphimc.viabedrock.netty.ZLibCompression;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.net.InetAddress;
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
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            ci.cancel();
            this.viafabricplus_decryptionCipher = decryptionCipher;
            this.viafabricplus_encryptionCipher = encryptionCipher;
        }
    }

    @Inject(method = "connect", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Lazy;get()Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void captureAddress(InetSocketAddress address, boolean useEpoll, CallbackInfoReturnable<ClientConnection> cir, final ClientConnection clientConnection) {
        ((IClientConnection) clientConnection).viafabricplus_captureAddress(address);
    }

    @Redirect(method = "connect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;channel(Ljava/lang/Class;)Lio/netty/bootstrap/AbstractBootstrap;"))
    private static AbstractBootstrap applyRakNetChannelFactory(Bootstrap instance, Class aClass) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            return instance.channelFactory(aClass == EpollSocketChannel.class ? RakChannelFactory.client(EpollDatagramChannel.class) : RakChannelFactory.client(NioDatagramChannel.class));
        }
        return instance.channel(aClass);
    }

    @Redirect(method = "connect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;connect(Ljava/net/InetAddress;I)Lio/netty/channel/ChannelFuture;"))
    private static ChannelFuture applyRakNetPing(Bootstrap instance, InetAddress inetHost, int inetPort) {
        if (RakNetPingSessions.isPingSession(inetHost)) {
            return instance.bind(new InetSocketAddress(0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        return instance.connect(inetHost, inetPort);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            channelActive(ctx);
        }
    }

    @Override
    public void viafabricplus_setupPreNettyEncryption() {
        this.encrypted = true;
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_DECODER_NAME, "decrypt", new PacketDecryptor(this.viafabricplus_decryptionCipher));
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_ENCODER_NAME, "encrypt", new PacketEncryptor(this.viafabricplus_encryptionCipher));
    }

    @Override
    public void viafabricplus_captureAddress(InetSocketAddress address) {
        this.viafabricplus_capturedAddress = address;
    }

    @Override
    public InetSocketAddress viafabricplus_capturedAddress() {
        return this.viafabricplus_capturedAddress;
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

        this.channel.pipeline().addAfter(BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, BedrockRakNetConstants.ENCRYPTION_HANDLER_NAME, new AesGcmEncryption(secretKey));
    }
}
