/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.viaversion.viafabricplus.injection.access.base.IConnection;
import com.viaversion.viafabricplus.injection.access.base.ILocalSampleLogger;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.platform.ViaChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.InetSocketAddress;
import javax.crypto.Cipher;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.Connection;
import net.minecraft.network.HandlerNames;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.netty.PreNettyLengthPrepender;
import net.raphimc.vialegacy.netty.PreNettyLengthRemover;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Connection.class)
public abstract class MixinConnection extends SimpleChannelInboundHandler<Packet<?>> implements IConnection {

    @Shadow
    public Channel channel;

    @Shadow
    private boolean encrypted;

    @Shadow
    public abstract void channelActive(@NotNull ChannelHandlerContext context) throws Exception;

    @Unique
    private UserConnection viaFabricPlus$userConnection;

    @Unique
    private ProtocolVersion viaFabricPlus$serverVersion;

    @Unique
    private Cipher viaFabricPlus$decryptionCipher;

    @Inject(method = "setupCompression", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        // Compression enabled and elements put into pipeline, move via handlers
        ViaChannelInitializer.reorderPipeline(channel.pipeline(), HandlerNames.ENCODER, HandlerNames.DECODER);
    }

    @Inject(method = "setEncryptionKey", at = @At("HEAD"), cancellable = true)
    private void storeDecryptionCipher(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (this.viaFabricPlus$serverVersion != null /* This happens when opening a lan server and people are joining */ && this.viaFabricPlus$serverVersion.olderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            // Minecraft's encryption code is bad for us, we need to reorder the pipeline
            ci.cancel();

            // Minecraft 1.6.4 supports split encryption/decryption which means the server can only enable one side of the encryption
            // So we only enable the encryption side and later enable the decryption side if the 1.7 -> 1.6 protocol
            // tells us to do, therefore, we need to store the cipher instance.
            this.viaFabricPlus$decryptionCipher = decryptionCipher;

            // Enabling the encryption side
            if (encryptionCipher == null) {
                throw new IllegalStateException("Encryption cipher is null");
            }

            this.encrypted = true;
            this.channel.pipeline().addBefore(PreNettyLengthRemover.NAME, HandlerNames.ENCRYPT, new CipherEncoder(encryptionCipher));
        }
    }

    @Inject(method = "connectToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;Lnet/minecraft/server/network/EventLoopGroupHolder;Lnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;"))
    private static void setTargetVersion(InetSocketAddress inetSocketAddress, EventLoopGroupHolder eventLoopGroupHolder, LocalSampleLogger localSampleLogger, CallbackInfoReturnable<Connection> cir, @Local Connection clientConnection) {
        // Set the target version stored in the PerformanceLog field to the ClientConnection instance
        if (localSampleLogger instanceof ILocalSampleLogger mixinMultiValueDebugSampleLogImpl && mixinMultiValueDebugSampleLogImpl.viaFabricPlus$getForcedVersion() != null) {
            ((IConnection) clientConnection).viaFabricPlus$setTargetVersion(mixinMultiValueDebugSampleLogImpl.viaFabricPlus$getForcedVersion());
        }
    }

    @WrapWithCondition(method = "connectToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setBandwidthLogger(Lnet/minecraft/util/debugchart/LocalSampleLogger;)V"))
    private static boolean dontSetPerformanceLog(Connection instance, LocalSampleLogger packetSizeLog) {
        // We need to restore vanilla behavior since we use the PerformanceLog as a way to store the target version
        return !(packetSizeLog instanceof ILocalSampleLogger mixinMultiValueDebugSampleLogImpl) || mixinMultiValueDebugSampleLogImpl.viaFabricPlus$getForcedVersion() == null;
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void setTargetVersion(InetSocketAddress inetSocketAddress, EventLoopGroupHolder eventLoopGroupHolder, Connection connection, CallbackInfoReturnable<ChannelFuture> cir, @Local(argsOnly = true) LocalRef<EventLoopGroupHolder> eventLoopGroupHolderRef) {
        ProtocolVersion targetVersion = ((IConnection) connection).viaFabricPlus$getTargetVersion();
        if (targetVersion == null) { // No server specific override
            targetVersion = ProtocolTranslator.getTargetVersion();
        }
        if (targetVersion == ProtocolTranslator.AUTO_DETECT_PROTOCOL) { // Auto-detect enabled (when pinging always use native version). Auto-detect is resolved in ConnectScreen mixin
            targetVersion = ProtocolTranslator.NATIVE_VERSION;
        }
        ((IConnection) connection).viaFabricPlus$setTargetVersion(targetVersion);
    }

    @Override
    public void viaFabricPlus$setupPreNettyDecryption() {
        if (this.viaFabricPlus$decryptionCipher == null) {
            throw new IllegalStateException("Decryption cipher is null");
        }

        this.encrypted = true;
        // Enabling the decryption side for 1.6.4 if the 1.7 -> 1.6 protocol tells us to do
        this.channel.pipeline().addBefore(PreNettyLengthPrepender.NAME, HandlerNames.DECRYPT, new CipherDecoder(this.viaFabricPlus$decryptionCipher));
    }

    @Override
    public UserConnection viaFabricPlus$getUserConnection() {
        return this.viaFabricPlus$userConnection;
    }

    @Override
    public void viaFabricPlus$setUserConnection(UserConnection connection) {
        this.viaFabricPlus$userConnection = connection;
    }

    @Override
    public ProtocolVersion viaFabricPlus$getTargetVersion() {
        return this.viaFabricPlus$serverVersion;
    }

    @Override
    public void viaFabricPlus$setTargetVersion(final ProtocolVersion serverVersion) {
        this.viaFabricPlus$serverVersion = serverVersion;
    }

}
