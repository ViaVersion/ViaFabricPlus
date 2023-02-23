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
