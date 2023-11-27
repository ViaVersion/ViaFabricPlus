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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import de.florianmichael.viafabricplus.fixes.data.ItemRegistryDiff;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.item.ItemGroup$EntriesImpl")
public abstract class MixinItemGroup_EntriesImpl {

    @Shadow
    @Final
    private ItemGroup group;

    @Redirect(method = "add", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    private boolean removeUnknownItems(Item instance, FeatureSet featureSet) {
        final var index = GeneralSettings.global().removeNotAvailableItemsFromCreativeTab.getIndex();

        if (index == 2 || MinecraftClient.getInstance().isInSingleplayer()) return instance.isEnabled(featureSet);
        if (index == 1 && !Registries.ITEM_GROUP.getId(this.group).getNamespace().equals("minecraft")) return instance.isEnabled(featureSet);

        if (ItemRegistryDiff.keepItem(instance)) {
            return instance.isEnabled(featureSet);
        }
        return false;
    }

}
