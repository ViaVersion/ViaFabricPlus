package de.florianmichael.viafabricplus.injection.mixin.fixes;

import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class MixinCamera {

    @Shadow
    private float cameraY;

    @Shadow
    private float lastCameraY;

    @Shadow
    private Entity focusedEntity;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.BEFORE))
    public void onUpdateHeight(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!DebugSettings.getClassWrapper().replaceSneaking.getValue() && DebugSettings.getClassWrapper().sneakInstant.getValue()) {
            cameraY = lastCameraY = focusedEntity.getStandingEyeHeight();
        }
    }

    @Inject(method = "updateEyeHeight", at = @At(value = "HEAD"), cancellable = true)
    public void onUpdateEyeHeight(CallbackInfo ci) {
        if (this.focusedEntity == null) return;

        if (DebugSettings.getClassWrapper().replaceSneaking.getValue()) {
            ci.cancel();
            this.lastCameraY = this.cameraY;

            if (this.focusedEntity instanceof PlayerEntity player && !player.isSleeping()) {
                if (player.isSneaking()) {
                    cameraY = 1.54F;
                } else if (!DebugSettings.getClassWrapper().longSneaking.getValue()) {
                    cameraY = 1.62F;
                } else if (cameraY < 1.62F) {
                    float delta = 1.62F - cameraY;
                    delta *= 0.4;
                    cameraY = 1.62F - delta;
                }
            } else {
                cameraY = focusedEntity.getStandingEyeHeight();
            }
        }
    }
}
