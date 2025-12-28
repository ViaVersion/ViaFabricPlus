/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.legacy_tab_completion;

import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Style;
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

@Mixin(CommandSuggestions.class)
public abstract class MixinCommandSuggestions {

    @Shadow
    public abstract void updateCommandInfo();

    @Shadow
    @Nullable
    private CommandSuggestions.@Nullable SuggestionsList suggestions;

    @Shadow
    @Final
    EditBox input;

    @Shadow
    @Final
    private List<FormattedCharSequence> commandUsage;

    @Inject(method = "formatChat", at = @At(value = "HEAD"), cancellable = true)
    private void disableTextFieldColors(String original, int firstCharacterIndex, CallbackInfoReturnable<FormattedCharSequence> cir) {
        if (this.viaFabricPlus$cancelTabComplete()) {
            cir.setReturnValue(FormattedCharSequence.forward(original, Style.EMPTY));
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void handle1_12_2KeyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (this.viaFabricPlus$cancelTabComplete()) {
            if (input.key() == GLFW.GLFW_KEY_TAB && this.suggestions == null) {
                this.updateCommandInfo();
            } else if (this.suggestions != null) {
                if (this.suggestions.keyPressed(input)) {
                    cir.setReturnValue(true);
                    return;
                }
                this.input.setSuggestion(null);
                this.suggestions = null;
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void clearMessages(GuiGraphics drawContext, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.viaFabricPlus$cancelTabComplete()) {
            this.commandUsage.clear();
        }
    }

    @Unique
    private boolean viaFabricPlus$cancelTabComplete() {
        return DebugSettings.INSTANCE.legacyTabCompletions.isEnabled() && this.input.getValue().startsWith("/");
    }

}
