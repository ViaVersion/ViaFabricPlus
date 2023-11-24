package de.florianmichael.viafabricplus.injection.mixin.fixes.authlib;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import de.florianmichael.viafabricplus.injection.access.IKeyPairResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(value = KeyPairResponse.class, remap = false)
public class MixinKeyPairResponse implements IKeyPairResponse {

    @Unique
    private ByteBuffer viaFabricPlus$legacyKeySignature;

    @Override
    public ByteBuffer viafabricplus$getLegacyPublicKeySignature() {
        return this.viaFabricPlus$legacyKeySignature;
    }

    @Override
    public void viafabricplus$setLegacyPublicKeySignature(ByteBuffer signature) {
        this.viaFabricPlus$legacyKeySignature = signature;
    }
}
