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

package com.viaversion.viafabricplus.features.entity.attribute;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;

public final class EnchantmentAttributesEmulation1_20_6 {

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20_5)) {
                return;
            }
            // Update generic attributes for all entities
            for (Entity entity : world.entitiesForRendering()) {
                if (entity.isLocalInstanceAuthoritative() && entity instanceof LivingEntity livingEntity) {
                    livingEntity.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(getEquipmentLevel(Enchantments.DEPTH_STRIDER, livingEntity) / 3F);
                    setGenericMovementEfficiencyAttribute(livingEntity);
                }
            }

            // Update player specific attributes for all players
            for (Player player : world.players()) {
                if (!player.isLocalInstanceAuthoritative()) {
                    continue;
                }
                final int efficiencyLevel = getEquipmentLevel(Enchantments.EFFICIENCY, player);
                if (efficiencyLevel > 0) {
                    player.getAttribute(Attributes.MINING_EFFICIENCY).setBaseValue(efficiencyLevel * efficiencyLevel + 1);
                } else {
                    player.getAttribute(Attributes.MINING_EFFICIENCY).setBaseValue(0);
                }

                player.getAttribute(Attributes.SNEAKING_SPEED).setBaseValue(0.3F + getEquipmentLevel(Enchantments.SWIFT_SNEAK, player) * 0.15F);
                player.getAttribute(Attributes.SUBMERGED_MINING_SPEED).setBaseValue(getEquipmentLevel(Enchantments.AQUA_AFFINITY, player) <= 0 ? 0.2F : 1F);
            }
        });
    }

    /**
     * Called from MixinLivingEntity as well to ensure the attribute value is set at the correct place in the entity tick logic.
     * Called above just as a fallback if a mod accesses the raw attribute value directly.
     */
    public static void setGenericMovementEfficiencyAttribute(final LivingEntity entity) {
        final boolean isOnSoulSpeedBlock = entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS);
        if (isOnSoulSpeedBlock && getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0) {
            entity.getAttribute(Attributes.MOVEMENT_EFFICIENCY).setBaseValue(1);
        } else {
            entity.getAttribute(Attributes.MOVEMENT_EFFICIENCY).setBaseValue(0);
        }
    }

    private static int getEquipmentLevel(final ResourceKey<Enchantment> enchantment, final LivingEntity entity) {
        final Optional<Holder.Reference<Enchantment>> enchantmentRef = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(enchantment);
        return enchantmentRef.map(e -> EnchantmentHelper.getEnchantmentLevel(e, entity)).orElse(0);
    }

}
