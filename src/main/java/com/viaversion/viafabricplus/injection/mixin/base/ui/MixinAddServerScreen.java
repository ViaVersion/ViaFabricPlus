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

package com.viaversion.viafabricplus.injection.mixin.base.ui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.injection.access.IServerInfo;
import com.viaversion.viafabricplus.screen.base.PerServerVersionScreen;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public abstract class MixinAddServerScreen extends Screen {

    @Shadow
    @Final
    private ServerInfo server;

    @Shadow
    private TextFieldWidget serverNameField;

    @Shadow
    private TextFieldWidget addressField;

    @Unique
    private String viaFabricPlus$nameField;

    @Unique
    private String viaFabricPlus$addressField;

    public MixinAddServerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addVersionSetterButton(CallbackInfo ci) {
        final int buttonPosition = GeneralSettings.global().addServerScreenButtonOrientation.getIndex();
        if (buttonPosition == 0) { // Off
            return;
        }

        final ProtocolVersion forcedVersion = ((IServerInfo) server).viaFabricPlus$forcedVersion();

        // Restore input if the user cancels the version selection screen (or if the user is editing an existing server)
        if (viaFabricPlus$nameField != null && viaFabricPlus$addressField != null) {
            this.serverNameField.setText(viaFabricPlus$nameField);
            this.addressField.setText(viaFabricPlus$addressField);

            viaFabricPlus$nameField = null;
            viaFabricPlus$addressField = null;
        }

        ButtonWidget.Builder buttonBuilder = ButtonWidget.builder(forcedVersion == null ? Text.translatable("base.viafabricplus.set_version") : Text.of(forcedVersion.getName()), button -> {
            // Store current input in case the user cancels the version selection
            viaFabricPlus$nameField = serverNameField.getText();
            viaFabricPlus$addressField = addressField.getText();

            client.setScreen(new PerServerVersionScreen(this, version -> ((IServerInfo) server).viaFabricPlus$forceVersion(version)));
        }).size(98, 20);

        // Set the button's position according to the configured orientation and add the button to the screen
        this.addDrawableChild(GeneralSettings.withOrientation(buttonBuilder, buttonPosition, width, height).build());
    }

}
