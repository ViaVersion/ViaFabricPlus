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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_14to1_13_2;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets$2", remap = false)
public class MixinInventoryPackets_2 {

    @Inject(method = "lambda$register$0", at = @At(value = "FIELD", target = "Lcom/viaversion/viaversion/api/type/Type;BOOLEAN:Lcom/viaversion/viaversion/api/type/types/BooleanType;", ordinal = 2, shift = At.Shift.BEFORE))
    public void removeWrongData(PacketWrapper wrapper, CallbackInfo ci) {
        wrapper.clearInputBuffer();
    }
}
