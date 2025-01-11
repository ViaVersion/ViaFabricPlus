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

package com.viaversion.viafabricplus.injection.mixin.base.access;

import com.viaversion.viafabricplus.injection.access.base.bedrock.IChunkTracker;
import net.raphimc.viabedrock.api.chunk.BedrockChunk;
import net.raphimc.viabedrock.protocol.storage.ChunkTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(value = ChunkTracker.class, remap = false)
public abstract class MixinChunkTracker implements IChunkTracker {

    @Shadow
    @Final
    private Set<?> subChunkRequests;

    @Shadow
    @Final
    private Set<?> pendingSubChunks;

    @Shadow
    @Final
    private Map<Long, BedrockChunk> chunks;

    @Override
    public int viaFabricPlus$getSubChunkRequests() {
        return this.subChunkRequests.size();
    }

    @Override
    public int viaFabricPlus$getPendingSubChunks() {
        return this.pendingSubChunks.size();
    }

    @Override
    public int viaFabricPlus$getChunks() {
        return this.chunks.size();
    }

}
