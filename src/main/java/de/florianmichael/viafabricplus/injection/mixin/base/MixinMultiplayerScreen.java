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
package de.florianmichael.viafabricplus.injection.mixin.base;

import de.florianmichael.viafabricplus.definition.LegacyServerAddress;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.screen.impl.base.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.base.settings.groups.GeneralSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerScreen extends Screen {

    public MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void addProtocolSelectionButton(CallbackInfo ci) {
        ButtonWidget.Builder builder = ButtonWidget.builder(Text.literal("ViaFabricPlus"), button -> ProtocolSelectionScreen.INSTANCE.open(this));

        final int orientation = GeneralSettings.INSTANCE.multiplayerScreenButtonOrientation.getIndex();
        switch (orientation) {
            case 0 -> builder = builder.position(5, 5);
            case 1 -> builder = builder.position(width - 98 - 5, 5);
            case 2 -> builder = builder.position(5, height - 20 - 5);
            case 3 -> builder = builder.position(width - 98 - 5, height - 20 - 5);
        }

        this.addDrawableChild(builder.size(98, 20).build());
    }

    @Unique
    private ServerInfo viafabricplus_lastConnect;

    @Inject(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"))
    public void track(ServerInfo entry, CallbackInfo ci) {
        viafabricplus_lastConnect = entry;
    }

    @Redirect(method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;parse(Ljava/lang/String;)Lnet/minecraft/client/network/ServerAddress;"))
    public ServerAddress doOwnParse(String address) {
        return LegacyServerAddress.parse(((IServerInfo) viafabricplus_lastConnect).viafabricplus_forcedVersion(), address);
    }
}
