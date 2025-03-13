/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.remove_newer_screen_features;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureBlockScreen.class)
public abstract class MixinStructureBlockScreen {

    @Shadow
    private CyclingButtonWidget<Boolean> strictButton;

    @Shadow
    @Final
    private static Text STRICT_TEXT;

    @Inject(method = "updateWidgets", at = @At("RETURN"))
    private void hideStrictButton(StructureBlockMode mode, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            strictButton.visible = false;
        }
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private boolean hideStrictText(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color) {
        if (text == STRICT_TEXT) {
            return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_21_4);
        } else {
            return true;
        }
    }

}
