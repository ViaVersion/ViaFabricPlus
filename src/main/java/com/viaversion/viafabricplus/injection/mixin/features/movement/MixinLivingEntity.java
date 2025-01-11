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

package com.viaversion.viafabricplus.injection.mixin.features.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    private Optional<BlockPos> climbingPos;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract boolean canEnterTrapdoor(BlockPos pos, BlockState state);

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Redirect(method = "travelMidAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isChunkLoaded(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean modifyLoadedCheck(World instance, BlockPos blockPos) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return this.getWorld().isChunkLoaded(blockPos) && instance.getChunkManager().isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        } else {
            return this.getWorld().isChunkLoaded(blockPos);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onLanding()V"))
    private void dontResetLevitationFallDistance(LivingEntity instance) {
        if (this.hasStatusEffect(StatusEffects.SLOW_FALLING) || ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
            instance.onLanding();
        }
    }

    @Inject(method = "canEnterTrapdoor", at = @At("HEAD"), cancellable = true)
    private void disableCrawling(CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ci.setReturnValue(false);
        }
    }

    @Inject(method = "isClimbing", at = @At("RETURN"), cancellable = true)
    private void allowGappedLadderClimb(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThan(LegacyProtocolVersion.b1_5tob1_5_2) && !cir.getReturnValueZ() && !this.isSpectator()) {
            final BlockPos blockPos = this.getBlockPos().up();
            final BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isIn(BlockTags.CLIMBABLE)) {
                this.climbingPos = Optional.of(blockPos);
                cir.setReturnValue(true);
            } else if (blockState.getBlock() instanceof TrapdoorBlock && this.canEnterTrapdoor(blockPos, blockState)) {
                this.climbingPos = Optional.of(blockPos);
                cir.setReturnValue(true);
            }
        }
    }

}
