package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.ProtocolRange;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerAddress.class)
public class MixinServerAddress {

    @Shadow @Final private static ServerAddress INVALID;

    @Unique
    private final static ProtocolRange viafabricplus_srvRange = new ProtocolRange(ProtocolVersion.v1_16_4, LegacyProtocolVersion.r1_3_1tor1_3_2);

    @Inject(method = "parse", at = @At("RETURN"), cancellable = true)
    private static void fixAddress(String address, CallbackInfoReturnable<ServerAddress> cir) {
        if (!cir.getReturnValue().equals(INVALID) && viafabricplus_srvRange.contains(ViaLoadingBase.getClassWrapper().getTargetVersion())) {
            cir.setReturnValue(AllowedAddressResolver.DEFAULT.redirectResolver.lookupRedirect(cir.getReturnValue()).orElse(cir.getReturnValue()));
        }
    }
}
