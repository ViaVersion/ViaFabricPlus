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
package de.florianmichael.viafabricplus.injection.mixin.fixes.vialegacy;

import de.florianmichael.viafabricplus.definition.c0_30.CustomClassicProtocolExtensions;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.data.ClassicProtocolExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClassicProtocolExtension.class, remap = false)
public class MixinClassicProtocolExtension {

    @Inject(method = "supportsVersion", at = @At("HEAD"), cancellable = true)
    public void allowExtensions_supportsVersion(int version, CallbackInfoReturnable<Boolean> cir) {
        if (CustomClassicProtocolExtensions.INSTANCE.ALLOWED_EXTENSIONS.contains((ClassicProtocolExtension) (Object) this))
            cir.setReturnValue(true);
    }

    @Inject(method = "isSupported", at = @At("HEAD"), cancellable = true)
    public void allowExtensions_isSupported(CallbackInfoReturnable<Boolean> cir) {
        if (CustomClassicProtocolExtensions.INSTANCE.ALLOWED_EXTENSIONS.contains((ClassicProtocolExtension) (Object) this))
            cir.setReturnValue(true);
    }

    @Inject(method = "getHighestSupportedVersion", at = @At("HEAD"), cancellable = true)
    public void allowExtensions_getHighestSupportedVersion(CallbackInfoReturnable<Integer> cir) {
        if (CustomClassicProtocolExtensions.INSTANCE.ALLOWED_EXTENSIONS.contains((ClassicProtocolExtension) (Object) this))
            cir.setReturnValue(1);
    }
}
