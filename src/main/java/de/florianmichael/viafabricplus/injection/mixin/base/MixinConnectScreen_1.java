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

import de.florianmichael.viafabricplus.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.definition.ClientsideFixes;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Final
    @Shadow
    ServerInfo field_40415;

    @Final
    @Shadow
    ServerAddress field_33737;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Optional;get()Ljava/lang/Object;"))
    public Object mapSocketAddress(Optional<InetSocketAddress> instance) {
        final var original = instance.orElse(null); // original invoke
        if (field_40415 != null) {
            // Handle forced version system
            final VersionEnum forcedVersion = ((IServerInfo) field_40415).viafabricplus_forcedVersion();
            if (forcedVersion != null) {
                ProtocolHack.getForcedVersions().put(original, forcedVersion);
                ChangeProtocolVersionCallback.EVENT.invoker().onChangeProtocolVersion(forcedVersion);
            }
        }

        if (ClientsideFixes.LEGACY_SRV_RESOLVE.contains(ProtocolHack.getTargetVersion(original))) {
            final var direct = AllowedAddressResolver.DEFAULT.addressResolver.resolve(this.field_33737).orElse(null);

            if (direct != null) return direct.getInetSocketAddress();
        }
        return original; // original invoke
    }
}
