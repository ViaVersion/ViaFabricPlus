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

package de.florianmichael.viafabricplus.definition.v1_19_0.storage;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.viafabricplus.definition.v1_19_0.JsonHelper;
import de.florianmichael.viafabricplus.definition.v1_19_0.MessageMetadataModel;
import net.lenni0451.mcstructs.text.components.StringComponent;
import net.lenni0451.mcstructs.text.serializer.TextComponentSerializer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.UUID;

public class ChatSession1_19_0 extends AbstractChatSession {

    private final byte[] legacyKey;

    public ChatSession1_19_0(UserConnection user, ProfileKey profileKey, PrivateKey privateKey, byte[] legacyKey) {
        super(user, profileKey, privateKey);
        this.legacyKey = legacyKey;
    }

    public byte[] sign(final UUID sender, final MessageMetadataModel messageMetadata) {
        return getSigner().sign(updater -> {
            final byte[] data = new byte[32];
            final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

            buffer.putLong(messageMetadata.salt());
            buffer.putLong(sender.getMostSignificantBits()).putLong(sender.getLeastSignificantBits());
            buffer.putLong(Instant.ofEpochMilli(messageMetadata.timestamp()).getEpochSecond());

            updater.update(data);
            updater.update(JsonHelper.toSortedString(TextComponentSerializer.V1_18.serializeJson(new StringComponent(messageMetadata.plain()))).getBytes(StandardCharsets.UTF_8));
        });
    }

    public byte[] getLegacyKey() {
        return legacyKey;
    }
}
