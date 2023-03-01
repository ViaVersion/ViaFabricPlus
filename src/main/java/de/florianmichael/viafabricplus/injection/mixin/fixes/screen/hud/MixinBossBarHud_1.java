package de.florianmichael.viafabricplus.injection.mixin.fixes.screen.hud;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.hud.BossBarHud$1")
public class MixinBossBarHud_1 {

    @Redirect(method = "updateProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;setPercent(F)V"))
    public void nullSafety(ClientBossBar instance, float percent) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            if (instance != null) instance.setPercent(percent);
        }
    }
}
