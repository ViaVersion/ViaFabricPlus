/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.base.integration.bedrock;

import com.viaversion.viafabricplus.injection.access.base.bedrock.IConfirmScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConfirmScreen.class)
public abstract class MixinConfirmScreen implements IConfirmScreen {

    @Mutable
    @Shadow
    @Final
    private Text message;

    @Shadow
    protected abstract void init();

    @Unique
    private boolean viaFabricPlus$selfInflicted = false;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I", shift = At.Shift.AFTER), cancellable = true)
    private void preventButtonClearing(CallbackInfo ci) {
        if (viaFabricPlus$selfInflicted) {
            viaFabricPlus$selfInflicted = false;
            ci.cancel();
        }
    }

    @Override
    public void viaFabricPlus$setMessage(Text message) {
        viaFabricPlus$selfInflicted = true;
        this.message = message;
        this.init();
    }

}
