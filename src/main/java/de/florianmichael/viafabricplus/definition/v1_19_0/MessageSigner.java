package de.florianmichael.viafabricplus.definition.v1_19_0;

import de.florianmichael.viafabricplus.definition.v1_19_0.model.SignatureUpdatableModel;

import java.security.*;

public interface MessageSigner {

    byte[] sign(final SignatureUpdatableModel signer);

    static MessageSigner create(final PrivateKey privateKey, final String algorithm) {
        return signer -> {
            try {
                final Signature signature = Signature.getInstance(algorithm);
                signature.initSign(privateKey);

                signer.update(data -> {
                    try {
                        signature.update(data);
                    } catch (SignatureException e) {
                        throw new RuntimeException(e);
                    }
                });
                return signature.sign();
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                throw new IllegalStateException("Failed to sign message", e);
            }
        };
    }
}
