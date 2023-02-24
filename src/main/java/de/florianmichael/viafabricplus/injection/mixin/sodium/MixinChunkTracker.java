package de.florianmichael.viafabricplus.injection.mixin.sodium;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.ChunkTracker", remap = false)
public abstract class MixinChunkTracker {

    @Redirect(method = "recalculateChunks", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2IntOpenHashMap;get(J)I"))
    private int modifyRenderCondition(Long2IntOpenHashMap instance, long k) {
        return instance.getOrDefault(k, -1);
    }
}
