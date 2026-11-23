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

package com.viaversion.viafabricplus.injection.mixin.features.v1_15_2.movement;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFluidInteraction.class)
public abstract class MixinEntityFluidInteraction {

    @Unique
    private Entity viaFabricPlus$lavaEntity;

    @Inject(method = "update", at = @At("HEAD"))
    private void captureEntity(Entity entity, boolean ignoreCurrent, CallbackInfoReturnable<Boolean> cir) {
        this.viaFabricPlus$lavaEntity = entity;
    }

    // <= 1.13.2 queried the world directly, 1.14-1.15.2 set the state on any lava block collision regardless of fluid height
    @Inject(method = "isInFluid", at = @At("RETURN"), cancellable = true)
    private void checkLavaDirectly(TagKey<Fluid> fluid, CallbackInfoReturnable<Boolean> cir) {
        if (this.viaFabricPlus$lavaEntity != null && fluid == FluidTags.LAVA && ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            final AABB box;
            if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
                box = this.viaFabricPlus$lavaEntity.getBoundingBox().deflate(0.1F, 0.4F, 0.1F);
            } else {
                box = this.viaFabricPlus$lavaEntity.getFluidInteractionBox();
            }
            cir.setReturnValue(this.viaFabricPlus$lavaEntity.level().getBlockStatesIfLoaded(box).anyMatch(state -> state.getFluidState().is(FluidTags.LAVA)));
        }
    }

    @Definition(id = "eyeY", local = @Local(type = double.class, name = "eyeY"))
    @Definition(id = "fluidTopForCamera", local = @Local(type = double.class, name = "fluidTopForCamera"))
    @Expression("eyeY <= fluidTopForCamera")
    @ModifyExpressionValue(method = "update", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean addMagicOffset(boolean original, @Local(name = "eyeY") double eyeY, @Local(name = "fluidBottom") double fluidBottom, @Local(name = "level") BlockGetter level, @Local(name = "fluidState") FluidState fluidState, @Local(name = "mutablePos") BlockPos.MutableBlockPos mutablePos) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            return eyeY <= (fluidBottom + (fluidState.getHeight(level, mutablePos) + (0.11111111F * 2F)));
        } else {
            return original;
        }
    }

    @Inject(method = "applyCurrentTo", at = @At("HEAD"), cancellable = true)
    private void disableLavaCurrent(TagKey<Fluid> fluid, Entity entity, double scale, CallbackInfo ci) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) && fluid == FluidTags.LAVA) {
            ci.cancel();
        }
    }

}
