package de.florianmichael.viafabricplus.definition.v1_19_2;

import de.florianmichael.viafabricplus.definition.v1_19_0.model.SignatureUpdaterModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public record MessageHeaderModel(UUID sender, byte[] precedingSignature) {

    public byte[] toByteArray(final UUID uuid) {
        final byte[] data = new byte[16];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return data;
    }

    public void updater(final byte[] bodyDigest, final SignatureUpdaterModel updater) {
        if (precedingSignature != null) {
            updater.update(precedingSignature);
        }

        updater.update(toByteArray(sender()));
        updater.update(bodyDigest);
    }
}
