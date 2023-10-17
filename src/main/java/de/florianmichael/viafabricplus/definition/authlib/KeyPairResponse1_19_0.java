package de.florianmichael.viafabricplus.definition.authlib;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import java.nio.ByteBuffer;
/*
This library is part of the AuthLib, we are overwriting this class to add a new field.
 */

public record KeyPairResponse1_19_0() {}
 /*       KeyPairResponse.KeyPair keyPair,
        ByteBuffer publicKeySignatureV2,
        ByteBuffer publicKeySignature // removed in 1.20-rc1 ,
        String expiresAt,
        String refreshedAfter
}*/