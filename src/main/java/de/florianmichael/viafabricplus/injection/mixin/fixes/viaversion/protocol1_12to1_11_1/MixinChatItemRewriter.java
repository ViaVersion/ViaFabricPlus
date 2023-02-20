package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_12to1_11_1;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.nbt.BinaryTagIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ChatItemRewriter;
import net.raphimc.vialegacy.util.ViaStringTagReader1_11_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = ChatItemRewriter.class, remap = false)
public abstract class MixinChatItemRewriter {

    @Redirect(method = "toClient", at = @At(value = "INVOKE", target = "Ljava/util/regex/Pattern;matcher(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;"))
    private static Matcher rewriteShowItem(Pattern pattern, CharSequence input) {
        try {
            final CompoundTag tag = ViaStringTagReader1_11_2.getTagFromJson(input.toString());
            input = BinaryTagIO.writeString(tag);
        } catch (Throwable e) {
            Via.getPlatform().getLogger().log(Level.WARNING, "Error converting 1.11.2 nbt to 1.12.2 nbt: '" + input + "'", e);
        }
        return Pattern.compile("$^").matcher(input);
    }
}
