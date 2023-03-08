/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.item;

import de.florianmichael.viafabricplus.settings.groups.GeneralSettings;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import net.minecraft.item.ItemGroups;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroups.class)
public class MixinItemGroups {

    @Unique
    private static ComparableProtocolVersion viafabricplus_version;

    @Unique
    private static boolean viafabricplus_state;

    @Redirect(method = "displayParametersMatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/featuretoggle/FeatureSet;equals(Ljava/lang/Object;)Z"))
    private static boolean adjustLastVersionMatchCheck(FeatureSet instance, Object o) {
        return instance.equals(o) && viafabricplus_version == ViaLoadingBase.getClassWrapper().getTargetVersion() && viafabricplus_state == GeneralSettings.getClassWrapper().removeNotAvailableItemsFromCreativeTab.getValue();
    }

    @Inject(method = "updateDisplayParameters", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroups;updateEntries(Lnet/minecraft/resource/featuretoggle/FeatureSet;Z)V", shift = At.Shift.BEFORE))
    private static void trackLastVersion(FeatureSet enabledFeatures, boolean operatorEnabled, CallbackInfoReturnable<Boolean> cir) {
        viafabricplus_version = ViaLoadingBase.getClassWrapper().getTargetVersion();
        viafabricplus_state = GeneralSettings.getClassWrapper().removeNotAvailableItemsFromCreativeTab.getValue();
    }
}
