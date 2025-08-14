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

package com.viaversion.viafabricplus.visuals.injection.mixin.classic.walking_animation;

import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class MixinBipedEntityModel<T extends BipedEntityRenderState> {

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPart;roll:F", ordinal = 1, shift = At.Shift.AFTER))
    private void addOldWalkAnimation(T bipedEntityRenderState, CallbackInfo ci) {
        if (VisualSettings.INSTANCE.oldWalkingAnimation.isEnabled()) {
            final float limbSwingAnimationProgress = bipedEntityRenderState.limbSwingAnimationProgress;
            final float limbSwingAmplitude = bipedEntityRenderState.limbSwingAmplitude;

            this.rightArm.pitch = MathHelper.cos(limbSwingAnimationProgress * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmplitude;
            this.rightArm.roll = (MathHelper.cos(limbSwingAnimationProgress * 0.2312F) + 1.0F) * 1.0F * limbSwingAmplitude;

            this.leftArm.pitch = MathHelper.cos(limbSwingAnimationProgress * 0.6662F) * 2.0F * limbSwingAmplitude;
            this.leftArm.roll = (MathHelper.cos(limbSwingAnimationProgress * 0.2812F) - 1.0F) * 1.0F * limbSwingAmplitude;
        }
    }

}
