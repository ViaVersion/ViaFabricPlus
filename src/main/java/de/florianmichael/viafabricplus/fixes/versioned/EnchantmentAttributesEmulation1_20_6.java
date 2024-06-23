/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.fixes.versioned;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.util.EnchantmentUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;

public class EnchantmentAttributesEmulation1_20_6 {

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            final ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
                final int efficiencyLevel = EnchantmentUtil.getEquipmentLevel(Enchantments.EFFICIENCY, player);
                if (efficiencyLevel > 0) {
                    player.getAttributeInstance(EntityAttributes.PLAYER_MINING_EFFICIENCY).setBaseValue(efficiencyLevel * efficiencyLevel + 1);
                } else {
                    player.getAttributeInstance(EntityAttributes.PLAYER_MINING_EFFICIENCY).setBaseValue(0);
                }
                player.getAttributeInstance(EntityAttributes.PLAYER_SNEAKING_SPEED).setBaseValue(0.3F + EnchantmentUtil.getEquipmentLevel(Enchantments.SWIFT_SNEAK, player) * 0.15F);
                player.getAttributeInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY).setBaseValue(EnchantmentUtil.getEquipmentLevel(Enchantments.DEPTH_STRIDER, player) / 3F);
                player.getAttributeInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).setBaseValue(EnchantmentUtil.getEquipmentLevel(Enchantments.AQUA_AFFINITY, player) <= 0 ? 0.2F : 1F);
            }
        });
    }

}
