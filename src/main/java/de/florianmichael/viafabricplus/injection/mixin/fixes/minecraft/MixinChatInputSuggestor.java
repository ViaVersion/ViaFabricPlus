/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.raphimc.vialoader.util.VersionEnum;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatInputSuggestor.class)
public abstract class MixinChatInputSuggestor {

    @Shadow
    public abstract void refresh();

    @Shadow
    @Nullable
    private ChatInputSuggestor.@Nullable SuggestionWindow window;

    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    @Final
    private List<OrderedText> messages;

    @Inject(method = "provideRenderText", at = @At(value = "HEAD"), cancellable = true)
    private void disableTextFieldColors(String original, int firstCharacterIndex, CallbackInfoReturnable<OrderedText> cir) {
        if (!this.cancelTabComplete()) return;

        cir.setReturnValue(OrderedText.styledForwardsVisitedString(original, Style.EMPTY));
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handle1_12_2KeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.cancelTabComplete()) return;

        if (keyCode == GLFW.GLFW_KEY_TAB && this.window == null) {
            this.refresh();
        } else if (this.window != null) {
            if (this.window.keyPressed(keyCode, scanCode, modifiers)) {
                cir.setReturnValue(true);
                return;
            }
            this.textField.setSuggestion(null);
            this.window = null;
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void clearMessages(DrawContext drawContext, int mouseX, int mouseY, CallbackInfo ci) {
        if (!this.cancelTabComplete()) return;

        this.messages.clear();
    }

    @Unique
    private boolean cancelTabComplete() {
        return ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2) && this.textField.getText().startsWith("/");
    }

}
