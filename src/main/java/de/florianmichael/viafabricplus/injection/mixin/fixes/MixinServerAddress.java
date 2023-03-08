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
package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.ProtocolRange;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerAddress.class)
public class MixinServerAddress {

    @Shadow @Final private static ServerAddress INVALID;

    @Unique
    private final static ProtocolRange viafabricplus_srvRange = new ProtocolRange(ProtocolVersion.v1_16_4, LegacyProtocolVersion.r1_3_1tor1_3_2);

    @Inject(method = "parse", at = @At("RETURN"), cancellable = true)
    private static void fixAddress(String address, CallbackInfoReturnable<ServerAddress> cir) {
        if (!cir.getReturnValue().equals(INVALID) && viafabricplus_srvRange.contains(ViaLoadingBase.getClassWrapper().getTargetVersion())) {
            cir.setReturnValue(AllowedAddressResolver.DEFAULT.redirectResolver.lookupRedirect(cir.getReturnValue()).orElse(cir.getReturnValue()));
        }
    }
}
