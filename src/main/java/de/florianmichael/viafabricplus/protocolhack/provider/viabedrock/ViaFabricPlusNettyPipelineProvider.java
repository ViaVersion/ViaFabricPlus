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
package de.florianmichael.viafabricplus.protocolhack.provider.viabedrock;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.injection.access.IClientConnection;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.raphimc.viabedrock.protocol.providers.NettyPipelineProvider;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ViaFabricPlusNettyPipelineProvider extends NettyPipelineProvider {

    @Override
    public void enableCompression(UserConnection user, int threshold, int algorithm) {
        final IClientConnection currentConnection = (IClientConnection) user.getChannel().attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).get();

        try {
            switch (algorithm) {
                case 0 -> currentConnection.viafabricplus_enableZLibCompression();
                case 1 -> currentConnection.viafabricplus_enableSnappyCompression();

                default -> throw new IllegalStateException("Invalid compression algorithm: " + algorithm);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enableEncryption(UserConnection user, SecretKey key) {
        final IClientConnection currentConnection = (IClientConnection) user.getChannel().attr(ProtocolHack.LOCAL_MINECRAFT_CONNECTION).get();

        try {
            currentConnection.viafabricplus_enableAesGcmEncryption(key);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
