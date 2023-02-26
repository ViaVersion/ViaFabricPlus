package de.florianmichael.viafabricplus.injection.mixin.fixes.screen;

import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class MixinChatScreen {

    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At("RETURN"))
    public void changeChatLength(CallbackInfo ci) {
        this.chatField.setMaxLength(ChatLengthDefinition.getMaxLength());
    }
}
