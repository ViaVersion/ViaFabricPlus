/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.translator;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import de.florianmichael.viafabricplus.injection.access.IPacketWrapperImpl;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.List;
import java.util.stream.Collectors;

public class ItemTranslator {

    public static Item minecraftToViaVersion(final UserConnection user, final ItemStack stack, final int targetVersion) {
        final List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(ViaLoadingBase.getClassWrapper().getNativeVersion(), targetVersion);
        if (protocolPath == null) return null;

        final CreativeInventoryActionC2SPacket dummyPacket = new CreativeInventoryActionC2SPacket(36, stack);
        final PacketByteBuf emptyBuf = new PacketByteBuf(Unpooled.buffer());
        dummyPacket.write(emptyBuf);

        final Integer id = NetworkState.PLAY.getPacketId(NetworkSide.SERVERBOUND, dummyPacket);
        if (id == null) return null;

        final PacketWrapper wrapper = new PacketWrapperImpl(id, emptyBuf, user);
        try {
            wrapper.apply(Direction.SERVERBOUND, State.PLAY, 0, protocolPath.stream().map(ProtocolPathEntry::protocol).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return (Item) ((IPacketWrapperImpl) wrapper).viafabricplus_readableObjects().stream().filter(typeObjectPair -> Item.class.equals(typeObjectPair.key().getOutputClass())).findFirst().orElse(null).value();
    }
}
