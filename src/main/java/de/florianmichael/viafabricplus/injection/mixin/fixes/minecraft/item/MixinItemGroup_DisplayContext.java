/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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

import de.florianmichael.viafabricplus.injection.access.IItemGroup_DisplayContext;
import de.florianmichael.viafabricplus.settings.groups.GeneralSettings;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemGroup.DisplayContext.class)
public class MixinItemGroup_DisplayContext implements IItemGroup_DisplayContext {

    @Unique
    private static ComparableProtocolVersion viafabricplus_version;

    @Unique
    private static boolean viafabricplus_state;

    @Redirect(method = "doesNotMatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/featuretoggle/FeatureSet;equals(Ljava/lang/Object;)Z"))
    private boolean adjustLastVersionMatchCheck(FeatureSet instance, Object o) {
        return instance.equals(o) && viafabricplus_version == ViaLoadingBase.getClassWrapper().getTargetVersion() && viafabricplus_state == GeneralSettings.INSTANCE.removeNotAvailableItemsFromCreativeTab.getValue();
    }

    @Override
    public ComparableProtocolVersion viafabricplus_getVersion() {
        return viafabricplus_version;
    }

    @Override
    public void viafabricplus_setVersion(ComparableProtocolVersion comparableProtocolVersion) {
        viafabricplus_version = comparableProtocolVersion;
    }

    @Override
    public boolean viafabricplus_isState() {
        return viafabricplus_state;
    }

    @Override
    public void viafabricplus_setState(boolean state) {
        viafabricplus_state = state;
    }
}
