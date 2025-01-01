/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.protocoltranslator.impl.provider.viabedrock;

import com.viaversion.vialoader.netty.VLPipeline;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viafabricplus.protocoltranslator.netty.ViaFabricPlusVLLegacyPipeline;
import io.netty.channel.Channel;
import net.raphimc.viabedrock.api.io.compression.ProtocolCompression;
import net.raphimc.viabedrock.netty.AesEncryptionCodec;
import net.raphimc.viabedrock.netty.CompressionCodec;
import net.raphimc.viabedrock.protocol.provider.NettyPipelineProvider;

import javax.crypto.SecretKey;

public final class ViaFabricPlusNettyPipelineProvider extends NettyPipelineProvider {

    @Override
    public void enableCompression(UserConnection user, ProtocolCompression protocolCompression) {
        final Channel channel = user.getChannel();

        if (channel.pipeline().names().contains(ViaFabricPlusVLLegacyPipeline.VIABEDROCK_COMPRESSION_HANDLER_NAME)) {
            throw new IllegalStateException("Compression already enabled");
        }

        channel.pipeline().addBefore("splitter", ViaFabricPlusVLLegacyPipeline.VIABEDROCK_COMPRESSION_HANDLER_NAME, new CompressionCodec(protocolCompression));
    }

    @Override
    public void enableEncryption(UserConnection user, SecretKey key) {
        final Channel channel = user.getChannel();

        if (channel.pipeline().names().contains(ViaFabricPlusVLLegacyPipeline.VIABEDROCK_ENCRYPTION_HANDLER_NAME)) {
            throw new IllegalStateException("Encryption already enabled");
        }

        try {
            channel.pipeline().addAfter(VLPipeline.VIABEDROCK_FRAME_ENCAPSULATION_HANDLER_NAME, ViaFabricPlusVLLegacyPipeline.VIABEDROCK_ENCRYPTION_HANDLER_NAME, new AesEncryptionCodec(key));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
