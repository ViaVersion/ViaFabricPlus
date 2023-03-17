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
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.constants.BedrockRakNetConstants;
import de.florianmichael.viafabricplus.protocolhack.constants.PreNettyConstants;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyDecoder;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyEncoder;
import de.florianmichael.viafabricplus.protocolhack.replacement.VFPVLBViaDecodeHandler;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.*;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPingEncoder;
import de.florianmichael.viafabricplus.protocolhack.platform.viabedrock.library_fix.FixedUnconnectedPongDecoder;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.VLBViaEncodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.netty.BatchLengthCodec;
import net.raphimc.viabedrock.netty.PacketEncapsulationCodec;
import net.raphimc.viabedrock.protocol.BedrockBaseProtocol;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
import org.cloudburstmc.netty.channel.raknet.RakClientChannel;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPingEncoder;
import org.cloudburstmc.netty.handler.codec.raknet.common.UnconnectedPongDecoder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnection_1 {

    @Final
    @Shadow
    ClientConnection field_11663;

    @Redirect(method = "initChannel", at = @At(value = "INVOKE", target = "Lio/netty/channel/ChannelConfig;setOption(Lio/netty/channel/ChannelOption;Ljava/lang/Object;)Z"))
    public boolean applyRakNetOptions(ChannelConfig instance, ChannelOption<Object> tChannelOption, Object t) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            instance.setOption(RakChannelOption.RAK_PROTOCOL_VERSION, 11);
            instance.setOption(RakChannelOption.RAK_CONNECT_TIMEOUT, 4_000L);
            instance.setOption(RakChannelOption.RAK_SESSION_TIMEOUT, 30_000L);
            return instance.setOption(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong());
        }
        return instance.setOption(tChannelOption, t);
    }

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hackNettyPipeline(Channel channel, CallbackInfo ci) {
        final boolean rakNet = ViaLoadingBase.getClassWrapper().getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest);

        if (channel instanceof SocketChannel || rakNet) {
            final UserConnection user = new UserConnectionImpl(channel, true);
            channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).set(user);
            channel.attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).set(field_11663);

            new ProtocolPipelineImpl(user);

            channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new VLBViaEncodeHandler(user));
            channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new VFPVLBViaDecodeHandler(user));

            if (rakNet) {
                user.getProtocolInfo().getPipeline().add(BedrockBaseProtocol.INSTANCE);

                channel.pipeline().replace("splitter", BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, new BatchLengthCodec());

                channel.pipeline().addBefore(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.DISCONNECT_HANDLER_NAME, new DisconnectHandler());
                channel.pipeline().addAfter(BedrockRakNetConstants.DISCONNECT_HANDLER_NAME, BedrockRakNetConstants.FRAME_ENCAPSULATION_HANDLER_NAME, new RakMessageEncapsulationCodec());
                channel.pipeline().addAfter(BedrockRakNetConstants.BATCH_LENGTH_HANDLER_NAME, BedrockRakNetConstants.PACKET_ENCAPSULATION_HANDLER_NAME, new PacketEncapsulationCodec());

                channel.pipeline().remove("prepender");
                channel.pipeline().remove("timeout");

                final InetSocketAddress address = ((IClientConnection) field_11663).viafabricplus_capturedAddress();

                // Pinging in RakNet is something different
                if (RakNetPingSessions.isPingSession(address.getAddress())) {
                    RakNetPingSessions.pingSessions.remove(address.getAddress());
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

            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

                channel.pipeline().addBefore("prepender", PreNettyConstants.HANDLER_ENCODER_NAME, new VFPPreNettyEncoder(user));
                channel.pipeline().addBefore("splitter", PreNettyConstants.HANDLER_DECODER_NAME, new VFPPreNettyDecoder(user));
            }
        }
    }
}
