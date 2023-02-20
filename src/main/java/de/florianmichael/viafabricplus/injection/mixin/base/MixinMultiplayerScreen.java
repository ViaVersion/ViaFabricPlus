package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerScreen extends Screen {

    public MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void addProtocolSelectionButton(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("ViaFabricPlus"), button -> client.setScreen(ProtocolSelectionScreen.INSTANCE)).position(3, 3).size(98, 20).build());
    }
}
