package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_17to1_16_4;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets.EntityPackets;
import de.florianmichael.viafabricplus.definition.c0_30.ClassicWorldHeightInjection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings({"unchecked", "rawtypes"})
// Copyright RaphiMC/RK_01 - LICENSE file
@Mixin(value = EntityPackets.class, remap = false)
public abstract class MixinEntityPackets1_17 {

    @Redirect(method = "registerPackets", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_17to1_16_4/Protocol1_17To1_16_4;registerClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;Lcom/viaversion/viaversion/api/protocol/remapper/PacketHandler;)V"))
    private void handleClassicWorldHeight(Protocol1_17To1_16_4 instance, ClientboundPacketType packetType, PacketHandler packetHandler) {
        if (packetType == ClientboundPackets1_16_2.JOIN_GAME) packetHandler = ClassicWorldHeightInjection.handleJoinGame(packetHandler);
        if (packetType == ClientboundPackets1_16_2.RESPAWN) packetHandler = ClassicWorldHeightInjection.handleRespawn(packetHandler);

        ((Protocol) instance).registerClientbound(packetType, packetHandler);
    }
}
