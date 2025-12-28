/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.injection.mixin.features.movement.liquid.MixinLivingEntity;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer extends MixinLivingEntity {

    @Unique
    private int viaFabricPlus$ticksSinceSwimming;

    public MixinPlayer(final EntityType<?> type, final Level world) {
        super(type, world);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"))
    private BlockPos modifyWaterAbovePosition(double x, double y, double z) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return BlockPos.containing(x, y - 0.9, z);
        } else {
            return BlockPos.containing(x, y, z);
        }
    }

    @WrapWithCondition(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    private boolean preventSwimmingMotionWhenJumping(Player instance, Vec3 vec3d) {
        return !ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) || !instance.isJumping();
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
    private void preventJumpingWhenStartedSwimming(Vec3 movementInput, CallbackInfo ci) {
        if (!ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return;
        }

        if (this.isSwimming()) {
            this.viaFabricPlus$ticksSinceSwimming++;
        } else {
            this.viaFabricPlus$ticksSinceSwimming = 0;
        }

        if (this.viaFabricPlus$ticksSinceSwimming > 0 && this.viaFabricPlus$ticksSinceSwimming < 10 && this.isJumping()) {
            this.setDeltaMovement(this.getDeltaMovement().x(), 0, this.getDeltaMovement().z());
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSwimming()Z"))
    private boolean preventSwimmingResurface(Player instance) {
        if (!ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) || !instance.isSwimming()) {
            return instance.isSwimming();
        }

        double d = this.getLookAngle().y;
        // TODO: The value used here (0.55) isn't entirely correct, however in most cases it should be fine.
        if (this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + 0.4, this.getZ())).isEmpty() && d > 0 && d < 0.55) {
            instance.setDeltaMovement(instance.getDeltaMovement().x(), 0, instance.getDeltaMovement().z());
            return false;
        }

        return true;
    }

}
