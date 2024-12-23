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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.item;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquippableComponent.class)
public abstract class MixinEquippableComponent {

    @Shadow
    @Final
    private EquipmentSlot slot;

    @Redirect(method = "equip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreative()Z"))
    private boolean removeCreativeCondition(PlayerEntity instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_20) && instance.isCreative();
    }

    @Inject(method = "equip", at = @At("HEAD"), cancellable = true)
    private void cancelArmorSwap(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_3)) {
            final ItemStack targetItem = player.getEquippedStack(this.slot);
            if (!targetItem.isEmpty()) {
                cir.setReturnValue(ActionResult.FAIL);
            }
        }
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
