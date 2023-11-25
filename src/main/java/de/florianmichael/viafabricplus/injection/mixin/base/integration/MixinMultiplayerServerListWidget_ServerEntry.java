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

package de.florianmichael.viafabricplus.injection.mixin.base.integration;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
public abstract class MixinMultiplayerServerListWidget_ServerEntry {

    @Shadow
    @Final
    private ServerInfo server;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setMultiplayerScreenTooltip(Ljava/util/List;)V", ordinal = 0))
    private void drawTranslatingState(MultiplayerScreen instance, List<Text> tooltip, Operation<Void> original) {
        if (GeneralSettings.INSTANCE.showAdvertisedServerVersion.getValue()) {
            final IServerInfo mixinServerInfo = ((IServerInfo) server);

            if (mixinServerInfo.viaFabricPlus$enabled()) {
                final var versionEnum = VersionEnum.fromProtocolId(mixinServerInfo.viaFabricPlus$translatingVersion());

                tooltip.add(Text.translatable("misc.viafabricplus.translate", versionEnum != VersionEnum.UNKNOWN ? versionEnum.getName() + " (" + versionEnum.getVersion() + ")" : mixinServerInfo.viaFabricPlus$translatingVersion()));
                tooltip.add(Text.translatable("misc.viafabricplus.serverversion", server.version.getString() + " (" + server.protocolVersion + ")"));
            }
        }

        original.call(instance, tooltip);
    }

}
