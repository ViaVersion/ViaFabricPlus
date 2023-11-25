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

package de.florianmichael.viafabricplus.injection.mixin.compat.classic4j;

import de.florianmichael.classic4j.model.classicube.CCAuthenticationResponse;
import de.florianmichael.classic4j.model.classicube.CCError;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CCAuthenticationResponse.class, remap = false)
public abstract class MixinCCAuthenticationResponse {

    // Classic4J doesn't support translations, so we have to map them manually
    @Redirect(method = "getErrorDisplay", at = @At(value = "FIELD", target = "Lde/florianmichael/classic4j/model/classicube/CCError;description:Ljava/lang/String;"))
    private String mapTranslations(CCError instance) {
        switch (instance) {
            case TOKEN -> Text.translatable("classicube.viafabricplus.error.token").getString();
            case USERNAME -> Text.translatable("classicube.viafabricplus.error.username").getString();
            case PASSWORD -> Text.translatable("classicube.viafabricplus.error.password").getString();
            case VERIFICATION -> Text.translatable("classicube.viafabricplus.error.verification").getString();
            case LOGIN_CODE -> Text.translatable("classicube.viafabricplus.error.logincode").getString();
        }

        return instance.description;
    }

}
