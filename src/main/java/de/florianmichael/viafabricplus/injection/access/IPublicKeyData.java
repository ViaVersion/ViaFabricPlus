package de.florianmichael.viafabricplus.injection.access;

import java.nio.ByteBuffer;

public interface IPublicKeyData {

    ByteBuffer protocolhack_get1_19_0Key();

    void protocolhack_set1_19_0Key(final ByteBuffer oldKey);
}
