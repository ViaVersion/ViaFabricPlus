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

import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.screen.ForceVersionScreen;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public class MixinAddServerScreen extends Screen {

    @Shadow @Final private ServerInfo server;

    public MixinAddServerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void injectButton(CallbackInfo ci) {
        final ComparableProtocolVersion forcedVersion = ((IServerInfo) server).viafabricplus_forcedVersion();
        this.addDrawableChild(ButtonWidget.builder(Text.literal(forcedVersion == null ? "Set version for this server" :forcedVersion.getName()), button ->
                        client.setScreen(new ForceVersionScreen(this, version -> ((IServerInfo) server).viafabricplus_forceVersion(version)))).
                position(width - (forcedVersion == null ? 150 : 98) - 5, 5).size(forcedVersion == null ? 150 : 98, 20).build());
    }
}
