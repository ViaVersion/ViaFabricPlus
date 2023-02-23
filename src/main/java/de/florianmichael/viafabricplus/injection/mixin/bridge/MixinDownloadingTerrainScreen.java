package de.florianmichael.viafabricplus.injection.mixin.bridge;

import de.florianmichael.viafabricplus.definition.c0_30.ClassicProgressRenderer;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public class MixinDownloadingTerrainScreen {

    @Inject(method = "render", at = @At("RETURN"))
    public void renderClassicProgress(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ClassicProgressRenderer.renderProgress(matrices);
    }
}
