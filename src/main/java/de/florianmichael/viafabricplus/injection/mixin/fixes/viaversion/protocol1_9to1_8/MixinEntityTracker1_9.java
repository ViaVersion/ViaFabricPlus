/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

// Copyright RaphiMC/RK_01 - LICENSE file
@Mixin(value = EntityTracker1_9.class, remap = false)
public abstract class MixinEntityTracker1_9 {

    @Redirect(method = "handleMetadata", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z")))
    private float removeMin(float a, float b) {
        return a;
    }

    @Redirect(method = "handleMetadata", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z")))
    private float removeMax(float a, float b) {
        return b;
    }

    @Redirect(method = "handleMetadata", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/minecraft/metadata/Metadata;getValue()Ljava/lang/Object;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/configuration/ViaVersionConfig;isBossbarAntiflicker()Z")))
    private Object remapNaNToZero(Metadata instance) {
        if (instance.getValue() instanceof Float && ((Float) instance.getValue()).isNaN()) {
            return 0F;
        }

        return instance.getValue();
    }

}
