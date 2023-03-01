package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {

    @Shadow @Final private ClientConnection connection;

    @Inject(method = "joinServerSession", at = @At("HEAD"), cancellable = true)
    public void dontVerifySessionIfCracked(String serverId, CallbackInfoReturnable<Text> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            if (!connection.channel.attr(ViaFabricPlus.LOCAL_VIA_CONNECTION).get().get(ProtocolMetadataStorage.class).authenticate) {
                cir.setReturnValue(null);
            }
        }
    }
}
