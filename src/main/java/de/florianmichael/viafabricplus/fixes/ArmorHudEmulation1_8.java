/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.UUID;

public class ArmorHudEmulation1_8 {

    private static final UUID ARMOR_POINTS_UUID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");

    private static double oldArmor = 0;

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (!VisualSettings.global().emulateArmorHud.isEnabled()) {
                return;
            }

            if (MinecraftClient.getInstance().player != null) {
                final UserConnection userConnection = ProtocolHack.getPlayNetworkUserConnection();
                if (userConnection != null) {
                    try {
                        sendArmorUpdate(userConnection);
                    } catch (Throwable t) {
                        ViaFabricPlus.global().getLogger().error("Error sending armor update", t);
                    }
                }
            }
        });
    }

    public static void sendArmorUpdate(final UserConnection userConnection) throws Exception {
        int armor = 0;
        for (final ItemStack stack : MinecraftClient.getInstance().player.getInventory().armor) {
            armor += ArmorType.findByType(Registries.ITEM.getId(stack.getItem()).toString()).getArmorPoints();
        }
        if (armor == oldArmor) return;

        oldArmor = armor;
        final PacketWrapper properties = PacketWrapper.create(ClientboundPackets1_9.ENTITY_PROPERTIES, userConnection);
        properties.write(Type.VAR_INT, MinecraftClient.getInstance().player.getId());
        properties.write(Type.INT, 1);
        properties.write(Type.STRING, "generic.armor");
        properties.write(Type.DOUBLE, 0D);
        properties.write(Type.VAR_INT, 1);
        properties.write(Type.UUID, ARMOR_POINTS_UUID);
        properties.write(Type.DOUBLE, (double) armor);
        properties.write(Type.BYTE, (byte) 0);
        properties.scheduleSend(Protocol1_9To1_8.class);
    }

}
