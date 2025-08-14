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

package com.viaversion.viafabricplus.injection.mixin.features.entity.interaction;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public abstract boolean isAlive();

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void removeLeashActions(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // Removes shearing of equipment & snipping all leashes + condition changes
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_5)) {
            final ItemStack itemStack = player.getStackInHand(hand);
            if (this.isAlive() && this instanceof final Leashable leashable) {
                if (leashable.getLeashHolder() != player) {
                    if (itemStack.isOf(Items.LEAD) && leashable.canBeLeashedTo(player)) {
                        itemStack.decrement(1);
                        cir.setReturnValue(ActionResult.SUCCESS);
                        return;
                    }
                } else {
                    cir.setReturnValue(ActionResult.SUCCESS.noIncrementStat());
                    return;
                }
            }

            cir.setReturnValue(ActionResult.PASS);
        }
    }

}
