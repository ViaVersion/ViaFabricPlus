/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.compat.ipnext;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * https://github.com/blackd/Inventory-Profiles/tree/all-in-one is handling the offhand slot even when
 * ViaFabricPlus removes the slot in <= 1.8.9, so we have to cancel the handling of the offhand slot
 *
 * Fixes https://github.com/ViaVersion/ViaFabricPlus/issues/209
 */
@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Mixin(targets = "org.anti_ad.mc.ipnext.event.AutoRefillHandler$ItemSlotMonitor", remap = false)
public abstract class MixinAutoRefillHandler_ItemSlotMonitor {

    @Shadow
    public int currentSlotId;

    @Inject(method = { "checkHandle", "checkShouldHandle" }, at = @At("HEAD"), cancellable = true)
    public void dontHandleOffhandSlot(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            if (currentSlotId == 45) ci.cancel();
        }
    }

    @Inject(method = "updateCurrent", at = @At(value = "FIELD", target = "Lorg/anti_ad/mc/ipnext/event/AutoRefillHandler$ItemSlotMonitor;currentSlotId:I", shift = At.Shift.AFTER), cancellable = true)
    public void dontUpdateCurrentOffhandSlot(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            if (currentSlotId == 45) ci.cancel();
        }
    }

}
