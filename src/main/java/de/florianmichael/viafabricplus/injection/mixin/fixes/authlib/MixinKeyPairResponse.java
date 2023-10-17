package de.florianmichael.viafabricplus.injection.mixin.fixes.authlib;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import de.florianmichael.viafabricplus.injection.access.IKeyPairResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(value = KeyPairResponse.class, remap = false)
public class MixinKeyPairResponse implements IKeyPairResponse {

    @Unique
    private ByteBuffer viafabricplus_legacyKeySignature;

    @Override
    public ByteBuffer viafabricplus_getLegacyPublicKeySignature() {
        return this.viafabricplus_legacyKeySignature;
    }

    @Override
    public void viafabricplus_setLegacyPublicKeySignature(ByteBuffer signature) {
        this.viafabricplus_legacyKeySignature = signature;
    }
}