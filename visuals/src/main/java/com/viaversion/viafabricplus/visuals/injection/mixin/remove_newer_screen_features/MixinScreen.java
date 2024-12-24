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

package com.viaversion.viafabricplus.visuals.injection.mixin.remove_newer_screen_features;

import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Inject(method = "addDrawableChild", at = @At("HEAD"), cancellable = true)
    private <T extends Element & Drawable & Selectable> void removeRecipeBook(T drawableElement, CallbackInfoReturnable<T> cir) {
        if (drawableElement instanceof TexturedButtonWidget button && button.textures == RecipeBookWidget.BUTTON_TEXTURES) {
            final boolean furnace = ((Screen) (Object) this) instanceof FurnaceScreen;

            if (VisualSettings.INSTANCE.hideFurnaceRecipeBook.isEnabled() && furnace) {
                cir.setReturnValue(drawableElement);
            } else if (VisualSettings.INSTANCE.hideCraftingRecipeBook.isEnabled() && !furnace) {
                cir.setReturnValue(drawableElement);
            }
        }
    }

}
