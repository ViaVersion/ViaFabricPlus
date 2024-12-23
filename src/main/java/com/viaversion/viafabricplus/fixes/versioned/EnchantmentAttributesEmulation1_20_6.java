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

package com.viaversion.viafabricplus.fixes.versioned;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class EnchantmentAttributesEmulation1_20_6 {

    static {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20_5)) {
                return;
            }
            // Update generic attributes for all entities
            for (Entity entity : world.getEntities()) {
                if (entity.isLogicalSideForUpdatingMovement() && entity instanceof LivingEntity livingEntity) {
                    livingEntity.getAttributeInstance(EntityAttributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(getEquipmentLevel(Enchantments.DEPTH_STRIDER, livingEntity) / 3F);
                    setGenericMovementEfficiencyAttribute(livingEntity);
                }
            }

            // Update player specific attributes for all players
            for (PlayerEntity player : world.getPlayers()) {
                if (!player.isLogicalSideForUpdatingMovement()) {
                    continue;
                }
                final int efficiencyLevel = getEquipmentLevel(Enchantments.EFFICIENCY, player);
                if (efficiencyLevel > 0) {
                    player.getAttributeInstance(EntityAttributes.MINING_EFFICIENCY).setBaseValue(efficiencyLevel * efficiencyLevel + 1);
                } else {
                    player.getAttributeInstance(EntityAttributes.MINING_EFFICIENCY).setBaseValue(0);
                }

                player.getAttributeInstance(EntityAttributes.SNEAKING_SPEED).setBaseValue(0.3F + getEquipmentLevel(Enchantments.SWIFT_SNEAK, player) * 0.15F);
                player.getAttributeInstance(EntityAttributes.SUBMERGED_MINING_SPEED).setBaseValue(getEquipmentLevel(Enchantments.AQUA_AFFINITY, player) <= 0 ? 0.2F : 1F);
            }
        });
    }

    public static void init() {
        // Calls the static block
    }

    /**
     * Called from MixinLivingEntity as well to ensure the attribute value is set at the correct place in the entity tick logic.
     * Called above just as a fallback if a mod accesses the raw attribute value directly.
     */
    public static void setGenericMovementEfficiencyAttribute(final LivingEntity entity) {
        final boolean isOnSoulSpeedBlock = entity.getWorld().getBlockState(entity.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS);
        if (isOnSoulSpeedBlock && getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0) {
            entity.getAttributeInstance(EntityAttributes.MOVEMENT_EFFICIENCY).setBaseValue(1);
        } else {
            entity.getAttributeInstance(EntityAttributes.MOVEMENT_EFFICIENCY).setBaseValue(0);
        }
    }

    private static int getEquipmentLevel(final RegistryKey<Enchantment> enchantment, final LivingEntity entity) {
        final Optional<RegistryEntry.Reference<Enchantment>> enchantmentRef = entity.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(enchantment);
        return enchantmentRef.map(e -> EnchantmentHelper.getEquipmentLevel(e, entity)).orElse(0);
    }

}
