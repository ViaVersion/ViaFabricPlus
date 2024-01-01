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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen.screenhandler;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
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
import net.minecraft.village.TradeOfferList;
import net.raphimc.vialoader.util.VersionEnum;
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
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            ci.cancel();

            if (recipeId >= this.getRecipes().size()) return;

            final ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
            final ClientPlayerEntity player = MinecraftClient.getInstance().player;

            // move 1st input slot to inventory
            if (!this.merchantInventory.getStack(0).isEmpty()) {
                final int count = this.merchantInventory.getStack(0).getCount();
                interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, player);
                if (count == this.merchantInventory.getStack(0).getCount()) return;
            }

            // move 2nd input slot to inventory
            if (!this.merchantInventory.getStack(1).isEmpty()) {
                final int count = this.merchantInventory.getStack(1).getCount();
                interactionManager.clickSlot(syncId, 1, 0, SlotActionType.QUICK_MOVE, player);
                if (count == this.merchantInventory.getStack(1).getCount()) return;
            }

            // refill the slots
            if (this.merchantInventory.getStack(0).isEmpty() && this.merchantInventory.getStack(1).isEmpty()) {
                this.viaFabricPlus$autofill(interactionManager, player, 0, this.getRecipes().get(recipeId).getAdjustedFirstBuyItem());
                this.viaFabricPlus$autofill(interactionManager, player, 1, this.getRecipes().get(recipeId).getSecondBuyItem());
            }
        }
    }

    @Inject(method = "canInsertIntoSlot", at = @At("HEAD"), cancellable = true)
    private void modifyCanInsertIntoSlot(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_13_2)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void viaFabricPlus$autofill(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player, int inputSlot, ItemStack stackNeeded) {
        if (stackNeeded.isEmpty()) return;

        int slot;
        for (slot = 3; slot < 39; slot++) {
            final ItemStack stack = slots.get(slot).getStack();
            if (ItemStack.canCombine(stack, stackNeeded)) {
                break;
            }
        }
        if (slot == 39) return;

        final boolean wasHoldingItem = !player.currentScreenHandler.getCursorStack().isEmpty();
        interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
        interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP_ALL, player);
        interactionManager.clickSlot(syncId, inputSlot, 0, SlotActionType.PICKUP, player);
        if (wasHoldingItem) interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
    }

}
