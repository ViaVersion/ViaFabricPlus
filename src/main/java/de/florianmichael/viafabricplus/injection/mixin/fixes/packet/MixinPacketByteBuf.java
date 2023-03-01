package de.florianmichael.viafabricplus.injection.mixin.fixes.packet;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PacketByteBuf.class)
public class MixinPacketByteBuf {

    @Inject(method = "readText", at = @At(value = "INVOKE", target = "Lio/netty/handler/codec/DecoderException;<init>(Ljava/lang/String;)V", shift = At.Shift.BEFORE, remap = false), cancellable = true)
    public void injectReadText(CallbackInfoReturnable<Text> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_18)) {
            cir.setReturnValue(null);
        }
    }
}
