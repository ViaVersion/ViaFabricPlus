/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.bridge;

import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import de.florianmichael.viafabricplus.settings.groups.BridgeSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class MixinOptionsScreen {

    @Shadow protected abstract ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier);

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/ClickableWidget;)Lnet/minecraft/client/gui/widget/ClickableWidget;", ordinal = 10, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addValuesButton(CallbackInfo ci, GridWidget gridWidget, GridWidget.Adder adder) {
        if (BridgeSettings.getClassWrapper().optionsButtonInGameOptions.getValue()) {
            adder.add(this.createButton(Text.literal("ViaFabricPlus").styled(style -> style.withColor(Formatting.GOLD)).append(" ").append("Settings..."), () -> {
                SettingsScreen.INSTANCE.prevScreen = (OptionsScreen) (Object) this;
                return SettingsScreen.INSTANCE;
            }));
        }
    }
}
