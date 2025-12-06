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

package com.viaversion.viafabricplus.injection.mixin.features.interaction.container_clicking;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.ItemCost;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantMenu.class)
public abstract class MixinMerchantScreenHandler extends AbstractContainerMenu {

    @Shadow
    @Final
    private MerchantContainer tradeContainer;

    @Shadow
    public abstract MerchantOffers getOffers();

    protected MixinMerchantScreenHandler(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "tryMoveItems", at = @At("HEAD"), cancellable = true)
    private void onSwitchTo(int recipeId, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            ci.cancel();

            if (recipeId >= this.getOffers().size()) {
                return;
            }

            final MultiPlayerGameMode interactionManager = Minecraft.getInstance().gameMode;
            final LocalPlayer player = Minecraft.getInstance().player;

            // move 1st input slot to inventory
            if (!this.tradeContainer.getItem(0).isEmpty()) {
                final int count = this.tradeContainer.getItem(0).getCount();
                interactionManager.handleInventoryMouseClick(containerId, 0, 0, ClickType.QUICK_MOVE, player);
                if (count == this.tradeContainer.getItem(0).getCount()) {
                    return;
                }
            }

            // move 2nd input slot to inventory
            if (!this.tradeContainer.getItem(1).isEmpty()) {
                final int count = this.tradeContainer.getItem(1).getCount();
                interactionManager.handleInventoryMouseClick(containerId, 1, 0, ClickType.QUICK_MOVE, player);
                if (count == this.tradeContainer.getItem(1).getCount()) {
                    return;
                }
            }

            // refill the slots
            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                final MerchantOffer tradeOffer = this.getOffers().get(recipeId);
                this.viaFabricPlus$autofill(interactionManager, player, 0, tradeOffer.getItemCostA());
                tradeOffer.getItemCostB().ifPresent(item -> this.viaFabricPlus$autofill(interactionManager, player, 1, item));
            }
        }
    }

    @Inject(method = "canTakeItemForPickAll", at = @At("HEAD"), cancellable = true)
    private void modifyCanInsertIntoSlot(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void viaFabricPlus$autofill(MultiPlayerGameMode interactionManager, LocalPlayer player, int inputSlot, ItemCost stackNeeded) {
        int slot;
        for (slot = 3; slot < 39; slot++) {
            final ItemStack itemStack = this.slots.get(slot).getItem();
            if (!itemStack.isEmpty() && stackNeeded.test(itemStack)) {
                final ItemStack itemStack2 = this.tradeContainer.getItem(inputSlot);
                if (itemStack2.isEmpty() || ItemStack.isSameItemSameComponents(itemStack, itemStack2)) {
                    break;
                }
            }
        }
        if (slot == 39) {
            return;
        }

        final boolean wasHoldingItem = !player.containerMenu.getCarried().isEmpty();
        interactionManager.handleInventoryMouseClick(containerId, slot, 0, ClickType.PICKUP, player);
        interactionManager.handleInventoryMouseClick(containerId, slot, 0, ClickType.PICKUP_ALL, player);
        interactionManager.handleInventoryMouseClick(containerId, inputSlot, 0, ClickType.PICKUP, player);
        if (wasHoldingItem) interactionManager.handleInventoryMouseClick(containerId, slot, 0, ClickType.PICKUP, player);
    }

}
