package de.florianmichael.viafabricplus.injection.mixin.fixes.visual;

import de.florianmichael.viafabricplus.settings.groups.VisualSettings;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getIndicatorAt(DD)Lnet/minecraft/client/gui/hud/MessageIndicator;"))
    public MessageIndicator removeIndicator(ChatHud instance, double mouseX, double mouseY) {
        if (VisualSettings.getClassWrapper().hideSignatureIndicator.getValue()) {
            return null;
        }
        return instance.getIndicatorAt(mouseX, mouseY);
    }
}
