package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import de.florianmichael.viafabricplus.settings.groups.VisualSettings;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At("RETURN"))
    public void changeChatLength(CallbackInfo ci) {
        this.chatField.setMaxLength(ChatLengthDefinition.getMaxLength());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getIndicatorAt(DD)Lnet/minecraft/client/gui/hud/MessageIndicator;"))
    public MessageIndicator removeIndicator(ChatHud instance, double mouseX, double mouseY) {
        if (VisualSettings.getClassWrapper().hideSignatureIndicator.getValue()) {
            return null;
        }
        return instance.getIndicatorAt(mouseX, mouseY);
    }
}
