package de.florianmichael.viafabricplus.injection.access;

import java.nio.ByteBuffer;

public interface IKeyPairResponse {

    ByteBuffer viafabricplus_getLegacyPublicKeySignature();

    void viafabricplus_setLegacyPublicKeySignature(final ByteBuffer signature);
}
