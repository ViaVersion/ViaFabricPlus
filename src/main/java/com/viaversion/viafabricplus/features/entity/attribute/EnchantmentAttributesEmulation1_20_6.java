/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

package com.viaversion.viafabricplus.features.entity.attribute;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceKey;
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
                    setAttribute(livingEntity, Attributes.WATER_MOVEMENT_EFFICIENCY, getEquipmentLevel(Enchantments.DEPTH_STRIDER, livingEntity) / 3D);
                    setGenericMovementEfficiencyAttribute(livingEntity);
                }
            }

            // Update player-specific attributes for all players
            for (Player player : world.players()) {
                if (!player.isLocalInstanceAuthoritative()) {
                    continue;
                }

                final int efficiencyLevel = getEquipmentLevel(Enchantments.EFFICIENCY, player);
                setAttribute(player, Attributes.MINING_EFFICIENCY, efficiencyLevel > 0 ? efficiencyLevel * efficiencyLevel + 1D : 0D);
                setAttribute(player, Attributes.SNEAKING_SPEED, 0.3D + getEquipmentLevel(Enchantments.SWIFT_SNEAK, player) * 0.15D);
                setAttribute(player, Attributes.SUBMERGED_MINING_SPEED, getEquipmentLevel(Enchantments.AQUA_AFFINITY, player) <= 0 ? 0.2D : 1D);
                setAttribute(player, Attributes.ATTACK_KNOCKBACK, getEquipmentLevel(Enchantments.KNOCKBACK, player));
            }
        });
    }

    /**
     * Called from MixinLivingEntity as well to ensure the attribute value is set at the correct place in the entity tick logic.
     * Called above just as a fallback if a mod accesses the raw attribute value directly.
     */
    public static void setGenericMovementEfficiencyAttribute(final LivingEntity entity) {
        final boolean isOnSoulSpeedBlock = entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS);
        setAttribute(entity, Attributes.MOVEMENT_EFFICIENCY, isOnSoulSpeedBlock && getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0 ? 1 : 0);
    }

    private static int getEquipmentLevel(final ResourceKey<Enchantment> enchantment, final LivingEntity livingEntity) {
        return EnchantmentHelper.getEnchantmentLevel(livingEntity.level().registryAccess().getOrThrow(enchantment), livingEntity);
    }

    private static void setAttribute(final LivingEntity entity, final Holder<Attribute> attribute, final double value) {
        final AttributeInstance attributeInstance = entity.getAttribute(attribute);
        attributeInstance.removeModifiers(); // Minecraft is applying attribute modifiers in some situations, remove them before we set the base value
        attributeInstance.setBaseValue(value);
    }

}
