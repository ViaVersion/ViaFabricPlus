package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void postLoad(RunArgs args, CallbackInfo ci) {
        try {
            ViaFabricPlus.getClassWrapper().postLoad();
        } catch (Exception ignored) {}
    }
}
