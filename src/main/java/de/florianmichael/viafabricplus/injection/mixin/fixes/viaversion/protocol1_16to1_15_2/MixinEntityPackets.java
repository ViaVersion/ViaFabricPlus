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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_16to1_15_2;

import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ServerboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import de.florianmichael.viafabricplus.injection.access.IInventoryTracker1_16;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPackets.class, remap = false)
public class MixinEntityPackets {

    @Inject(method = "register", at = @At("RETURN"))
    private static void rewriteCheck(Protocol1_16To1_15_2 protocol, CallbackInfo ci) {
        protocol.registerServerbound(ServerboundPackets1_16.ANIMATION, ServerboundPackets1_14.ANIMATION, wrapper -> {
            final InventoryTracker1_16 inventoryTracker = wrapper.user().get(InventoryTracker1_16.class);
            // Don't send an arm swing if the player has an inventory opened.
            if (((IInventoryTracker1_16) inventoryTracker).viafabricplus_isInventoryOpen()) {
                wrapper.cancel();
            }
        }, true);
    }
}
