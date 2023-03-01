package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerAddress.class)
public class MixinServerAddress {

    @Shadow @Final private static ServerAddress INVALID;

    @Inject(method = "parse", at = @At("RETURN"), cancellable = true)
    private static void fixAddress(String address, CallbackInfoReturnable<ServerAddress> cir) {
        if (!cir.getReturnValue().equals(INVALID) && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            cir.setReturnValue(AllowedAddressResolver.DEFAULT.redirectResolver.lookupRedirect(cir.getReturnValue()).orElse(cir.getReturnValue()));
        }
    }
}
