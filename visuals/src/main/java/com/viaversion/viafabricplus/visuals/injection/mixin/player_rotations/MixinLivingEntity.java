/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.visuals.injection.mixin.player_rotations;

import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    public float bodyYaw;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F"))
    private float changeBodyRotationInterpolation(float g) {
        if (VisualSettings.INSTANCE.changeBodyRotationInterpolation.isEnabled()) {
            g = MathHelper.clamp(g, -75.0F, 75.0F);
            this.bodyYaw = this.getYaw() - g;
            if (Math.abs(g) > 50.0F) {
                this.bodyYaw += g * 0.2F;
            }
            return Float.MIN_VALUE; // Causes the if to always fail
        } else {
            return Math.abs(g);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;abs(F)F"))
    private float alwaysRotateWhenWalkingBackwards(float value) {
        if (VisualSettings.INSTANCE.sidewaysBackwardsRunning.isEnabled()) {
            return 0F;
        } else {
            return MathHelper.abs(value);
        }
    }

}