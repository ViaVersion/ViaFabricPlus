package de.florianmichael.viafabricplus.injection.mixin.fixes.input;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.definition.v1_12_2.SyncInputExecutor;
import de.florianmichael.viafabricplus.setting.groups.DebugSettings;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
    public void redirectSync(MinecraftClient instance, Runnable runnable) {
        if (DebugSettings.getClassWrapper().executeInputsInSync.getValue()) {
            SyncInputExecutor.trackKeyboardInteraction(runnable);
            return;
        }

        instance.execute(runnable);
    }

    @Redirect(method = "processF3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendCommand(Ljava/lang/String;)Z", ordinal = 0))
    public boolean replaceSpectatorCommand(ClientPlayNetworkHandler instance, String command) {
        if (ViaLoadingBase.getTargetVersion().isOlderThan(ProtocolVersion.v1_8)) {
            return false;
        }
        return instance.sendCommand(command);
    }
}
