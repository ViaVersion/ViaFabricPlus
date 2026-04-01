/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.networking.server_pinging;

import com.viaversion.viafabricplus.injection.access.base.IServerData;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.multiplayer.ServerStatusPinger$1")
public abstract class MixinServerStatusPinger_1 {

    @Final
    @Shadow
    ServerData val$data;

    @Shadow
    private static Component sanitizeDescription(final Component original) {
        return null;
    }

    @Redirect(method = "handleStatusResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerStatusPinger$1;sanitizeDescription(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/Component;"))
    private Component removeSanitizeDescription(Component component) {
        if (((IServerData) val$data).viaFabricPlus$translatingVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_11)) {
            return component;
        } else {
            return sanitizeDescription(component);
        }
    }

}
