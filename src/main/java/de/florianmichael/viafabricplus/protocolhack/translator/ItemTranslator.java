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

package de.florianmichael.viafabricplus.protocolhack.translator;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import net.raphimc.vialegacy.protocols.beta.protocol1_0_0_1tob1_8_0_1.ClientboundPacketsb1_8;
import net.raphimc.vialegacy.protocols.beta.protocol1_0_0_1tob1_8_0_1.types.Typesb1_8_0_1;
import net.raphimc.vialegacy.protocols.release.protocol1_4_4_5to1_4_2.types.Types1_4_2;

public class ItemTranslator {

    private static final UserConnection VIA_B1_8_TO_MC_USER_CONNECTION = ProtocolHack.createDummyUserConnection(ProtocolHack.NATIVE_VERSION, LegacyProtocolVersion.b1_8tob1_8_1);
    private static final int CREATIVE_INVENTORY_ACTION_ID = NetworkState.PLAY.getHandler(NetworkSide.SERVERBOUND).getId(new CreativeInventoryActionC2SPacket(0, ItemStack.EMPTY));

    /**
     * Converts a Minecraft item stack to a ViaVersion item stack
     *
     * @param stack         The Minecraft item stack
     * @param targetVersion The target version to convert to (e.g. r1.13)
     * @return The ViaVersion item stack for the target version
     */
    public static Item mcToVia(final ItemStack stack, final ProtocolVersion targetVersion) {
        final UserConnection user = ProtocolHack.createDummyUserConnection(ProtocolHack.NATIVE_VERSION, targetVersion);

        try {
            final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeShort(0); // slot
            buf.writeItemStack(stack); // item

            final PacketWrapper wrapper = PacketWrapper.create(CREATIVE_INVENTORY_ACTION_ID, buf, user);
            user.getProtocolInfo().getPipeline().transform(Direction.SERVERBOUND, State.PLAY, wrapper);

            wrapper.read(Type.SHORT); // slot
            return wrapper.read(getItemType(targetVersion)); // item
        } catch (Throwable t) {
            ViaFabricPlus.global().getLogger().error("Error converting native item stack to ViaVersion " + targetVersion + " item stack", t);
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
            return Type.ITEM1_8;
        } else if (targetVersion.olderThan(ProtocolVersion.v1_13_2)) {
            return Type.ITEM1_13;
        } else if (targetVersion.olderThanOrEqualTo(ProtocolVersion.v1_20_2)) {
            return Type.ITEM1_13_2;
        } else {
            return Type.ITEM1_20_2;
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
            final PacketWrapper wrapper = PacketWrapper.create(ClientboundPacketsb1_8.SET_SLOT, VIA_B1_8_TO_MC_USER_CONNECTION);
            wrapper.write(Type.BYTE, (byte) 0); // window id
            wrapper.write(Type.SHORT, (short) 0); // slot
            wrapper.write(Types1_4_2.NBTLESS_ITEM, item); // item

            wrapper.resetReader();
            wrapper.user().getProtocolInfo().getPipeline().transform(Direction.CLIENTBOUND, State.PLAY, wrapper);

            wrapper.read(Type.UNSIGNED_BYTE); // sync id
            wrapper.read(Type.VAR_INT); // revision
            wrapper.read(Type.SHORT); // slot
            final Item viaItem = wrapper.read(Type.ITEM1_13_2); // item
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
