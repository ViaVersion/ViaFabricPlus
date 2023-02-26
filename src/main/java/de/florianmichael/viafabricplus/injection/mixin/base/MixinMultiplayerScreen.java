package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.value.ValueHolder;
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
        ButtonWidget.Builder builder = ButtonWidget.builder(Text.literal("ViaFabricPlus"), button -> ProtocolSelectionScreen.open(this));

        final int orientation = ValueHolder.mainButtonOrientation.getIndex();
        switch (orientation) {
            case 0 -> builder = builder.position(0, 0);
            case 1 -> builder = builder.position(width - 98, 0);
            case 2 -> builder = builder.position(0, height - 20);
            case 3 -> builder = builder.position(width - 98, height - 20);
        }

        this.addDrawableChild(builder.size(98, 20).build());
    }
}
