/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.screen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.fixes.ClientsideFixes;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatScreen.class, priority = 1) // Apply our mixin first so other mods can override the chat length
public abstract class MixinChatScreen {

    @Shadow
    protected TextFieldWidget chatField;

    @Shadow
    private String originalChatText;

    @Shadow
    ChatInputSuggestor chatInputSuggestor;

    @Inject(method = "init", at = @At("RETURN"))
    private void changeChatLength(CallbackInfo ci) {
        this.chatField.setMaxLength(ClientsideFixes.getChatLength());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;getIndicatorAt(DD)Lnet/minecraft/client/gui/hud/MessageIndicator;"))
    private MessageIndicator removeIndicator(ChatHud instance, double mouseX, double mouseY) {
        if (VisualSettings.global().hideSignatureIndicator.isEnabled()) {
            return null;
        } else {
            return instance.getIndicatorAt(mouseX, mouseY);
        }
    }

    @WrapWithCondition(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setText(Ljava/lang/String;)V"))
    public boolean moveSetTextDown(TextFieldWidget instance, String text) {
        return !DebugSettings.global().legacyTabCompletions.isEnabled();
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void moveSetTextDown(CallbackInfo ci) {
        if (DebugSettings.global().legacyTabCompletions.isEnabled()) {
            this.chatField.setText(this.originalChatText);
            this.chatInputSuggestor.refresh();
        }
    }

    @Redirect(method = "onChatFieldUpdate", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
    private boolean fixCommandKey(String instance, Object other) {
        if (this.viaFabricPlus$keepTabComplete()) {
            return instance.equals(other);
        } else {
            return instance.isEmpty();
        }
    }

    @WrapWithCondition(method = "onChatFieldUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;refresh()V"))
    private boolean disableAutoTabComplete(ChatInputSuggestor instance) {
        return this.viaFabricPlus$keepTabComplete();
    }

    @Unique
    private boolean viaFabricPlus$keepTabComplete() {
        return !DebugSettings.global().legacyTabCompletions.isEnabled() || !this.chatField.getText().startsWith("/");
    }

}
