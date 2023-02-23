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

// Copyright RaphiMC/RK_01 - GPL v3 LICENSE
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
