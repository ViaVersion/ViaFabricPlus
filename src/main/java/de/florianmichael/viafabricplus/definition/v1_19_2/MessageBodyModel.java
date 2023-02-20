/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
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
