package de.florianmichael.everyprotocol.injection.mixin.base;

import de.florianmichael.everyprotocol.EveryProtocol;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MixinMain {

    @Inject(method = "main([Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReport;initCrashReport()V"))
    private static void loadViaLoadingBase(CallbackInfo ci) {
        EveryProtocol.getClassWrapper().create();
    }
}
