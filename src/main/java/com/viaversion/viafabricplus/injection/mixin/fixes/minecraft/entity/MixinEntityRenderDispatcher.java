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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.fixes.versioned.visual.BoatRenderer1_8;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    @Unique
    private BoatRenderer1_8 viaFabricPlus$boatRenderer;

    @Inject(method = "reload", at = @At("TAIL"))
    private void createBoatRenderer1_8(ResourceManager manager, CallbackInfo ci, @Local EntityRendererFactory.Context context) {
        viaFabricPlus$boatRenderer = new BoatRenderer1_8(context);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void useBoatRenderer1_8(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && entity instanceof BoatEntity) {
            cir.setReturnValue((EntityRenderer<? super T, ?>) viaFabricPlus$boatRenderer);
        }
    }

}
