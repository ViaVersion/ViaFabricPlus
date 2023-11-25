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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.type.types.chunk.ChunkType1_8;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.logging.Level;

@Mixin(value = ChunkType1_8.class, remap = false)
public abstract class MixinChunkType1_8 {

    @Shadow
    public static Chunk deserialize(int chunkX, int chunkZ, boolean fullChunk, boolean skyLight, int bitmask, byte[] data) {
        return null;
    }

    @Redirect(method = "read(Lio/netty/buffer/ByteBuf;)Lcom/viaversion/viaversion/api/minecraft/chunks/Chunk;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/type/types/chunk/ChunkType1_8;deserialize(IIZZI[B)Lcom/viaversion/viaversion/api/minecraft/chunks/Chunk;"))
    private Chunk fixAegis(int chunkX, int chunkZ, boolean fullChunk, boolean skyLight, int bitmask, byte[] data) {
        try {
            return deserialize(chunkX, chunkZ, fullChunk, skyLight, bitmask, data);
        } catch (Throwable e) {
            Via.getPlatform().getLogger().log(Level.WARNING, "The server sent an invalid chunk data packet, returning an empty chunk", e);
            final ChunkSection[] airSections = new ChunkSection[16];
            for (int i = 0; i < airSections.length; i++) {
                airSections[i] = new ChunkSectionImpl(true);
                airSections[i].palette(PaletteType.BLOCKS).addId(0);
            }
            return new BaseChunk(chunkX, chunkZ, fullChunk, false, 65535, airSections, new int[256], new ArrayList<>());
        }
    }
}
