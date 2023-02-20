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
