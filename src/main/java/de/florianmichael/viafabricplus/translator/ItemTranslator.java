package de.florianmichael.viafabricplus.translator;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
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

            wrapper.read(Type.SHORT);
            return wrapper.read(Type.ITEM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
