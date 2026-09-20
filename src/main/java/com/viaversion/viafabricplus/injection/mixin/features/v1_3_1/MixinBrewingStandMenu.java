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

package com.viaversion.viafabricplus.injection.mixin.features.v1_3_1;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingStandMenu.class)
public abstract class MixinBrewingStandMenu extends AbstractContainerMenu {

    protected MixinBrewingStandMenu(@Nullable final MenuType<?> menuType, final int containerId) {
        super(menuType, containerId);
    }

    @Redirect(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean disableShiftClickIngredientSlot(Slot instance, ItemStack itemStack) {
        return instance.mayPlace(itemStack) && ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_3_1tor1_3_2);
    }

    @Redirect(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/PotionIngredient;isPotionInput(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/RecipeAccess;)Z"))
    private boolean disableShiftClickPotionSlot(ItemStack itemStack, RecipeAccess recipeAccess) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_2)) {
            return (itemStack.is(Items.POTION) || itemStack.is(Items.SPLASH_POTION) || itemStack.is(Items.LINGERING_POTION) || itemStack.is(Items.GLASS_BOTTLE)) && ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(LegacyProtocolVersion.r1_3_1tor1_3_2);
        }

        return PotionIngredient.isPotionInput(itemStack, recipeAccess);
    }

}
