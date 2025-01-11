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

package com.viaversion.viafabricplus.features.emulation.armor_hud;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.DebugSettings;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.data.ArmorTypes1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_9;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.UUID;

public final class ArmorHudEmulation1_8 {

    private static final UUID ARMOR_POINTS_UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");

    private static double previousArmorPoints = 0;

    static {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (!DebugSettings.INSTANCE.emulateArmorHud.isEnabled()) {
                return;
            }

            if (MinecraftClient.getInstance().player != null) {
                final UserConnection userConnection = ProtocolTranslator.getPlayNetworkUserConnection();
                if (userConnection != null) {
                    try {
                        sendArmorUpdate(userConnection);
                    } catch (Throwable t) {
                        ViaFabricPlusImpl.INSTANCE.logger().error("Error sending armor update", t);
                    }
                }
            } else {
                previousArmorPoints = 0;
            }
        });
    }

    public static void init() {
        // Calls the static block
    }

    private static void sendArmorUpdate(final UserConnection userConnection) {
        // Calculate the armor points.
        int armor = 0;
        for (final ItemStack stack : MinecraftClient.getInstance().player.getInventory().armor) {
            armor += ArmorTypes1_8.findByType(Registries.ITEM.getId(stack.getItem()).toString()).getArmorPoints();
        }

        // We only want to update the armor points if they actually changed.
        if (armor == previousArmorPoints) {
            return;
        }
        previousArmorPoints = armor;

        final PacketWrapper updateAttributes = PacketWrapper.create(ClientboundPackets1_9.UPDATE_ATTRIBUTES, userConnection);
        updateAttributes.write(Types.VAR_INT, MinecraftClient.getInstance().player.getId());
        updateAttributes.write(Types.INT, 1);
        updateAttributes.write(Types.STRING, "generic.armor");
        updateAttributes.write(Types.DOUBLE, 0.0D);
        updateAttributes.write(Types.VAR_INT, 1);
        updateAttributes.write(Types.UUID, ARMOR_POINTS_UUID);
        updateAttributes.write(Types.DOUBLE, (double) armor);
        updateAttributes.write(Types.BYTE, (byte) 0);
        updateAttributes.scheduleSend(Protocol1_8To1_9.class);
    }

}
