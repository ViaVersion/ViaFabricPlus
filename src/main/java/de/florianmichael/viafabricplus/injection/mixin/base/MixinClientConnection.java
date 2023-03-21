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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.protocolhack.constants.PreNettyConstants;
import de.florianmichael.viafabricplus.protocolhack.constants.BedrockRakNetConstants;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.DisconnectHandler;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.PingEncapsulationCodec;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.RakMessageEncapsulationCodec;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPingEncoder;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPongDecoder;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyDecoder;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyEncoder;
import de.florianmichael.viafabricplus.protocolhack.replacement.ViaFabricPlusVLBViaDecodeHandler;
import de.florianmichael.vialoadingbase.event.PipelineReorderEvent;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import de.florianmichael.vialoadingbase.netty.VLBViaEncodeHandler;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Lazy;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.*;
import net.raphimc.viabedrock.protocol.BedrockBaseProtocol;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPingEncoder;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPongDecoder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection extends SimpleChannelInboundHandler<Packet<?>> implements IClientConnection {

    @Shadow private Channel channel;

    @Shadow private boolean encrypted;

    @Shadow public abstract void channelActive(ChannelHandlerContext context) throws Exception;

    @Shadow @Final public static Lazy<EpollEventLoopGroup> EPOLL_CLIENT_IO_GROUP;
    @Shadow @Final public static Lazy<NioEventLoopGroup> CLIENT_IO_GROUP;

    @Unique
    private Cipher viafabricplus_decryptionCipher;

    @Unique
    private Cipher viafabricplus_encryptionCipher;

    @Unique
    private boolean viafabricplus_compressionEnabled = false;

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

    /**
     * @author Mojang, FlorianMichael as EnZaXD
     * @reason Hack Netty Pipeline (do cursed shit)
     */
    @Overwrite
    public static ClientConnection connect(InetSocketAddress address, boolean useEpoll) {
        final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);

        final Class<? extends AbstractChannel> channelType = Epoll.isAvailable() && useEpoll ? EpollSocketChannel.class : NioSocketChannel.class;
        final Lazy<? extends MultithreadEventLoopGroup> lazy = Epoll.isAvailable() && useEpoll ? EPOLL_CLIENT_IO_GROUP : CLIENT_IO_GROUP;

        final boolean rakNet = ProtocolHack.getForcedVersions().containsKey(address) ?
                (ProtocolHack.getForcedVersions().get(address).getVersion() == BedrockProtocolVersion.bedrockLatest.getVersion()) :
                ProtocolHack.getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest);

        Bootstrap nettyBoostrap = new Bootstrap();
        nettyBoostrap = nettyBoostrap.group(lazy.get());
        nettyBoostrap = nettyBoostrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel channel) throws Exception {
                try {
                    if (rakNet) {
                        channel.config().setOption(RakChannelOption.RAK_PROTOCOL_VERSION, 11);
                        channel.config().setOption(RakChannelOption.RAK_CONNECT_TIMEOUT, 4_000L);
                        channel.config().setOption(RakChannelOption.RAK_SESSION_TIMEOUT, 30_000L);
                        channel.config().setOption(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong());
                    } else {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    }
                } catch (Exception ignored) {
                }
                ChannelPipeline channelPipeline = channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
                ClientConnection.addHandlers(channelPipeline, NetworkSide.CLIENTBOUND);

                channelPipeline.addLast("packet_handler", clientConnection);

                if (channel instanceof SocketChannel || rakNet) {
                    if (ProtocolHack.getForcedVersions().containsKey(address)) {
                        channel.attr(ProtocolHack.FORCED_VERSION).set(ProtocolHack.getForcedVersions().get(address));
                        ProtocolHack.getForcedVersions().remove(address);
                    }

                    final UserConnection user = new UserConnectionImpl(channel, true);
                    channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).set(user);
                    channel.attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).set(clientConnection);

                    new ProtocolPipelineImpl(user);

                    channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new VLBViaEncodeHandler(user));
                    channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new ViaFabricPlusVLBViaDecodeHandler(user));

                    if (rakNet) {
                        user.getProtocolInfo().getPipeline().add(BedrockBaseProtocol.INSTANCE);

                        channel.pipeline().replace("splitter", BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, new BatchLengthCodec());

                        channel.pipeline().addBefore(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.DISCONNECT_HANDLER_NAME, new DisconnectHandler());
                        channel.pipeline().addAfter(BedrockRakNetConstants.DISCONNECT_HANDLER_NAME, BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, new RakMessageEncapsulationCodec());
                        channel.pipeline().addAfter(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.PACKET_ENCAPSULATION_HANDLER_NAME, new PacketEncapsulationCodec());

                        channel.pipeline().remove("prepender");
                        channel.pipeline().remove("timeout");

                        // Pinging in RakNet is something different
                        if (ProtocolHack.getRakNetPingSessions().contains(address)) {
                            ProtocolHack.getRakNetPingSessions().remove(address);
                            { // Temporary fix for the ping encoder
                                final RakClientChannel rakChannel = (RakClientChannel) channel;

                                rakChannel.parent().pipeline().replace(UnconnectedPingEncoder.NAME, UnconnectedPingEncoder.NAME, new FixedUnconnectedPingEncoder(rakChannel));
                                rakChannel.parent().pipeline().replace(UnconnectedPongDecoder.NAME, UnconnectedPongDecoder.NAME, new FixedUnconnectedPongDecoder(rakChannel));
                            }

                            channel.pipeline().replace(BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, BedrockRakNetConstants.PING_ENCAPSULATION_HANDLER_NAME, new PingEncapsulationCodec(address));

                            channel.pipeline().remove(BedrockRakNetConstants.PACKET_ENCAPSULATION_HANDLER_NAME);
                            channel.pipeline().remove(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME);
                        }
                    }

                    if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                        user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

                        channel.pipeline().addBefore("prepender", PreNettyConstants.HANDLER_ENCODER_NAME, new VFPPreNettyEncoder(user));
                        channel.pipeline().addBefore("splitter", PreNettyConstants.HANDLER_DECODER_NAME, new VFPPreNettyDecoder(user));
                    }
                }
            }
        });
        if (rakNet) {
            nettyBoostrap = nettyBoostrap.channelFactory(channelType == EpollSocketChannel.class ? RakChannelFactory.client(EpollDatagramChannel.class) : RakChannelFactory.client(NioDatagramChannel.class));
        } else {
            nettyBoostrap = nettyBoostrap.channel(channelType);
        }

        if (ProtocolHack.getRakNetPingSessions().contains(address)) {
            nettyBoostrap.bind(new InetSocketAddress(0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE).syncUninterruptibly();
        } else {
            nettyBoostrap.connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        }
        return clientConnection;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        channelActive(ctx);
    }

    @Override
    public void viafabricplus_setupPreNettyEncryption() {
        this.encrypted = true;
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_DECODER_NAME, "decrypt", new PacketDecryptor(this.viafabricplus_decryptionCipher));
        this.channel.pipeline().addBefore(PreNettyConstants.HANDLER_ENCODER_NAME, "encrypt", new PacketEncryptor(this.viafabricplus_encryptionCipher));
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
