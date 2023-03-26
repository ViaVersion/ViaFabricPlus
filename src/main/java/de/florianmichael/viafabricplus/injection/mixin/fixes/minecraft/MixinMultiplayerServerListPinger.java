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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import de.florianmichael.viafabricplus.definition.v1_14_4.LegacyServerAddress;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiplayerServerListPinger.class)
public class MixinMultiplayerServerListPinger {

    @Unique
    private ServerInfo viafabricplus_lastConnect;

    @Unique
    private ServerAddress viafabricplus_wrappedAddress;

    @Inject(method = "add", at = @At("HEAD"))
    public void track(ServerInfo entry, Runnable saver, CallbackInfo ci) {
        viafabricplus_lastConnect = entry;
    }

    @Inject(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AllowedAddressResolver;resolve(Lnet/minecraft/client/network/ServerAddress;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void doOwnParse(ServerInfo entry, Runnable saver, CallbackInfo ci, ServerAddress serverAddress) {
        viafabricplus_wrappedAddress = LegacyServerAddress.parse(((IServerInfo) viafabricplus_lastConnect).viafabricplus_forcedVersion(), serverAddress.getAddress());
    }

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;getAddress()Ljava/lang/String;"))
    public String replaceWithWrappedAddress(ServerAddress instance) {
        return viafabricplus_wrappedAddress.getAddress();
    }

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;getPort()I"))
    public int replaceWithWrappedPort(ServerAddress instance) {
        return viafabricplus_wrappedAddress.getPort();
    }
}
