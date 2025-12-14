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

package com.viaversion.viafabricplus.injection.mixin.base.connection;

import com.viaversion.viafabricplus.injection.access.base.IEventLoopGroupHolder;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EventLoopGroupHolder.class)
public class MixinEventLoopGroupHolder implements IEventLoopGroupHolder {

    @Unique
    private boolean viaFabricPlus$connecting = false;

    @Inject(method = "remote", at = @At("RETURN"))
    private static void resetConnectingFlag(CallbackInfoReturnable<EventLoopGroupHolder> cir) {
        ((IEventLoopGroupHolder) cir.getReturnValue()).viaFabricPlus$setConnecting(false);
    }

    @Override
    public boolean viaFabricPlus$isConnecting() {
        return viaFabricPlus$connecting;
    }

    @Override
    public void viaFabricPlus$setConnecting(final boolean connecting) {
        this.viaFabricPlus$connecting = connecting;
    }

}
