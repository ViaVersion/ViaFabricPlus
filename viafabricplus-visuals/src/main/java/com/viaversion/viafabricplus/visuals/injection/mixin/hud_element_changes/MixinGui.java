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

package com.viaversion.viafabricplus.visuals.injection.mixin.hud_element_changes;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.CameraType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Gui.class)
public abstract class MixinGui {

    @Unique
    private static final int viaFabricPlusVisuals$ARMOR_ICON_WIDTH = 8;

    @Inject(method = "playAirBubblePoppedSound", at = @At("HEAD"), cancellable = true)
    private void disableBubblePopSound(int bubble, Player player, int burstBubbles, CallbackInfo ci) {
        if (VisualSettings.INSTANCE.removeBubblePopSound.getValue()) {
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "renderAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 2))
    private boolean disableEmptyBubbles(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        return !VisualSettings.INSTANCE.hideEmptyBubbleIcons.getValue();
    }

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean alwaysRenderCrosshair(CameraType instance, Operation<Boolean> original) {
        if (VisualSettings.INSTANCE.alwaysRenderCrosshair.isEnabled()) {
            return true;
        } else {
            return original.call(instance);
        }
    }

    @Inject(method = "willPrioritizeJumpInfo", at = @At("HEAD"), cancellable = true)
    private void removeMountJumpBar(CallbackInfoReturnable<Boolean> cir) {
        if (VisualSettings.INSTANCE.hideModernHUDElements.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void removeMountJumpBar(CallbackInfo ci) {
        if (VisualSettings.INSTANCE.hideModernHUDElements.isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "getVehicleMaxHearts", at = @At("HEAD"), cancellable = true)
    private void removeHungerBar(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (VisualSettings.INSTANCE.hideModernHUDElements.isEnabled()) {
            cir.setReturnValue(1);
        }
    }

    @ModifyExpressionValue(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;guiHeight()I"), require = 0)
    private int moveHealthDown(int value) {
        if (VisualSettings.INSTANCE.hideModernHUDElements.isEnabled()) {
            return value + 6; // Magical offset
        } else {
            return value;
        }
    }

    @ModifyArgs(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), require = 0)
    private static void moveArmorPositions(Args args, @Local(ordinal = 3, argsOnly = true) int x, @Local(ordinal = 6) int n) {
        if (!VisualSettings.INSTANCE.hideModernHUDElements.isEnabled()) {
            return;
        }

        final Minecraft client = Minecraft.getInstance();

        final int armorWidth = 10 * viaFabricPlusVisuals$ARMOR_ICON_WIDTH;
        final int offset = n * viaFabricPlusVisuals$ARMOR_ICON_WIDTH;

        args.set(2, client.getWindow().getGuiScaledWidth() - x - armorWidth + offset - 1);
        args.set(3, (int) args.get(3) + client.font.lineHeight + 1);
    }

    @ModifyArg(method = "renderAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"),
            index = 2, require = 0)
    private int moveAirBubbles(int value) {
        if (VisualSettings.INSTANCE.hideModernHUDElements.isEnabled()) {
            final Minecraft client = Minecraft.getInstance();
            return client.getWindow().getGuiScaledWidth() - value - client.font.lineHeight;
        } else {
            return value;
        }
    }

}
