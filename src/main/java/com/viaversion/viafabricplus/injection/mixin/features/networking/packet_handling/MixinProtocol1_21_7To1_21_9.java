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

package com.viaversion.viafabricplus.injection.mixin.features.networking.packet_handling;

import com.viaversion.viafabricplus.base.sync_tasks.SyncTasks;
import com.viaversion.viafabricplus.injection.access.networking.packet_handling.IGameTestDebugRenderer;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ClientboundPacket1_21_6;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ClientboundPackets1_21_6;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.packet.ServerboundPacket1_21_6;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.Protocol1_21_7To1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ClientboundPacket1_21_9;
import com.viaversion.viaversion.protocols.v1_21_7to1_21_9.packet.ServerboundPacket1_21_9;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocol1_21_7To1_21_9.class, remap = false)
public abstract class MixinProtocol1_21_7To1_21_9 extends AbstractProtocol<ClientboundPacket1_21_6, ClientboundPacket1_21_9, ServerboundPacket1_21_6, ServerboundPacket1_21_9> {

    public MixinProtocol1_21_7To1_21_9(final Class<ClientboundPacket1_21_6> unmappedClientboundPacketType, final Class<ClientboundPacket1_21_9> mappedClientboundPacketType, final Class<ServerboundPacket1_21_6> mappedServerboundPacketType, final Class<ServerboundPacket1_21_9> unmappedServerboundPacketType) {
        super(unmappedClientboundPacketType, mappedClientboundPacketType, mappedServerboundPacketType, unmappedServerboundPacketType);
    }

    @Inject(method = "registerPackets", at = @At("RETURN"))
    private void handleGameTestPayloads(CallbackInfo ci) {
        registerClientbound(ClientboundPackets1_21_6.CUSTOM_PAYLOAD, wrapper -> {
            final String channel = wrapper.passthrough(Types.STRING);
            if (channel.equals("minecraft:debug/game_test_add_marker")) {
                wrapper.resetReader();

                wrapper.set(Types.STRING, 0, SyncTasks.PACKET_SYNC_IDENTIFIER);
                wrapper.write(Types.STRING, SyncTasks.executeSyncTask(buf -> {
                    final BlockPos pos = buf.readBlockPos();
                    final int color = buf.readInt();
                    final String name = buf.readString();
                    final int duration = buf.readInt();

                    final IGameTestDebugRenderer mixinTestDebugRenderer = (IGameTestDebugRenderer) MinecraftClient.getInstance().worldRenderer.gameTestDebugRenderer;
                    mixinTestDebugRenderer.viaFabricPlus$addMarker(pos, color, name, duration);
                }));
            } else if (channel.equals("minecraft:debug/game_test_clear")) {
                wrapper.resetReader();

                wrapper.set(Types.STRING, 0, SyncTasks.PACKET_SYNC_IDENTIFIER);
                wrapper.write(Types.STRING, SyncTasks.executeSyncTask(buf -> MinecraftClient.getInstance().worldRenderer.gameTestDebugRenderer.clear()));
            }
        });
    }

}
