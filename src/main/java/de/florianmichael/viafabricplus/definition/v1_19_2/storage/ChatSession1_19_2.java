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

package de.florianmichael.viafabricplus.definition.v1_19_2.storage;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.viafabricplus.definition.v1_19_0.MessageMetadataModel;
import de.florianmichael.viafabricplus.definition.v1_19_0.storage.AbstractChatSession;
import de.florianmichael.viafabricplus.definition.v1_19_2.MessageBodyModel;
import de.florianmichael.viafabricplus.definition.v1_19_2.MessageHeaderModel;

import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.UUID;

public class ChatSession1_19_2 extends AbstractChatSession {
    public final static SecureRandom SECURE_RANDOM = new SecureRandom();

    private byte[] precedingSignature = null;

    public ChatSession1_19_2(UserConnection user, ProfileKey profileKey, PrivateKey privateKey) {
        super(user, profileKey, privateKey);
    }

    public byte[] sign(final UUID sender, final MessageMetadataModel messageMetadata, final PlayerMessageSignature[] lastSeenMessages) {
        final MessageHeaderModel header = new MessageHeaderModel(sender, precedingSignature);
        final MessageBodyModel body = new MessageBodyModel(messageMetadata, lastSeenMessages);

        precedingSignature = getSigner().sign(updater -> header.updater(body.digestBytes(), updater));

        return precedingSignature;
    }
}
