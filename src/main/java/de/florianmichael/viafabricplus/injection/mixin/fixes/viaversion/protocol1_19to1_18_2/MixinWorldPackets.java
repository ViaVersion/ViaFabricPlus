/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_19to1_18_2;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_14_4to1_14_3.Protocol1_14_4To1_14_3;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.ClientboundPackets1_18;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.Protocol1_19To1_18_2;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.packets.WorldPackets;
import de.florianmichael.viafabricplus.definition.ClientPlayerInteractionManager1_18_2;
import de.florianmichael.viafabricplus.protocolhack.util.BlockStateTranslator;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldPackets.class, remap = false)
public class MixinWorldPackets {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_19to1_18_2/Protocol1_19To1_18_2;cancelClientbound(Lcom/viaversion/viaversion/api/protocol/packet/ClientboundPacketType;)V"))
    private static void passAcknowledgePlayerDigging(Protocol1_19To1_18_2 instance, ClientboundPacketType clientboundPacketType) {
        instance.registerClientbound(ClientboundPackets1_18.ACKNOWLEDGE_PLAYER_DIGGING, ClientboundPackets1_19.PLUGIN_MESSAGE, wrapper -> {
            wrapper.cancel();
            if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_14_4To1_14_3.class)) {
                return;
            }

            final var pos = wrapper.read(Type.POSITION1_14);
            final var blockState = Block.STATE_IDS.get(BlockStateTranslator.translateBlockState1_18(wrapper.read(Type.VAR_INT)));
            final var action = PlayerActionC2SPacket.Action.values()[wrapper.read(Type.VAR_INT)];
            final var allGood = wrapper.read(Type.BOOLEAN);

            MinecraftClient.getInstance().executeSync(() -> ClientPlayerInteractionManager1_18_2.handleBlockBreakAck(new BlockPos(pos.x(), pos.y(), pos.z()), blockState, action, allGood));
        });
    }
}
