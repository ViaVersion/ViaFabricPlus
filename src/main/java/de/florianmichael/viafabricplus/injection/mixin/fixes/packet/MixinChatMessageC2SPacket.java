package de.florianmichael.viafabricplus.injection.mixin.fixes.packet;

import de.florianmichael.viafabricplus.definition.ChatLengthDefinition;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatMessageC2SPacket.class)
public class MixinChatMessageC2SPacket {

    @ModifyConstant(method = "write", constant = @Constant(intValue = 256))
    public int expandChatLength(int constant) {
        return ChatLengthDefinition.getMaxLength();
    }
}
