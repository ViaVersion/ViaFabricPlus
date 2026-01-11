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

package com.viaversion.viafabricplus.injection.mixin.base.connection.bedrock;

import com.viaversion.viafabricplus.base.bedrock.NetherNetInetSocketAddress;
import com.viaversion.viafabricplus.injection.access.base.bedrock.IServerAddress;
import dev.kastle.netty.channel.nethernet.config.NetherNetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerNameResolver.class)
public abstract class MixinServerNameResolver {

    @Inject(method = "resolveAddress", at = @At("HEAD"), cancellable = true)
    private void returnNetherNetAddressEarly(ServerAddress serverAddress, CallbackInfoReturnable<Optional<ResolvedServerAddress>> cir) {
        if ((Object) serverAddress instanceof IServerAddress mixinServerAddress && mixinServerAddress.viaFabricPlus$getNetherNetAddress() != null) {
            final NetherNetAddress netherNetAddress = mixinServerAddress.viaFabricPlus$getNetherNetAddress();
            cir.setReturnValue(Optional.of(new ResolvedServerAddress() {
                @Override
                public String getHostName() {
                    return netherNetAddress.getNetworkId();
                }

                @Override
                public String getHostIp() {
                    return netherNetAddress.getNetworkId();
                }

                @Override
                public int getPort() {
                    return 0;
                }

                @Override
                public InetSocketAddress asInetSocketAddress() {
                    return new NetherNetInetSocketAddress(netherNetAddress);
                }
            }));
        }
    }

}
