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

package com.viaversion.viafabricplus.injection.mixin.base.ui;

import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectConnectScreen.class)
public abstract class MixinDirectConnectScreen extends Screen {

    public MixinDirectConnectScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addProtocolSelectionButton(CallbackInfo ci) {
        final int buttonPosition = GeneralSettings.INSTANCE.directConnectScreenButtonOrientation.getIndex();
        if (buttonPosition == 0) { // Off
            return;
        }
        ButtonWidget.Builder builder = ButtonWidget.builder(Text.of("ViaFabricPlus"), button -> ProtocolSelectionScreen.INSTANCE.open(this)).size(98, 20);

        // Set the button's position according to the configured orientation and add the button to the screen
        this.addDrawableChild(GeneralSettings.withOrientation(builder, buttonPosition, width, height).build());
    }

}
