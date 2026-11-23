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


package com.viaversion.viafabricplus.injection.mixin.features.v1_20_3.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandMenu.class)
public abstract class MixinBrewingStandMenu extends AbstractContainerMenu {

    @Shadow
    @Final
    private Slot ingredientSlot;

    protected MixinBrewingStandMenu(@Nullable final MenuType<?> menuType, final int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void legacyQuickMoveOrder(final Player player, final int slotIndex, final CallbackInfoReturnable<ItemStack> cir) {
        final ProtocolVersion version = ViaFabricPlus.api().targetVersion();
        if (version.newerThan(ProtocolVersion.v1_14_4) || slotIndex < 5) { // Moving out of the brewing stand slots never changed
            return;
        }

        final Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return;
        }

        final ItemStack stack = slot.getItem();
        final ItemStack clicked = stack.copy();
        if (!this.viaFabricPlus$moveLegacy(version, player, slotIndex, stack, clicked)) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == clicked.getCount()) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        slot.onTake(player, clicked);
        cir.setReturnValue(clicked);
    }

    @WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/PotionIngredient;isPotionInput(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/RecipeAccess;)Z"))
    private boolean requireSinglePotion(final ItemStack itemStack, final RecipeAccess recipeAccess, final Operation<Boolean> original) {
        return original.call(itemStack, recipeAccess) && viaFabricPlus$allowsPotionStack(ViaFabricPlus.api().targetVersion(), itemStack);
    }

    @Unique
    private boolean viaFabricPlus$moveLegacy(final ProtocolVersion version, final Player player, final int slotIndex, final ItemStack stack, final ItemStack clicked) {
        if (version.newerThanOrEqualTo(LegacyProtocolVersion.r1_3_1tor1_3_2)) { // Older versions only move between inventory and hotbar
            final boolean intoIngredient = version.olderThanOrEqualTo(ProtocolVersion.v1_10)
                ? !this.ingredientSlot.hasItem() && this.ingredientSlot.mayPlace(stack)
                : this.ingredientSlot.mayPlace(stack);
            if (intoIngredient) {
                return this.moveItemStackTo(stack, 3, 4, false);
            } else if (PotionIngredient.isPotionInput(clicked, player.level().recipeAccess()) && viaFabricPlus$allowsPotionStack(version, clicked)) {
                return this.moveItemStackTo(stack, 0, 3, false);
            } else if (BrewingStandMenu.FuelSlot.mayPlaceItem(clicked)) {
                return this.moveItemStackTo(stack, 4, 5, false);
            }
        }

        if (slotIndex < 32) {
            return this.moveItemStackTo(stack, 32, 41, false);
        } else {
            return this.moveItemStackTo(stack, 5, 32, false);
        }
    }

    @Unique
    private static boolean viaFabricPlus$allowsPotionStack(final ProtocolVersion version, final ItemStack itemStack) {
        return !version.betweenInclusive(ProtocolVersion.v1_12, ProtocolVersion.v1_20_3) || itemStack.getCount() == 1;
    }

}
