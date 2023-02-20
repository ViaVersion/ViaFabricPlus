/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.Chunk1_8Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.logging.Level;

@Mixin(value = Chunk1_8Type.class, remap = false)
public abstract class MixinChunk1_8Type {

    @Shadow
    public static Chunk deserialize(int chunkX, int chunkZ, boolean fullChunk, boolean skyLight, int bitmask, byte[] data) {
        return null;
    }

    @Redirect(method = "read(Lio/netty/buffer/ByteBuf;Lcom/viaversion/viaversion/protocols/protocol1_9_3to1_9_1_2/storage/ClientWorld;)Lcom/viaversion/viaversion/api/minecraft/chunks/Chunk;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_9to1_8/types/Chunk1_8Type;deserialize(IIZZI[B)Lcom/viaversion/viaversion/api/minecraft/chunks/Chunk;"))
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
