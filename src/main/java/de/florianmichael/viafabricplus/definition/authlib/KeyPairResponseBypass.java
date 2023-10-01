package de.florianmichael.viafabricplus.definition.authlib;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;

import java.nio.ByteBuffer;

/*
This library is part of the AuthLib, we are overwriting this class to add a new field.
 */

public record KeyPairResponseBypass(KeyPairResponse.KeyPair keyPair, ByteBuffer publicKeySignatureV2, ByteBuffer publicKeySignature /* own field */, String expiresAt, String refreshedAfter) {
}
