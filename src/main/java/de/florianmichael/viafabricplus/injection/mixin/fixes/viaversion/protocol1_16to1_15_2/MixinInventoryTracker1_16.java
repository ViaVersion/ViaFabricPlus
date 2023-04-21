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

import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.storage.InventoryTracker1_16;
import de.florianmichael.viafabricplus.injection.access.IInventoryTracker1_16;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// If the server uses -1 as Window ID, it can break ViaVersion
@Mixin(value = InventoryTracker1_16.class, remap = false)
public class MixinInventoryTracker1_16 implements IInventoryTracker1_16 {

    @Unique
    private boolean viafabricplus_inventoryOpen = false;

    @Inject(method = "setInventory", at = @At("RETURN"))
    public void setInventoryOpen(short inventory, CallbackInfo ci) {
        this.viafabricplus_inventoryOpen = true;
    }

    @Override
    public boolean viafabricplus_isInventoryOpen() {
        return this.viafabricplus_inventoryOpen;
    }

    @Override
    public void viafabricplus_setInventoryOpen(boolean inventoryOpen) {
        this.viafabricplus_inventoryOpen = inventoryOpen;
    }
}
