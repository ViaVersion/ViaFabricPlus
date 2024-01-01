/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Equipment.class)
public interface MixinEquipment {

    @Redirect(method = "equipAndSwap", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreative()Z"))
    default boolean removeCreativeCondition(PlayerEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            return false;
        }
        return instance.isCreative();
    }

    @Inject(method = "equipAndSwap", at = @At("HEAD"), cancellable = true)
    private void cancelArmorSwap(Item item, World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_3)) {
            final ItemStack heldItem = user.getStackInHand(hand);
            final EquipmentSlot targetSlot = MobEntity.getPreferredEquipmentSlot(heldItem);
            final ItemStack targetItem = user.getEquippedStack(targetSlot);

            if (!targetItem.isEmpty()) {
                cir.setReturnValue(TypedActionResult.fail(heldItem));
            }
        }
    }

}
