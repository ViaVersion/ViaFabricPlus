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

package com.viaversion.viafabricplus.injection.mixin.features.v1_11_1.screen;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.features.v1_11_1.FurnaceFuels1_11_2;
import com.viaversion.viafabricplus.features.v1_11_1.Recipes1_11_2;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceMenu.class)
public abstract class MixinAbstractFurnaceMenu {

    @Final
    @Shadow
    protected Level level;

    @Inject(method = "canSmelt", at = @At("HEAD"), cancellable = true)
    private void fixQuickMoveMaterial(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
            cir.setReturnValue(Recipes1_11_2.getRecipeManager(this.level.registryAccess().freeze()).getFirstMatch(RecipeType.SMELTING, new SingleRecipeInput(itemStack), this.level).isPresent());
        }
    }

    @Inject(method = "isFuel", at = @At("HEAD"), cancellable = true)
    private void fixQuickMoveFuel(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThan(ProtocolVersion.v1_14)) {
            cir.setReturnValue(FurnaceFuels1_11_2.isFuel(itemStack));
        }
    }

}
