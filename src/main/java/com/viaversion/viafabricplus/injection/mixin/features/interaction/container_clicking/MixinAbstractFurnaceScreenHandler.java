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

package com.viaversion.viafabricplus.injection.mixin.features.interaction.container_clicking;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class MixinAbstractFurnaceScreenHandler {

    @Shadow
    protected abstract boolean isSmeltable(ItemStack itemStack);

    @Shadow
    protected abstract boolean isFuel(ItemStack itemStack);

    @Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;isSmeltable(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean disableShiftClickSmeltingSlot(AbstractFurnaceScreenHandler instance, ItemStack itemStack) {
        return this.isSmeltable(itemStack) && ProtocolTranslator.getTargetVersion().newerThan(LegacyProtocolVersion.r1_2_1tor1_2_3);
    }

    @Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;isFuel(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean disableShiftClickFuelSlot(AbstractFurnaceScreenHandler instance, ItemStack itemStack) {
        return this.isFuel(itemStack) && ProtocolTranslator.getTargetVersion().newerThan(LegacyProtocolVersion.r1_2_1tor1_2_3);
    }

}
