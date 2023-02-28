package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_15to1_14_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.Protocol1_15To1_14_4;
import de.florianmichael.viafabricplus.definition.v1_14_4.Meta18Storage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocol1_15To1_14_4.class, remap = false)
public class MixinProtocol1_15To1_14_4 {

    @Inject(method = "init", at = @At("HEAD"))
    public void addMeta18Storage(UserConnection connection, CallbackInfo ci) {
        connection.put(new Meta18Storage(connection));
    }
}
