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

package com.viaversion.viafabricplus.injection.mixin.features.v1_12_2.movement;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFluidInteraction.class)
public abstract class MixinEntityFluidInteraction {

    @Unique
    private Entity viaFabricPlus$waterEntity;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getFluidInteractionBox()Lnet/minecraft/world/phys/AABB;"))
    private AABB useWaterBox1_12_2(Entity instance) {
        this.viaFabricPlus$waterEntity = instance;
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return viaFabricPlus$getWaterBox(instance);
        } else {
            return instance.getFluidInteractionBox();
        }
    }

    // The tracker height can be <= 0 inside the raised 1.12 box, 1.12 only checks for any water block in it
    @Inject(method = "isInFluid", at = @At("RETURN"), cancellable = true)
    private void checkWaterDirectly(TagKey<Fluid> fluid, CallbackInfoReturnable<Boolean> cir) {
        if (this.viaFabricPlus$waterEntity != null && fluid == FluidTags.WATER && ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final AABB box = viaFabricPlus$getWaterBox(this.viaFabricPlus$waterEntity);
            cir.setReturnValue(this.viaFabricPlus$waterEntity.level().getBlockStatesIfLoaded(box).anyMatch(state -> state.getFluidState().is(FluidTags.WATER)));
        }
    }

    @Definition(id = "fluidTop", local = @Local(type = double.class, name = "fluidTop"))
    @Definition(id = "box", local = @Local(type = AABB.class, name = "box"))
    @Definition(id = "minY", field = "Lnet/minecraft/world/phys/AABB;minY:D")
    @Expression("fluidTop < box.minY")
    @ModifyExpressionValue(method = "update", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean removeConditional(boolean original) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2) ) {
            return false; // Equates to true due to the negation in the original code
        } else {
            return original;
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 dontScaleCurrent(Vec3 instance, double scale) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return instance;
        } else {
            return instance.scale(scale);
        }
    }

    @Unique
    private static AABB viaFabricPlus$getWaterBox(final Entity entity) {
        return entity.getBoundingBox().inflate(0.0, -0.4F, 0.0).deflate(0.001);
    }

}
