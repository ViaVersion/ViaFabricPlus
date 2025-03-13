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

package com.viaversion.viafabricplus.injection.mixin.features.block.mining_calculation;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    @Shadow
    public abstract boolean canHarvest(BlockState state);

    @Shadow
    @Final
    PlayerInventory inventory;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
    private boolean changeSpeedCalculation(PlayerEntity instance, RegistryEntry<StatusEffect> statusEffect, @Local LocalFloatRef f) {
        final boolean hasMiningFatigue = instance.hasStatusEffect(statusEffect);
        if (hasMiningFatigue && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            f.set(f.get() * (1.0F - (this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier() + 1) * 0.2F));
            if (f.get() < 0) {
                f.set(0);
            }
            return false; // disable original code
        }
        return hasMiningFatigue;
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(Lnet/minecraft/entity/LivingEntity;)Z"))
    private void changeSpeedCalculation(BlockState block, CallbackInfoReturnable<Float> cir, @Local LocalFloatRef f) {
        final float efficiency = (float) this.getAttributeValue(EntityAttributes.MINING_EFFICIENCY);
        if (efficiency <= 0) {
            return;
        }

        final float speed = this.inventory.getSelectedStack().getMiningSpeedMultiplier(block);
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_4tor1_4_5) && this.canHarvest(block)) {
            f.set(speed + efficiency);
        } else if (speed > 1F || ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            if (!this.getMainHandStack().isEmpty()) {
                if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
                    if (speed <= 1.0 && !this.canHarvest(block)) {
                        f.set(speed + efficiency * 0.08F);
                    } else {
                        f.set(speed + efficiency);
                    }
                }
            }
        }
    }

}
