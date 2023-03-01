package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {

    @Inject(method = "getPositionOffset*", at = @At("RETURN"), cancellable = true)
    private void injectGetPositionOffset(AbstractClientPlayerEntity player, float delta, CallbackInfoReturnable<Vec3d> ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            if (player.getSleepingDirection() != null) {
                ci.setReturnValue(ci.getReturnValue().subtract(player.getSleepingDirection().getOffsetX() * 0.4, 0, player.getSleepingDirection().getOffsetZ() * 0.4));
            }
        }
    }

    @Redirect(method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z"))
    private boolean redirectGetPositionOffset(AbstractClientPlayerEntity player) {
        return (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_11_1)) && player.isInSneakingPose();
    }
}
