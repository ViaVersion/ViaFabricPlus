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

import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viafabricplus.screen.impl.PerServerVersionScreen;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManageServerScreen.class)
public abstract class MixinAddServerScreen extends Screen {

    @Shadow
    @Final
    private ServerData serverData;

    @Shadow
    private EditBox nameEdit;

    @Shadow
    private EditBox ipEdit;

    @Unique
    private String viaFabricPlus$nameField;

    @Unique
    private String viaFabricPlus$addressField;

    public MixinAddServerScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addVersionSetterButton(CallbackInfo ci) {
        final int buttonPosition = GeneralSettings.INSTANCE.addServerScreenButtonOrientation.getIndex();
        if (buttonPosition == 0) { // Off
            return;
        }

        final IServerInfo mixinServerInfo = (IServerInfo) serverData;
        final ProtocolVersion forcedVersion = mixinServerInfo.viaFabricPlus$forcedVersion();

        // Restore input if the user cancels the version selection screen (or if the user is editing an existing server)
        if (viaFabricPlus$nameField != null && viaFabricPlus$addressField != null) {
            this.nameEdit.setValue(viaFabricPlus$nameField);
            this.ipEdit.setValue(viaFabricPlus$addressField);

            viaFabricPlus$nameField = null;
            viaFabricPlus$addressField = null;
        }

        final Button.Builder buttonBuilder = Button.builder(forcedVersion == null ? Component.translatable("base.viafabricplus.set_version") : Component.nullToEmpty(forcedVersion.getName()), button -> {
            // Store current input in case the user cancels the version selection
            viaFabricPlus$nameField = nameEdit.getValue();
            viaFabricPlus$addressField = ipEdit.getValue();

            minecraft.setScreen(new PerServerVersionScreen(this, mixinServerInfo::viaFabricPlus$forceVersion, mixinServerInfo::viaFabricPlus$forcedVersion));
        }).size(98, 20);
        GeneralSettings.setOrientation(buttonBuilder::pos, buttonPosition, width, height);
        this.addRenderableWidget(buttonBuilder.build());
    }

}
