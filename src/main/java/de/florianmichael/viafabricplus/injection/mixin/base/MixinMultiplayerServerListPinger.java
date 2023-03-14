/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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

import de.florianmichael.viafabricplus.platform.raknet.RakNetPingSessions;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;
import java.util.Optional;

@Mixin(MultiplayerServerListPinger.class)
public class MixinMultiplayerServerListPinger {

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public Object mapSocketAddress(Optional<InetSocketAddress> instance) {
        final InetSocketAddress address = instance.get();
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isEqualTo(BedrockProtocolVersion.bedrockLatest)) {
            RakNetPingSessions.storePingSession(address.getAddress());
        }
        return address;
    }
}
