package com.viaversion.viafabricplus.visuals.injection.mixin.classic.walking_animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(LivingEntityRenderer.class)
// Nostalgic tweaks was a reference while making this code, i don't really don't do modern modding, so it was quite a good reference to help do this!
// check it out!: https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks/blob/1.21/common/src/main/java/mod/adrenix/nostalgic/mixin/tweak/animation/player/LivingEntityRendererMixin.java
// https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks/blob/1.21/common/src/main/java/mod/adrenix/nostalgic/helper/animation/ClassicWalkHelper.java
// this code also takes a bit from c0.30 and birevan's port they made for me, thx if i haven't said that before again
public class MixinLivingEntityRenderer {

	@Inject(
			method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", // targets the render method
			at = @At(
					ordinal = 1,
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
					)
			)
	public <S extends LivingEntityRenderState> void oldBobbing(S livingEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo callback) {

		float pos = livingEntityRenderState.walkAnimationPos;
		float speed = livingEntityRenderState.walkAnimationSpeed;

		matrixStack.translate(
		    0.0F,
		    -Math.abs(Math.cos(pos * 0.6662F)) * 0.3125F * speed,
		    0.0F
		);

	}
}
