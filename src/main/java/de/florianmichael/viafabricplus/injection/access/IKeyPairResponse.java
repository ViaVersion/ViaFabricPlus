package de.florianmichael.viafabricplus.injection.access;

import java.nio.ByteBuffer;

public interface IKeyPairResponse {

    ByteBuffer viafabricplus$getLegacyPublicKeySignature();

    void viafabricplus$setLegacyPublicKeySignature(final ByteBuffer signature);
}
