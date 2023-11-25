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

package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.screen.common.ForceVersionScreen;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public class MixinAddServerScreen extends Screen {

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
    private void injectButton(CallbackInfo ci) {
        final VersionEnum forcedVersion = ((IServerInfo) server).viaFabricPlus$forcedVersion();

        if (viaFabricPlus$nameField != null && viaFabricPlus$addressField != null) {
            this.serverNameField.setText(viaFabricPlus$nameField);
            this.addressField.setText(viaFabricPlus$addressField);

            viaFabricPlus$nameField = null;
            viaFabricPlus$addressField = null;
        }

        var builder = ButtonWidget.builder(forcedVersion == null ? Text.translatable("misc.viafabricplus.addserverscreenbuttontitle") : Text.literal(forcedVersion.getName()), button -> {
            viaFabricPlus$nameField = serverNameField.getText();
            viaFabricPlus$addressField = addressField.getText();

            client.setScreen(new ForceVersionScreen(this, version -> ((IServerInfo) server).viaFabricPlus$forceVersion(version)));
        });

        final int orientation = GeneralSettings.INSTANCE.addServerScreenButtonOrientation.getIndex();
        switch (orientation) {
            case 0 -> builder = builder.position(5, 5);
            case 1 -> builder = builder.position(width - (forcedVersion == null ? 150 : 98) - 5, 5);
            case 2 -> builder = builder.position(5, height - 20 - 5);
            case 3 -> builder = builder.position(width - (forcedVersion == null ? 150 : 98) - 5, height - 20 - 5);
        }

        this.addDrawableChild(builder.size(forcedVersion == null ? 150 : 98, 20).build());
    }
}
