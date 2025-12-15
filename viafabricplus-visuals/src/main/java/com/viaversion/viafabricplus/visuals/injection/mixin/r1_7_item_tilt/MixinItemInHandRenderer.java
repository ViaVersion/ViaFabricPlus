/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.visuals.injection.mixin.r1_7_item_tilt;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.ItemInHandRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.InteractionHand;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow
    protected abstract void applyItemArmAttackTransform(PoseStack matrices, HumanoidArm arm, float swingProgress);

    @Inject(method = "renderArmWithItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 2, shift = At.Shift.AFTER))
    private void applyFoodSwingOffset(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, CallbackInfo ci) {
        viaFabricPlusVisuals$applySwingOffset(player, hand, swingProgress, matrices);
    }

    @Inject(method = "renderArmWithItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 1, shift = At.Shift.AFTER))
    private void applyBowAndBlockingSwingOffset(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, CallbackInfo ci, @Local ItemUseAnimation itemUseAnimation) {
        if (itemUseAnimation == ItemUseAnimation.BOW || itemUseAnimation == ItemUseAnimation.BLOCK) {
            viaFabricPlusVisuals$applySwingOffset(player, hand, swingProgress, matrices);
        }
    }

    @Inject(method = "renderArmWithItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", ordinal = 1))
    private void slightlyTiltItemPosition(AbstractClientPlayer player, float tickProgress, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, CallbackInfo ci) {
        if (VisualSettings.INSTANCE.tiltItemPositions.isEnabled() && !(item.getItem() instanceof BlockItem)) {
            final HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            final int direction = arm == HumanoidArm.RIGHT ? 1 : -1;

            final float scale = 0.7585F / 0.86F;
            matrices.scale(scale, scale, scale);
            matrices.translate(direction * -0.084F, 0.059F, 0.08F);
            matrices.mulPose(Axis.YP.rotationDegrees(direction * 5.0F));
        }
    }

    @Unique
    private void viaFabricPlusVisuals$applySwingOffset(AbstractClientPlayer player, InteractionHand hand, float swingProgress, PoseStack matrices) {
        if (VisualSettings.INSTANCE.swingHandOnItemUse.isEnabled()) {
            final HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
            applyItemArmAttackTransform(matrices, arm, swingProgress);
        }
    }

}
