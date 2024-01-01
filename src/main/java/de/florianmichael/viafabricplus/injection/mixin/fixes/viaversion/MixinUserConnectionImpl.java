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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.connection.UserConnectionImpl;
import de.florianmichael.viafabricplus.protocolhack.util.NoPacketSendChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = UserConnectionImpl.class, remap = false)
public abstract class MixinUserConnectionImpl {

    @Shadow
    @Final
    private Channel channel;

    @Inject(method = "sendRawPacket(Lio/netty/buffer/ByteBuf;Z)V", at = @At("HEAD"), cancellable = true)
    private void handleNoPacketSendChannel(ByteBuf packet, boolean currentThread, CallbackInfo ci) {
        if (this.channel instanceof NoPacketSendChannel) {
            ci.cancel();
        }
    }

}
