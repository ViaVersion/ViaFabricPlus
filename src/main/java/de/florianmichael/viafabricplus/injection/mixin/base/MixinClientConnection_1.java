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
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.platform.PreNettyConstants;
import de.florianmichael.viafabricplus.platform.VFPPreNettyDecoder;
import de.florianmichael.viafabricplus.platform.VFPPreNettyEncoder;
import de.florianmichael.viafabricplus.platform.VFPVLBViaDecodeHandler;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.VLBViaEncodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnection_1 {

    @Final
    @Shadow
    ClientConnection field_11663;

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hackNettyPipeline(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            final UserConnection user = new UserConnectionImpl(channel, true);
            channel.attr(ViaFabricPlus.LOCAL_VIA_CONNECTION).set(user);
            channel.attr(ViaFabricPlus.LOCAL_MINECRAFT_CONNECTION).set(field_11663);

            new ProtocolPipelineImpl(user);

            channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new VLBViaEncodeHandler(user));
            channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new VFPVLBViaDecodeHandler(user));

            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

                channel.pipeline().addBefore("prepender", PreNettyConstants.HANDLER_ENCODER_NAME, new VFPPreNettyEncoder(user));
                channel.pipeline().addBefore("splitter", PreNettyConstants.HANDLER_DECODER_NAME, new VFPPreNettyDecoder(user));
            }
        }
    }
}
