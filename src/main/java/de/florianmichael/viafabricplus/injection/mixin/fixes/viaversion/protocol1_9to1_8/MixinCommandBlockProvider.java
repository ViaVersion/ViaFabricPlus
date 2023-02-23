package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_9to1_8;

import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CommandBlockProvider.class)
public class MixinCommandBlockProvider {

    @ModifyConstant(method = "sendPermission", constant = @Constant(intValue = 26), remap = false)
    public int modifyPermissionLevel(int constant) {
        return 28;
    }
}
