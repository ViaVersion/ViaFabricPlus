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
