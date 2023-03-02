package de.florianmichael.viafabricplus.injection.mixin.fixes.visual;

import de.florianmichael.viafabricplus.settings.groups.VisualSettings;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class MixinBipedEntityModel<T extends LivingEntity> {

	@Shadow @Final public ModelPart rightArm;

	@Shadow @Final public ModelPart leftArm;

	@Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelPart;roll:F", ordinal = 1, shift = At.Shift.AFTER))
	public void addOldWalkAnimation(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		if (VisualSettings.getClassWrapper().oldWalkingAnimation.getValue()) {
			this.rightArm.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 2.0F * g;
			this.rightArm.roll = (MathHelper.cos(f * 0.2312F) + 1.0F) * 1.0F * g;

			this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g;
			this.leftArm.roll = (MathHelper.cos(f * 0.2812F) - 1.0F) * 1.0F * g;
		}
	}
}
