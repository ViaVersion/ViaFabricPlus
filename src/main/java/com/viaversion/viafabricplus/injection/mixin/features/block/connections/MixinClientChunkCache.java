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

package com.viaversion.viafabricplus.injection.mixin.features.block.connections;

import com.viaversion.viafabricplus.features.block.connections.BlockConnectionsEmulation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientChunkCache {

    @Shadow
    private ClientLevel level;

    @Inject(method = "updateLevelChunk", at = @At("TAIL"))
    private void updateBlockConnections(int chunkX, int chunkZ, ClientboundLevelChunkPacketData clientboundLevelChunkPacketData, CallbackInfo ci) {
        BlockConnectionsEmulation.updateChunkNeighborConnections(this.level, chunkX, chunkZ);
    }

    @Inject(method = "handleBlockUpdate", at = @At("TAIL"))
    private void updateBlockConnections(ClientboundBlockUpdatePacket clientboundBlockUpdatePacket, CallbackInfo ci) {
        BlockConnectionsEmulation.updateChunkNeighborConnections(this.level, clientboundBlockUpdatePacket.getPos());
    }

    @Inject(method = "handleLightUpdatePacket", at = @At("TAIL"))
    private void updateBlockConnections(ClientboundLightUpdatePacket clientboundLightUpdatePacket, CallbackInfo ci) {
        BlockConnectionsEmulation.updateChunkNeighborConnections(this.level, clientboundLightUpdatePacket.getX(), clientboundLightUpdatePacket.getZ());
    }

}
