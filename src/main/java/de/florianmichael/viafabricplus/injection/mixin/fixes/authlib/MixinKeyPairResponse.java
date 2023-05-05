package de.florianmichael.viafabricplus.injection.mixin.fixes.authlib;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import de.florianmichael.viafabricplus.injection.access.IKeyPairResponse;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.ByteBuffer;

@Mixin(KeyPairResponse.class)
public class MixinKeyPairResponse implements IKeyPairResponse {

    @SerializedName("publicKeySignature")
    private ByteBuffer viafabricplus_legacyKeySignature;

    @Override
    public ByteBuffer viafabricplus_getLegacyPublicKeySignature() {
        return this.viafabricplus_legacyKeySignature;
    }
}
