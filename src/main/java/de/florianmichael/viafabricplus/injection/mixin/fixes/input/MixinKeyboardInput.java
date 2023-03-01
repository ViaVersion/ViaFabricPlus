package de.florianmichael.viafabricplus.injection.mixin.fixes.input;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    private boolean injectTick(boolean slowDown) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return this.sneaking;
        } else if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            return !MinecraftClient.getInstance().player.isSpectator() && (this.sneaking || slowDown);
        }
        return slowDown;
    }
}
