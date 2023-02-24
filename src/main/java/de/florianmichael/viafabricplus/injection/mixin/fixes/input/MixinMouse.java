package de.florianmichael.viafabricplus.injection.mixin.fixes.input;

import de.florianmichael.viafabricplus.definition.v1_12_2.SyncInputExecutor;
import de.florianmichael.viafabricplus.value.ValueHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Mouse.class, priority = 1001)
public class MixinMouse {

    @Redirect(method = { "method_29615", "method_22685", "method_22684" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
    public void redirectSync(MinecraftClient instance, Runnable runnable) {
        if (ValueHolder.executeInputsInSync.getValue()) {
            SyncInputExecutor.trackMouseInteraction(runnable);
            return;
        }

        instance.execute(runnable);
    }
}
