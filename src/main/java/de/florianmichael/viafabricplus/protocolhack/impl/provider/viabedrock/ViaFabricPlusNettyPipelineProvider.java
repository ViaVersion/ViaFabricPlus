/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.impl.provider.viabedrock;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.protocolhack.netty.ViaFabricPlusVLLegacyPipeline;
import io.netty.channel.Channel;
import net.raphimc.viabedrock.netty.AesEncryption;
import net.raphimc.viabedrock.netty.SnappyCompression;
import net.raphimc.viabedrock.netty.ZLibCompression;
import net.raphimc.viabedrock.protocol.providers.NettyPipelineProvider;
import net.raphimc.vialoader.netty.VLPipeline;

import javax.crypto.SecretKey;

public class ViaFabricPlusNettyPipelineProvider extends NettyPipelineProvider {

    @Override
    public void enableCompression(UserConnection user, int threshold, int algorithm) {
        final Channel channel = user.getChannel();

        if (channel.pipeline().names().contains(ViaFabricPlusVLLegacyPipeline.VIABEDROCK_COMPRESSION_HANDLER_NAME)) {
            throw new IllegalStateException("Compression already enabled");
        }

        switch (algorithm) {
            case 0 -> channel.pipeline().addBefore("splitter", ViaFabricPlusVLLegacyPipeline.VIABEDROCK_COMPRESSION_HANDLER_NAME, new ZLibCompression());
            case 1 -> channel.pipeline().addBefore("splitter", ViaFabricPlusVLLegacyPipeline.VIABEDROCK_COMPRESSION_HANDLER_NAME, new SnappyCompression());
            default -> throw new IllegalStateException("Invalid compression algorithm: " + algorithm);
        }
    }

    @Override
    public void enableEncryption(UserConnection user, SecretKey key) {
        final Channel channel = user.getChannel();

        if (channel.pipeline().names().contains(ViaFabricPlusVLLegacyPipeline.VIABEDROCK_ENCRYPTION_HANDLER_NAME)) {
            throw new IllegalStateException("Encryption already enabled");
        }

        try {
            channel.pipeline().addAfter(VLPipeline.VIABEDROCK_FRAME_ENCAPSULATION_HANDLER_NAME, ViaFabricPlusVLLegacyPipeline.VIABEDROCK_ENCRYPTION_HANDLER_NAME, new AesEncryption(key));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
