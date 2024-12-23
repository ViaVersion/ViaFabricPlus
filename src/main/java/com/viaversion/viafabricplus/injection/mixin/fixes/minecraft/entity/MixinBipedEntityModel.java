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

import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class MixinBipedEntityModel<T extends BipedEntityRenderState> {

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Redirect(method = "positionBlockingArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
    private float preventArmFollowingThirdPersonRotation(float value, float min, float max) {
        if (VisualSettings.global().lockBlockingArmRotation.isEnabled()) {
            return 0.0F;
        } else {
            return MathHelper.clamp(value, min, max);
        }
    }

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPart;roll:F", ordinal = 1, shift = At.Shift.AFTER))
    private void addOldWalkAnimation(T bipedEntityRenderState, CallbackInfo ci) {
        if (VisualSettings.global().oldWalkingAnimation.isEnabled()) {
            final float limbFrequency = bipedEntityRenderState.limbFrequency;
            final float limbAmplitudeMultiplier = bipedEntityRenderState.limbAmplitudeMultiplier;

            this.rightArm.pitch = MathHelper.cos(limbFrequency * 0.6662F + 3.1415927F) * 2.0F * limbAmplitudeMultiplier;
            this.rightArm.roll = (MathHelper.cos(limbFrequency * 0.2312F) + 1.0F) * 1.0F * limbAmplitudeMultiplier;

            this.leftArm.pitch = MathHelper.cos(limbFrequency * 0.6662F) * 2.0F * limbAmplitudeMultiplier;
            this.leftArm.roll = (MathHelper.cos(limbFrequency * 0.2812F) - 1.0F) * 1.0F * limbAmplitudeMultiplier;
        }
    }

}
