/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_11.entity;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.features.v1_21_11.IEntityFluidInteraction;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityFluidInteraction.class)
public abstract class MixinEntityFluidInteraction implements IEntityFluidInteraction {

    @Unique
    private boolean viaFabricPlus$onlyFlowWater = false;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBox()Lnet/minecraft/world/phys/AABB;"))
    private AABB useCorrectBoxForEntityHeight(Entity instance) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_11)) {
            return instance.getFluidInteractionBox();
        } else {
            return instance.getBoundingBox();
        }
    }

    @Inject(method = "applyCurrentTo", at = @At("HEAD"), cancellable = true)
    private void ignoreLavaFlowUpdate(TagKey<Fluid> fluid, Entity entity, double scale, CallbackInfo ci) {
        if (this.viaFabricPlus$onlyFlowWater && fluid != FluidTags.WATER) {
            ci.cancel();
        }
    }

    @Override
    public void viaFabricPlus$onlyFlowWater(final Runnable runnable) {
        this.viaFabricPlus$onlyFlowWater = true;
        try {
            runnable.run();
        } finally {
            this.viaFabricPlus$onlyFlowWater = false;
        }
    }

}
