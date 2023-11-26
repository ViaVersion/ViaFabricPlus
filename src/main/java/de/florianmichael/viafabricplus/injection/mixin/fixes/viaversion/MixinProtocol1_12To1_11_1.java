package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1$3", remap = false)
public abstract class MixinProtocol1_12To1_11_1 {

    @Redirect(method = "lambda$register$1", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/connection/ProtocolInfo;getProtocolVersion()I"))
    private static int dontClearRecipes(ProtocolInfo instance) {
        return -1;
    }

}
