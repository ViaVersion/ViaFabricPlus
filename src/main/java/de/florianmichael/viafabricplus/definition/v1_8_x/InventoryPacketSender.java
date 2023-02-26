package de.florianmichael.viafabricplus.definition.v1_8_x;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@SuppressWarnings("DataFlowIssue")
public class InventoryPacketSender {
    public static void sendOpenInventoryAchievement() throws Exception {
        final UserConnection viaConnection = MinecraftClient.getInstance().getNetworkHandler().getConnection().channel.attr(ViaFabricPlus.LOCAL_VIA_CONNECTION).get();

        if (viaConnection != null && ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
            final PacketWrapper clientStatus = PacketWrapper.create(ServerboundPackets1_9_3.CLIENT_STATUS, viaConnection);
            clientStatus.write(Type.VAR_INT, 2); // Open Inventory Achievement

            clientStatus.sendToServer(Protocol1_12To1_11_1.class);
        }
    }
}
