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

import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryAcknowledgements.class, remap = false)
public abstract class MixinInventoryAcknowledgements {

    @Mutable
    @Shadow
    @Final
    private IntList ids;

    @Unique
    private it.unimi.dsi.fastutil.ints.IntList viaFabricPlus$ids;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void makeConcurrent(CallbackInfo ci) {
        this.ids = null;
        this.viaFabricPlus$ids = IntLists.synchronize(new IntArrayList());
    }

    @Inject(method = "addId", at = @At("HEAD"), cancellable = true)
    private void forwardAdd(int id, CallbackInfo ci) {
        viaFabricPlus$ids.add(id);
        ci.cancel();
    }

    @Inject(method = "removeId", at = @At("HEAD"), cancellable = true)
    private void forwardRemove(int id, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(viaFabricPlus$ids.rem(id));
    }

}
