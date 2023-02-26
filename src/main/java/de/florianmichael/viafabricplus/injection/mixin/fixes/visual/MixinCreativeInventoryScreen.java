package de.florianmichael.viafabricplus.injection.mixin.fixes.visual;

import de.florianmichael.viafabricplus.definition.c0_30.ClassicItemSelectionScreen;
import de.florianmichael.viafabricplus.setting.groups.VisualSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen {

    @Inject(method = "init", at = @At("RETURN"))
    public void replaceCreativeMenu(CallbackInfo ci) {
        if (VisualSettings.getClassWrapper().replaceCreativeInventory.getValue()) {
            MinecraftClient.getInstance().setScreen(ClassicItemSelectionScreen.INSTANCE);
        }
    }
}
