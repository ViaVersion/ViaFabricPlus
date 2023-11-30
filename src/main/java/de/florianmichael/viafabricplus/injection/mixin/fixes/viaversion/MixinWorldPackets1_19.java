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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.Protocol1_19To1_18_2;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.packets.WorldPackets;
import de.florianmichael.viafabricplus.fixes.ClientsideFixes;
import de.florianmichael.viafabricplus.injection.access.IClientPlayerInteractionManager;
import de.florianmichael.viafabricplus.protocolhack.translator.BlockStateTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldPackets.class, remap = false)
public abstract class MixinWorldPackets1_19 {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_19to1_18_2/Protocol1_19To1_18_2;cancelClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;)V"))
    private static void handleLegacyAcknowledgePlayerDigging(Protocol1_19To1_18_2 instance, ClientboundPacketType clientboundPacketType) {
        instance.registerClientbound(ClientboundPackets1_18.ACKNOWLEDGE_PLAYER_DIGGING, ClientboundPackets1_19.PLUGIN_MESSAGE, wrapper -> {
            wrapper.resetReader();

            final var uuid = ClientsideFixes.executeSyncTask(data -> {
                try {
                    final var pos = data.readBlockPos();
                    final var blockState = BlockStateTranslator.via1_18_2toMc(data.readVarInt());
                    final var action = data.readEnumConstant(PlayerActionC2SPacket.Action.class);
                    final var allGood = data.readBoolean();

                    final IClientPlayerInteractionManager interactionManager = (IClientPlayerInteractionManager) MinecraftClient.getInstance().interactionManager;
                    interactionManager.viaFabricPlus$get1_18_2InteractionManager().handleBlockBreakAck(pos, blockState, action, allGood);
                } catch (Throwable t) {
                    throw new RuntimeException("Failed to handle BlockBreakAck packet data", t);
                }
            });
            wrapper.write(Type.STRING, ClientsideFixes.PACKET_SYNC_IDENTIFIER);
            wrapper.write(Type.STRING, uuid);
        });
    }

}
