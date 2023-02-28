package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion.protocol1_16_2to1_16_1;

import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets.WorldPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Copyright RaphiMC/RK_01 - GPL v3 LICENSE
@Mixin(value = WorldPackets.class, remap = false)
public abstract class MixinWorldPackets_2 {

    @ModifyConstant(method = "lambda$register$1", constant = @Constant(intValue = 16))
    private static int modifySectionCountToSupportClassicWorldHeight(int constant) {
        return 64;
    }
}
