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

package de.florianmichael.viafabricplus.protocoltranslator.translator;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.Types1_20_5;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.fixes.VFPProtocol;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.Registries;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocol.beta.b1_8_0_1tor1_0_0_1.packet.ClientboundPacketsb1_8;
import net.raphimc.vialegacy.protocol.beta.b1_8_0_1tor1_0_0_1.types.Typesb1_8_0_1;
import net.raphimc.vialegacy.protocol.release.r1_4_2tor1_4_4_5.types.Types1_4_2;

public class ItemTranslator {

    private static final UserConnection VIA_B1_8_TO_MC_USER_CONNECTION = ProtocolTranslator.createDummyUserConnection(ProtocolTranslator.NATIVE_VERSION, LegacyProtocolVersion.b1_8tob1_8_1);

    /**
     * Converts a Minecraft item stack to a ViaVersion item stack
     *
     * @param stack         The Minecraft item stack
     * @param targetVersion The target version to convert to (e.g. v1.13)
     * @return The ViaVersion item stack for the target version
     */
    public static Item mcToVia(final ItemStack stack, final ProtocolVersion targetVersion) {
        final UserConnection user = ProtocolTranslator.createDummyUserConnection(ProtocolTranslator.NATIVE_VERSION, targetVersion);

        try {
            final RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), MinecraftClient.getInstance().getNetworkHandler().getRegistryManager());
            buf.writeShort(0); // slot
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, stack); // item

            final PacketWrapper setCreativeModeSlot = PacketWrapper.create(VFPProtocol.getSetCreativeModeSlot(), buf, user);
            user.getProtocolInfo().getPipeline().transform(Direction.SERVERBOUND, State.PLAY, setCreativeModeSlot);

            setCreativeModeSlot.read(Types.SHORT); // slot
            return setCreativeModeSlot.read(getItemType(targetVersion)); // item
        } catch (Throwable t) {
            ViaFabricPlus.global().getLogger().error("Error converting native item stack to ViaVersion {} item stack", targetVersion, t);
            return null;
        }
    }

    /**
     * Gets the ViaVersion item type for the target version
     *
     * @param targetVersion The target version
     * @return The ViaVersion item type
     */
    public static Type<Item> getItemType(final ProtocolVersion targetVersion) {
        if (targetVersion.olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            return Typesb1_8_0_1.CREATIVE_ITEM;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_13)) {
            return Types.ITEM1_8;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_13_2)) {
            return Types.ITEM1_13;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_20_2)) {
            return Types.ITEM1_13_2;
        }  else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_20_3)) {
            return Types.ITEM1_20_2;
        } else {
            return Types1_20_5.ITEM;
        }
    }

    /**
     * Converts a ViaVersion b1.8 item to a Minecraft item stack
     *
     * @param item The ViaVersion b1.8 item
     * @return The Minecraft item stack
     */
    public static ItemStack viaB1_8toMc(final Item item) {
        try {
            final PacketWrapper containerSetSlot = PacketWrapper.create(ClientboundPacketsb1_8.CONTAINER_SET_SLOT, VIA_B1_8_TO_MC_USER_CONNECTION);
            containerSetSlot.write(Types.BYTE, (byte) 0); // window id
            containerSetSlot.write(Types.SHORT, (short) 0); // slot
            containerSetSlot.write(Types1_4_2.NBTLESS_ITEM, item); // item

            containerSetSlot.resetReader();
            containerSetSlot.user().getProtocolInfo().getPipeline().transform(Direction.CLIENTBOUND, State.PLAY, containerSetSlot);

            containerSetSlot.read(Types.UNSIGNED_BYTE); // sync id
            containerSetSlot.read(Types.VAR_INT); // revision
            containerSetSlot.read(Types.SHORT); // slot
            final Item viaItem = containerSetSlot.read(getItemType(ProtocolTranslator.NATIVE_VERSION)); // item
            final ItemStack mcItem = new ItemStack(Registries.ITEM.get(viaItem.identifier()));
            mcItem.setCount(viaItem.amount());
            mcItem.setDamage(viaItem.data());
            return mcItem;
        } catch (Throwable t) {
            ViaFabricPlus.global().getLogger().error("Error converting ViaVersion b1.8 item to native item stack", t);
            return ItemStack.EMPTY;
        }
    }

}
