/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MixinMobEntity {

    @Shadow
    protected abstract ActionResult interactWithItem(PlayerEntity player, Hand hand);

    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;interactWithItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult moveItemInteractionsAfterLeashing(MobEntity instance, PlayerEntity player, Hand hand) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            return ActionResult.FAIL;
        } else {
            return interactWithItem(player, hand);
        }
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", shift = At.Shift.BEFORE), cancellable = true)
    private void moveItemInteractionsAfterLeashing(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            final ActionResult result = interactWithItem(player, hand);
            if (result.isAccepted()) {
                cir.setReturnValue(result);
            }
        }
    }

}
