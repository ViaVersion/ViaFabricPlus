/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.item.filter_creative_tabs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.features.item.filter_creative_tabs.VersionedRegistries;
import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.item.CreativeModeTab$ItemDisplayBuilder")
public abstract class MixinCreativeModeTab_ItemDisplayBuilder {

    @Shadow
    @Final
    private CreativeModeTab tab;

    @WrapOperation(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"))
    private boolean removeUnknownItems(Item instance, FeatureFlagSet featureSet, Operation<Boolean> original, @Local(argsOnly = true) ItemStack stack) {
        final boolean originalValue = original.call(instance, featureSet);
        final int index = GeneralSettings.INSTANCE.removeNotAvailableItemsFromCreativeTab.getIndex();

        if (index == 2 /* Off */ || Minecraft.getInstance().isLocalServer()) {
            return originalValue;
        } else if (index == 1 /* Vanilla only */ && !BuiltInRegistries.CREATIVE_MODE_TAB.getKey(this.tab).getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
            return originalValue;
        } else {
            return VersionedRegistries.keepItem(stack) && originalValue;
        }
    }

}
