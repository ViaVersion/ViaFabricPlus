// going to go crazy with comments, with enough i'll make it work %100, right?
package com.viaversion.viafabricplus.visuals.injection.mixin.classic.walking_animation; // declares the current package

import org.spongepowered.asm.mixin.Mixin; // imports the mixin class
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.viaversion.viafabricplus.visuals.settings.VisualSettings;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer; // imports the target class
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Mixin(LivingEntityRenderer.class) // class target for the mixin
// Nostalgic tweaks was a reference while making this code, i don't really don't do modern modding, so it was quite a good reference to help do this!
// check it out!: https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks/blob/1.21/common/src/main/java/mod/adrenix/nostalgic/mixin/tweak/animation/player/LivingEntityRendererMixin.java
// https://github.com/Nostalgica-Reverie/Nostalgic-Tweaks/blob/1.21/common/src/main/java/mod/adrenix/nostalgic/helper/animation/ClassicWalkHelper.java
// this code also takes a bit from c0.30 and birevan's port they made for me, thx if i haven't said that before again
public class MixinLivingEntityRenderer { // beginning of the mixin class
	
	float prevLimbYaw = 0.0F; // Jank that allows the previous limb yaw to be recorded
	@Inject( // start of the method injection
	        method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", // targets the render method
	        at = @At( // start of where it'll put the code
	            ordinal = 1, // instance number?
	            value = "INVOKE", // add method below to be called
	            target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V" // instance to place after
	        )
	    )
	    public <S extends LivingEntityRenderState> void oldBobbing(S livingEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo callback) { // method begins
			if (VisualSettings.INSTANCE.oldWalkingAnimation.isEnabled()) { // checks if the classic walking animation is enabled
				float var15; // ctrl c
				float var16; // ctrl v
				float var14 = 0.0625F; // another one
				//matrixStack.translate(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
				// from the port to a1.1.2_01 by birevan, modified to work
				var15 = prevLimbYaw + (livingEntityRenderState.limbSwingAmplitude - prevLimbYaw) * livingEntityRenderState.pitch;
				var16 = livingEntityRenderState.limbSwingAnimationProgress - livingEntityRenderState.limbSwingAmplitude/* * (1.0F - livingEntityRenderState.pitch)*/;
				if(var15 > 1.0F) {
					var15 = 1.0F;
				}
				
				float var1 = var16; // laziness
				float var2 = var15; // even more
				prevLimbYaw = livingEntityRenderState.limbSwingAmplitude; // the jank in question
				matrixStack.translate(0F, -Math.abs(Math.cos(var1 * 0.6662F)) * 0.5F * var2, 0F); // some math was stolen from nostalgic tweaks, sue me ;-), please actually don't, i don't know how copyright works
	    	}
	    }
}
