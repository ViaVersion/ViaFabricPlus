/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.definition.v1_19_2;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import de.florianmichael.viafabricplus.definition.v1_19_0.MessageMetadataModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@SuppressWarnings("UnstableApiUsage")
public class MessageBodyModel {
    private final MessageMetadataModel messageMetadata;
    private final PlayerMessageSignature[] lastSeenMessages;

    public MessageBodyModel(MessageMetadataModel messageMetadata, PlayerMessageSignature[] lastSeenMessages) {
        this.messageMetadata = messageMetadata;
        this.lastSeenMessages = lastSeenMessages;
    }

    public void writeLastSeenMessage(final DataOutput dataOutput) {
        for (PlayerMessageSignature seenMessage : lastSeenMessages) {
            try {
                dataOutput.writeByte(70);
                dataOutput.writeLong(seenMessage.uuid().getMostSignificantBits());
                dataOutput.writeLong(seenMessage.uuid().getLeastSignificantBits());
                dataOutput.write(seenMessage.signatureBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public byte[] digestBytes() {
        final HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream());
        final DataOutputStream dataOutputStream = new DataOutputStream(hashingOutputStream);

        try {
            dataOutputStream.writeLong(messageMetadata.salt());
            dataOutputStream.writeLong(Instant.ofEpochMilli(messageMetadata.timestamp()).getEpochSecond());

            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(dataOutputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(messageMetadata.plain());
            outputStreamWriter.flush();

            dataOutputStream.write(70);
            writeLastSeenMessage(dataOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return hashingOutputStream.hash().asBytes();
    }
}
