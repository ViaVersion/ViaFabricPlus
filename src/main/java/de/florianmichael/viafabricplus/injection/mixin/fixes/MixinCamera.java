/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes;

import de.florianmichael.viafabricplus.value.ValueHolder;
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
        if (!ValueHolder.replaceSneaking.getValue() && ValueHolder.sneakInstant.getValue()) {
            cameraY = lastCameraY = focusedEntity.getStandingEyeHeight();
        }
    }

    @Inject(method = "updateEyeHeight", at = @At(value = "HEAD"), cancellable = true)
    public void onUpdateEyeHeight(CallbackInfo ci) {
        if (this.focusedEntity == null) return;

        if (ValueHolder.replaceSneaking.getValue()) {
            ci.cancel();
            this.lastCameraY = this.cameraY;

            if (this.focusedEntity instanceof PlayerEntity player && !player.isSleeping()) {
                if (player.isSneaking()) {
                    cameraY = 1.54F;
                } else if (!ValueHolder.longSneaking.getValue()) {
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
