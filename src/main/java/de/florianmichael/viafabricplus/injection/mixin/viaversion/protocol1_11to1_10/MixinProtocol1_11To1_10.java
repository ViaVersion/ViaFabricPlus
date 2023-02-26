package de.florianmichael.viafabricplus.injection.mixin.viaversion.protocol1_11to1_10;

import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "com.viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10$13", remap = false)
public class MixinProtocol1_11To1_10 {

    @ModifyConstant(method = "lambda$register$0", constant = @Constant(intValue = 100))
    private static int changeMaxChatLength(int constant) {
        return ChatLengthDefinition.getMaxLength();
    }
}
