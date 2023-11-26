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

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.raphimc.vialoader.util.VersionEnum;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroups.class)
public abstract class MixinItemGroups {

    @Shadow
    @Nullable
    private static ItemGroup.@Nullable DisplayContext displayContext;

    @Shadow
    private static void updateEntries(ItemGroup.DisplayContext displayContext) {
    }

    @Unique
    private static VersionEnum viaFabricPlus$version;

    @Unique
    private static int viaFabricPlus$state;

    @Inject(method = "updateDisplayContext", at = @At("HEAD"), cancellable = true)
    private static void trackLastVersion(FeatureSet enabledFeatures, boolean operatorEnabled, RegistryWrapper.WrapperLookup lookup, CallbackInfoReturnable<Boolean> cir) {
        if (viaFabricPlus$version != ProtocolHack.getTargetVersion() || viaFabricPlus$state != GeneralSettings.global().removeNotAvailableItemsFromCreativeTab.getIndex()) {
            viaFabricPlus$version = ProtocolHack.getTargetVersion();
            viaFabricPlus$state = GeneralSettings.global().removeNotAvailableItemsFromCreativeTab.getIndex();

            displayContext = new ItemGroup.DisplayContext(enabledFeatures, operatorEnabled, lookup);
            updateEntries(displayContext);

            cir.setReturnValue(true);
        }
    }

}
