package de.florianmichael.viafabricplus.definition.authlib;

import java.nio.ByteBuffer;

/*
This library is part of the AuthLib, we are overwriting this class to add a new field.
 */

public record KeyPairResponseBypass(KeyPair keyPair, ByteBuffer publicKeySignatureV2, ByteBuffer publicKeySignature /* own field */, String expiresAt, String refreshedAfter) {

    public record KeyPair(String privateKey, String publicKey) {
    }
}
