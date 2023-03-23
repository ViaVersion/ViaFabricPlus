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
package de.florianmichael.viafabricplus.injection.reference;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.constants.PreNettyConstants;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyDecoder;
import de.florianmichael.viafabricplus.protocolhack.platform.vialegacy.VFPPreNettyEncoder;
import de.florianmichael.viafabricplus.protocolhack.replacement.ViaFabricPlusVLBViaDecodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import de.florianmichael.vialoadingbase.netty.VLBViaEncodeHandler;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;

import java.net.InetSocketAddress;

public class ClientConnectionReference {

    public static void hackNettyPipeline(final ClientConnection connection, final Channel channel, final InetSocketAddress address) {
        if (ProtocolHack.getForcedVersions().containsKey(address)) {
            channel.attr(ProtocolHack.FORCED_VERSION).set(ProtocolHack.getForcedVersions().get(address));
            ProtocolHack.getForcedVersions().remove(address);
        }
        final UserConnection user = new UserConnectionImpl(channel, true);
        channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).set(user);
        channel.attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).set(connection);

        new ProtocolPipelineImpl(user);

        System.out.println("Hacking Netty Pipeline (ViaVersion)");

        channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new VLBViaEncodeHandler(user));
        channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new ViaFabricPlusVLBViaDecodeHandler(user));

        if (ProtocolHack.getTargetVersion(channel).isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);

            channel.pipeline().addBefore("prepender", PreNettyConstants.HANDLER_ENCODER_NAME, new VFPPreNettyEncoder(user));
            channel.pipeline().addBefore("splitter", PreNettyConstants.HANDLER_DECODER_NAME, new VFPPreNettyDecoder(user));
        }
    }
}
