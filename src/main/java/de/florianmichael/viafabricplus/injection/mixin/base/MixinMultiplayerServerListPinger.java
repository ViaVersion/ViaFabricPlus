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

import com.llamalad7.mixinextras.sugar.Local;
import de.florianmichael.viafabricplus.injection.access.IPerformanceLog;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.profiler.PerformanceLog;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(MultiplayerServerListPinger.class)
public abstract class MixinMultiplayerServerListPinger {

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/profiler/PerformanceLog;)Lnet/minecraft/network/ClientConnection;"))
    private ClientConnection setForcedVersion(InetSocketAddress address, boolean useEpoll, PerformanceLog packetSizeLog, @Local ServerInfo serverInfo) {
        final VersionEnum forcedVersion = ((IServerInfo) serverInfo).viaFabricPlus$forcedVersion();
        if (forcedVersion != null) {
            if (packetSizeLog == null) {
                packetSizeLog = new PerformanceLog();
            }

            ((IPerformanceLog) packetSizeLog).viaFabricPlus$setForcedVersion(forcedVersion);
        }

        return ClientConnection.connect(address, useEpoll, packetSizeLog);
    }

}
