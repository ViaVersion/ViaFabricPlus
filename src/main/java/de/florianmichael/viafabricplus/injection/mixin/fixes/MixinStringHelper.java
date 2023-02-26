package de.florianmichael.viafabricplus.injection.mixin.fixes;

import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StringHelper.class)
public class MixinStringHelper {

    @ModifyConstant(method = "truncateChat", constant = @Constant(intValue = 256))
    private static int expandChatLength(int constant) {
        return ChatLengthDefinition.getMaxLength();
    }
}
