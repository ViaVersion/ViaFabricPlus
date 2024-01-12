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

package de.florianmichael.viafabricplus.injection.mixin.fixes.vialegacy;

import de.florianmichael.viafabricplus.fixes.versioned.classic.CPEAdditions;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.ClientboundPacketsc0_30cpe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientboundPacketsc0_30cpe.class, remap = false)
public abstract class MixinClientboundPacketsc0_30cpe {

    @Inject(method = "getPacket", at = @At("HEAD"), cancellable = true)
    private static void addCustomPackets(int id, CallbackInfoReturnable<ClientboundPacketsc0_30cpe> cir) {
        if (CPEAdditions.CUSTOM_PACKETS.containsKey(id)) {
            cir.setReturnValue(CPEAdditions.CUSTOM_PACKETS.get(id));
        }
    }

}
