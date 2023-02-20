package de.florianmichael.viafabricplus.injection.mixin.viaversion.protocol1_14to1_13_2;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets$2", remap = false)
public class MixinInventoryPackets_2 {

    @Inject(method = "lambda$register$0", at = @At(value = "FIELD", target = "Lcom/viaversion/viaversion/api/type/Type;BOOLEAN:Lcom/viaversion/viaversion/api/type/types/BooleanType;", ordinal = 2, shift = At.Shift.BEFORE))
    public void removeWrongData(PacketWrapper wrapper, CallbackInfo ci) {
        wrapper.clearInputBuffer();
    }
}
