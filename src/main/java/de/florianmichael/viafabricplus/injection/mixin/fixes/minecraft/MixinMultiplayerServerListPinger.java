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

import com.google.common.net.HostAndPort;
import de.florianmichael.viafabricplus.definition.LegacyServerAddress;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.lenni0451.reflect.stream.RStream;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiplayerServerListPinger.class)
public class MixinMultiplayerServerListPinger {

    @Inject(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AllowedAddressResolver;resolve(Lnet/minecraft/client/network/ServerAddress;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void mapParsing(ServerInfo entry, Runnable saver, CallbackInfo ci, ServerAddress serverAddress) {
        final ServerAddress remapped = LegacyServerAddress.parse(ProtocolHack.getTargetVersion(entry), serverAddress.hostAndPort.toString());
        RStream.of(serverAddress).fields().filter(HostAndPort.class).by(0).set(RStream.of(remapped).fields().filter(HostAndPort.class).by(0).get());
    }
}
