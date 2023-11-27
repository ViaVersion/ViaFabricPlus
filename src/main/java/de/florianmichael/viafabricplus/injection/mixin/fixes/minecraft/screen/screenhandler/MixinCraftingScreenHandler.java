/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

import de.florianmichael.viafabricplus.fixes.data.recipe.Recipes1_11_2;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreenHandler.class)
public abstract class MixinCraftingScreenHandler extends AbstractRecipeScreenHandler<RecipeInputInventory> {

    @Shadow
    @Final
    private RecipeInputInventory input;

    public MixinCraftingScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/CraftingScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z", ordinal = 1))
    private boolean noShiftClickMoveIntoCraftingTable(CraftingScreenHandler instance, ItemStack itemStack, int startIndex, int endIndex, boolean fromLast) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_4)) return false;
        return this.insertItem(itemStack, startIndex, endIndex, fromLast);
    }

    @Inject(method = "onContentChanged", at = @At("HEAD"))
    private void clientSideCrafting(Inventory inventory, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_11_1to1_11_2)) {
            Recipes1_11_2.setCraftingResultSlot(syncId, this, input);
        }
    }

}
