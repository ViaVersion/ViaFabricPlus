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

package com.viaversion.viafabricplus.injection.mixin.features.interaction.r1_18_2_block_ack_emulation;

import com.viaversion.viafabricplus.base.sync_tasks.SyncTasks;
import com.viaversion.viafabricplus.injection.access.interaction.r1_18_2_block_ack_emulation.IClientPlayerInteractionManager;
import com.viaversion.viafabricplus.protocoltranslator.translator.BlockStateTranslator;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_17_1to1_18.packet.ClientboundPackets1_18;
import com.viaversion.viaversion.protocols.v1_18_2to1_19.Protocol1_18_2To1_19;
import com.viaversion.viaversion.protocols.v1_18_2to1_19.packet.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.v1_18_2to1_19.rewriter.WorldPacketRewriter1_19;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldPacketRewriter1_19.class, remap = false)
public abstract class MixinWorldPacketRewriter1_19 {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/v1_18_2to1_19/Protocol1_18_2To1_19;cancelClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;)V"))
    private static void handleLegacyAcknowledgePlayerDigging(Protocol1_18_2To1_19 instance, ClientboundPacketType clientboundPacketType) {
        instance.registerClientbound(ClientboundPackets1_18.BLOCK_BREAK_ACK, ClientboundPackets1_19.CUSTOM_PAYLOAD, wrapper -> {
            wrapper.resetReader();

            final String uuid = SyncTasks.executeSyncTask(data -> {
                try {
                    final BlockPos pos = data.readBlockPos();
                    final BlockState blockState = BlockStateTranslator.via1_18_2toMc(data.readVarInt());
                    final PlayerActionC2SPacket.Action action = data.readEnumConstant(PlayerActionC2SPacket.Action.class);
                    final boolean allGood = data.readBoolean();

                    final IClientPlayerInteractionManager mixinInteractionManager = (IClientPlayerInteractionManager) MinecraftClient.getInstance().interactionManager;
                    mixinInteractionManager.viaFabricPlus$get1_18_2InteractionManager().handleBlockBreakAck(pos, blockState, action, allGood);
                } catch (Throwable t) {
                    throw new RuntimeException("Failed to handle BlockBreakAck packet data", t);
                }
            });
            wrapper.write(Types.STRING, SyncTasks.PACKET_SYNC_IDENTIFIER);
            wrapper.write(Types.STRING, uuid);
        });
    }

}
