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

package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.entity;

import com.viaversion.viafabricplus.ViaFabricPlus;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFluidInteraction.class)
public abstract class MixinEntityFluidInteraction {

    @Unique
    private Entity viaFabricPlus$currentEntity;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getFluidInteractionBox()Lnet/minecraft/world/phys/AABB;"))
    private AABB inflateFluidInteractionBox(Entity instance) {
        this.viaFabricPlus$currentEntity = instance;
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return instance.getBoundingBox().inflate(0.0, -0.4, 0.0).deflate(0.001);
        } else {
            return instance.getFluidInteractionBox();
        }
    }

    // Replace in liquid check with direct check and specify specific bounding box for fluid type
    @Inject(method = "isInFluid", at = @At("RETURN"), cancellable = true)
    private void replaceInFluidCheck(TagKey<Fluid> fluid, CallbackInfoReturnable<Boolean> cir) {
        if (this.viaFabricPlus$currentEntity != null) {
            final AABB entityBoundingBox = this.viaFabricPlus$currentEntity.getBoundingBox();

            AABB liquidBoundingBox = null;
            if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) && fluid == FluidTags.WATER) {
                liquidBoundingBox = entityBoundingBox.inflate(0.0, -0.4F, 0.0).deflate(0.001);
            } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2) && fluid == FluidTags.LAVA) {
                liquidBoundingBox = entityBoundingBox.deflate(0.1F, 0.4F, 0.1F);
            } else if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) && fluid == FluidTags.LAVA) {
                liquidBoundingBox = this.viaFabricPlus$currentEntity.getFluidInteractionBox();
            }

            if (liquidBoundingBox != null) {
                cir.setReturnValue(this.viaFabricPlus$currentEntity.level().getBlockStatesIfLoaded(liquidBoundingBox).anyMatch(key -> key.getFluidState().is(fluid)));
            }
        }
    }

}
