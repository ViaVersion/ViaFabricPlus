package de.florianmichael.viafabricplus.injection.mixin.fixes.packet;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(UpdatePlayerAbilitiesC2SPacket.class)
public class MixinUpdatePlayerAbilitiesC2SPacket {

    @Shadow
    @Final
    private boolean flying;

    @Inject(method = "write", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeByte(I)Lio/netty/buffer/ByteBuf;", shift = At.Shift.BEFORE), cancellable = true)
    public void injectWrite(PacketByteBuf buf, CallbackInfo ci) {
        final PlayerAbilities abilities = MinecraftClient.getInstance().player.getAbilities();

        byte b = 0;

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            if (this.flying) b = (byte) (b | 2); // Minecraft

            if (abilities.invulnerable) b |= 1;
            if (abilities.allowFlying) b |= 4;
            if (abilities.creativeMode) b |= 8; // Protocol Hack Fixes

            buf.writeByte(b);
            ci.cancel();
        }
    }
}
