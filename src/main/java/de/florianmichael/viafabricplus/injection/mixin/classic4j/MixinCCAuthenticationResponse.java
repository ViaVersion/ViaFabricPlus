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
package de.florianmichael.viafabricplus.injection.mixin.classic4j;

import de.florianmichael.classic4j.handler.classicube.auth.base.CCAuthenticationResponse;
import de.florianmichael.classic4j.model.classicube.highlevel.CCError;
import de.florianmichael.viafabricplus.integration.Classic4JImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CCAuthenticationResponse.class, remap = false)
public class MixinCCAuthenticationResponse {

    @Redirect(method = "getErrorDisplay", at = @At(value = "FIELD", target = "Lde/florianmichael/classic4j/model/classicube/highlevel/CCError;description:Ljava/lang/String;"))
    public String mapTranslations(CCError instance) {
        return Classic4JImpl.fromError(instance).getString();
    }
}
