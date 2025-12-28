/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.injection.mixin.base.connection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.injection.access.base.IEventLoopGroupHolder;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public abstract class MixinConnectScreen_1 {

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Exception;getMessage()Ljava/lang/String;", remap = false))
    private String handleNullExceptionMessage(Exception instance, Operation<String> original) {
        // Vanilla doesn't have these cases, but we do because of RakNet and other modifications to the Netty pipeline
        return instance.getMessage() == null ? "" : original.call(instance);
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/EventLoopGroupHolder;remote(Z)Lnet/minecraft/server/network/EventLoopGroupHolder;"))
    private EventLoopGroupHolder markAsConnecting(boolean bl) {
        final EventLoopGroupHolder holder = EventLoopGroupHolder.remote(bl);
        ((IEventLoopGroupHolder) holder).viaFabricPlus$setConnecting(true);
        return holder;
    }

}
