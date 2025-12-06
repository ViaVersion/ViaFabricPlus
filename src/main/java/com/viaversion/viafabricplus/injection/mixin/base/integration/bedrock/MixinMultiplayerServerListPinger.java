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

package com.viaversion.viafabricplus.injection.mixin.base.integration.bedrock;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viafabricplus.settings.impl.BedrockSettings;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerStatusPinger.class)
public abstract class MixinMultiplayerServerListPinger {

    @WrapOperation(method = "pingServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/resolver/ServerAddress;parseString(Ljava/lang/String;)Lnet/minecraft/client/multiplayer/resolver/ServerAddress;"))
    private ServerAddress replaceDefaultPort(String address, Operation<ServerAddress> original, @Local(argsOnly = true) ServerData entry) {
        // Replace port when pinging the server and the forced version is set
        return original.call(BedrockSettings.replaceDefaultPort(address, ((IServerInfo) entry).viaFabricPlus$forcedVersion()));
    }

}
