/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.input;

import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Shadow
    private float cameraY;

    @Shadow
    private float lastCameraY;

    @Shadow
    private Entity focusedEntity;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.BEFORE))
    private void onUpdateHeight(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!DebugSettings.global().replaceSneaking.isEnabled() && DebugSettings.global().sneakInstant.isEnabled()) {
            cameraY = lastCameraY = focusedEntity.getStandingEyeHeight();
        }
    }

    @Inject(method = "updateEyeHeight", at = @At(value = "HEAD"), cancellable = true)
    private void onUpdateEyeHeight(CallbackInfo ci) {
        if (this.focusedEntity == null) return;

        if (DebugSettings.global().replaceSneaking.isEnabled()) {
            ci.cancel();
            this.lastCameraY = this.cameraY;

            if (this.focusedEntity instanceof PlayerEntity player && !player.isSleeping()) {
                if (player.isSneaking()) {
                    cameraY = 1.54F;
                } else if (!DebugSettings.global().longSneaking.isEnabled()) {
                    cameraY = 1.62F;
                } else if (cameraY < 1.62F) {
                    float delta = 1.62F - cameraY;
                    delta *= 0.4;
                    cameraY = 1.62F - delta;
                }
            } else {
                cameraY = focusedEntity.getStandingEyeHeight();
            }
        }
    }
}
