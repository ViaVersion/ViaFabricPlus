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

package com.viaversion.viafabricplus.injection.mixin.features.movement.water;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends MixinLivingEntity {

    @Unique
    private int viaFabricPlus$ticksSinceSwimming;

    public MixinPlayerEntity(final EntityType<?> type, final World world) {
        super(type, world);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;ofFloored(DDD)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos modifyWaterAbovePosition(double x, double y, double z) {
        return BlockPos.ofFloored(x, y - (double)1.0F + 0.1D, z);
    }

    @WrapWithCondition(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private boolean preventSwimmingMotionWhenJumping(PlayerEntity instance, Vec3d vec3d) {
        return !ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) || !instance.isJumping();
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;"))
    private void preventJumpingWhenStartedSwimming(Vec3d movementInput, CallbackInfo ci) {
        if (this.isSwimming()) {
            this.viaFabricPlus$ticksSinceSwimming++;
        } else {
            this.viaFabricPlus$ticksSinceSwimming = 0;
        }

        if (this.viaFabricPlus$ticksSinceSwimming > 0 && this.viaFabricPlus$ticksSinceSwimming < 10 && this.isJumping()) {
            this.setVelocity(this.getVelocity().getX(), 0, this.getVelocity().getZ());
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSwimming()Z"))
    private boolean preventSwimmingResurface(PlayerEntity instance) {
        if (!ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) || !instance.isSwimming()) {
            return instance.isSwimming();
        }

        double d = this.getRotationVector().y;
        // TODO: The value used here (0.55) isn't entirely correct, however in most cases it should be fine.
        if (this.getEntityWorld().getFluidState(BlockPos.ofFloored(this.getX(), this.getY() + 0.4, this.getZ())).isEmpty() && d > 0 && d < 0.55) {
            instance.setVelocity(instance.getVelocity().getX(), 0, instance.getVelocity().getZ());
            return false;
        }

        return true;
    }

}
