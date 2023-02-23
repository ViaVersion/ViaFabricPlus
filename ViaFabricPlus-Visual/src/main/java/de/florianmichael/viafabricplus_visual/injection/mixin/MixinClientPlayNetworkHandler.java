package de.florianmichael.viafabricplus_visual.injection.mixin;

import de.florianmichael.viafabricplus_visual.ViaFabricPlusVisual;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Redirect(method = "onServerMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ServerMetadataS2CPacket;isSecureChatEnforced()Z"))
    public boolean removeSecureChatWarning(ServerMetadataS2CPacket instance) {
        return instance.isSecureChatEnforced() || ViaFabricPlusVisual.disableSecureChatWarning.getValue();
    }
}
