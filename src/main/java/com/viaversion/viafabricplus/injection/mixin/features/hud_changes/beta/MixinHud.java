/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.hud_changes.beta;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Hud.class)
public abstract class MixinHud {

    @Unique
    private static final int viaFabricPlus$ARMOR_ICON_WIDTH = 8;

    @Inject(method = "willPrioritizeJumpInfo", at = @At("HEAD"), cancellable = true)
    private void removeMountJumpBar(CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void removeMountJumpBar(CallbackInfo ci) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "getVehicleMaxHearts", at = @At("HEAD"), cancellable = true)
    private void removeHungerBar(LivingEntity vehicle, CallbackInfoReturnable<Integer> cir) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            cir.setReturnValue(1);
        }
    }

    @ModifyExpressionValue(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;guiHeight()I"), require = 0)
    private int moveHealthDown(int value) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            return value + 7; // Magical offset
        } else {
            return value;
        }
    }

    @ModifyArgs(method = "extractArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), require = 0)
    private static void moveArmorPositions(Args args) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            final int width = 10 * viaFabricPlus$ARMOR_ICON_WIDTH;
            args.set(2, (int) args.get(2) + width + 21);
            args.set(3, (int) args.get(3) + 10);
        }
    }

    @ModifyArg(method = "extractAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"), index = 2, require = 0)
    private int moveAirBubbles(int value) {
        if (ViaFabricPlusImpl.impl().visuals().hideModernHUDElements().isActive()) {
            final Minecraft client = Minecraft.getInstance();
            return client.getWindow().getGuiScaledWidth() - value - client.font.lineHeight;
        } else {
            return value;
        }
    }

}
