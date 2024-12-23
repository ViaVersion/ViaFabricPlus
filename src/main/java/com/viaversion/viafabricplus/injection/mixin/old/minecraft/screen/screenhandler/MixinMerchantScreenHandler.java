/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.old.minecraft.screen.screenhandler;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantScreenHandler.class)
public abstract class MixinMerchantScreenHandler extends ScreenHandler {

    @Shadow
    @Final
    private MerchantInventory merchantInventory;

    @Shadow
    public abstract TradeOfferList getRecipes();

    protected MixinMerchantScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "switchTo", at = @At("HEAD"), cancellable = true)
    private void onSwitchTo(int recipeId, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            ci.cancel();

            if (recipeId >= this.getRecipes().size()) {
                return;
            }

            final ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
            final ClientPlayerEntity player = MinecraftClient.getInstance().player;

            // move 1st input slot to inventory
            if (!this.merchantInventory.getStack(0).isEmpty()) {
                final int count = this.merchantInventory.getStack(0).getCount();
                interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, player);
                if (count == this.merchantInventory.getStack(0).getCount()) {
                    return;
                }
            }

            // move 2nd input slot to inventory
            if (!this.merchantInventory.getStack(1).isEmpty()) {
                final int count = this.merchantInventory.getStack(1).getCount();
                interactionManager.clickSlot(syncId, 1, 0, SlotActionType.QUICK_MOVE, player);
                if (count == this.merchantInventory.getStack(1).getCount()) {
                    return;
                }
            }

            // refill the slots
            if (this.merchantInventory.getStack(0).isEmpty() && this.merchantInventory.getStack(1).isEmpty()) {
                final TradeOffer tradeOffer = this.getRecipes().get(recipeId);
                this.viaFabricPlus$autofill(interactionManager, player, 0, tradeOffer.getFirstBuyItem());
                tradeOffer.getSecondBuyItem().ifPresent(item -> this.viaFabricPlus$autofill(interactionManager, player, 1, item));
            }
        }
    }

    @Inject(method = "canInsertIntoSlot", at = @At("HEAD"), cancellable = true)
    private void modifyCanInsertIntoSlot(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void viaFabricPlus$autofill(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, int inputSlot, TradedItem stackNeeded) {
        int slot;
        for (slot = 3; slot < 39; slot++) {
            final ItemStack itemStack = this.slots.get(slot).getStack();
            if (!itemStack.isEmpty() && stackNeeded.matches(itemStack)) {
                final ItemStack itemStack2 = this.merchantInventory.getStack(inputSlot);
                if (itemStack2.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
                    break;
                }
            }
        }
        if (slot == 39) {
            return;
        }

        final boolean wasHoldingItem = !player.currentScreenHandler.getCursorStack().isEmpty();
        interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
        interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP_ALL, player);
        interactionManager.clickSlot(syncId, inputSlot, 0, SlotActionType.PICKUP, player);
        if (wasHoldingItem) interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
    }

}
