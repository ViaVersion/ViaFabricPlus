/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package com.viaversion.viafabricplus.injection.mixin.base.perserverversion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.fixes.ClientsideFixes;
import com.viaversion.viafabricplus.injection.access.IMultiValueDebugSampleLogImpl;
import com.viaversion.viafabricplus.injection.access.IServerInfo;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.net.InetSocketAddress;

@Mixin(MultiplayerServerListPinger.class)
public abstract class MixinMultiplayerServerListPinger {

    @WrapOperation(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerAddress;parse(Ljava/lang/String;)Lnet/minecraft/client/network/ServerAddress;"))
    private ServerAddress replaceDefaultPort(String address, Operation<ServerAddress> original, @Local(argsOnly = true) ServerInfo entry) {
        // Replace port when pinging the server and the forced version is set
        return original.call(ClientsideFixes.replaceDefaultPort(address, ((IServerInfo) entry).viaFabricPlus$forcedVersion()));
    }

    @WrapOperation(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/util/profiler/MultiValueDebugSampleLogImpl;)Lnet/minecraft/network/ClientConnection;"))
    private ClientConnection setForcedVersion(InetSocketAddress address, boolean useEpoll, MultiValueDebugSampleLogImpl packetSizeLog, Operation<ClientConnection> original, @Local(argsOnly = true) ServerInfo serverInfo) {
        final IServerInfo mixinServerInfo = (IServerInfo) serverInfo;

        if (mixinServerInfo.viaFabricPlus$forcedVersion() != null && !mixinServerInfo.viaFabricPlus$passedDirectConnectScreen()) {
            // We use the PerformanceLog field to store the forced version since it's always null when pinging a server
            // So we can create a dummy instance, store the forced version in it and later destroy the instance again
            // To avoid any side effects, we also support cases where a mod is also creating a PerformanceLog instance
            if (packetSizeLog == null) {
                packetSizeLog = new MultiValueDebugSampleLogImpl(1);
            }

            // Attach the forced version to the PerformanceLog instance
            ((IMultiValueDebugSampleLogImpl) packetSizeLog).viaFabricPlus$setForcedVersion(mixinServerInfo.viaFabricPlus$forcedVersion());
            mixinServerInfo.viaFabricPlus$passDirectConnectScreen(false);
        }

        return original.call(address, useEpoll, packetSizeLog);
    }

}
