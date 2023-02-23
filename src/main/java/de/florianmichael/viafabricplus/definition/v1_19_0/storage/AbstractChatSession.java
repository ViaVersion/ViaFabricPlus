package de.florianmichael.viafabricplus.definition.v1_19_0.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import de.florianmichael.viafabricplus.definition.v1_19_0.MessageSigner;

import java.security.PrivateKey;

public abstract class AbstractChatSession extends StoredObject {
    private final ProfileKey profileKey;
    private final PrivateKey privateKey;

    private final MessageSigner signer;

    public AbstractChatSession(UserConnection user, final ProfileKey profileKey, final PrivateKey privateKey) {
        super(user);
        this.profileKey = profileKey;
        this.privateKey = privateKey;

        this.signer = MessageSigner.create(privateKey, "SHA256withRSA");
    }

    public ProfileKey getProfileKey() {
        return profileKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public MessageSigner getSigner() {
        return signer;
    }
}
