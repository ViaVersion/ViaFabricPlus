package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.type.types.misc.TagType;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TagType.class, remap = false)
public abstract class MixinTagType {

    @Redirect(method = "read(Lio/netty/buffer/ByteBuf;)Lcom/viaversion/viaversion/libs/opennbt/tag/builtin/Tag;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/libs/opennbt/tag/limiter/TagLimiter;create(II)Lcom/viaversion/viaversion/libs/opennbt/tag/limiter/TagLimiter;"))
    private TagLimiter removeNBTSizeLimit(int maxBytes, int maxLevels) {
        return TagLimiter.noop();
    }

}
