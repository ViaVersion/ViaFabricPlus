package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import de.florianmichael.viafabricplus.injection.access.IPublicKeyData;
import net.minecraft.client.util.ProfileKeysImpl;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProfileKeysImpl.class)
public class MixinProfileKeysImpl {

    @Inject(method = "decodeKeyPairResponse", at = @At("RETURN"))
    private static void trackLegacyKey(KeyPairResponse keyPairResponse, CallbackInfoReturnable<PlayerPublicKey.PublicKeyData> cir) {
        ((IPublicKeyData) (Object) cir.getReturnValue()).viafabricplus_set1_19_0Key(keyPairResponse.getLegacyPublicKeySignature());
    }
}
