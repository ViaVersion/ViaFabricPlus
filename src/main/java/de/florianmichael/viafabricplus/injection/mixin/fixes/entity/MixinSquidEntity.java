package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SquidEntity.class)
public class MixinSquidEntity {

    @Inject(method = "canBeLeashedBy", at = @At("HEAD"), cancellable = true)
    public void injectCanBeLeashedBy(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            cir.setReturnValue(false);
        }
    }
}
