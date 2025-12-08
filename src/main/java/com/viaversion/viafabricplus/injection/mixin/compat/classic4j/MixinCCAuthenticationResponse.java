/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.compat.classic4j;

import de.florianmichael.classic4j.model.classicube.CCAuthenticationResponse;
import de.florianmichael.classic4j.model.classicube.CCError;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CCAuthenticationResponse.class, remap = false)
public abstract class MixinCCAuthenticationResponse {

    // Classic4J doesn't support translations, so we have to map them manually
    @Redirect(method = "getErrorDisplay", at = @At(value = "FIELD", target = "Lde/florianmichael/classic4j/model/classicube/CCError;description:Ljava/lang/String;"))
    private String mapTranslations(CCError instance) {
        return switch (instance) {
            case TOKEN -> Component.translatable("classic4j_library.viafabricplus.error.token").getString();
            case USERNAME -> Component.translatable("classic4j_library.viafabricplus.error.username").getString();
            case PASSWORD -> Component.translatable("classic4j_library.viafabricplus.error.password").getString();
            case VERIFICATION -> Component.translatable("classic4j_library.viafabricplus.error.verification").getString();
            case LOGIN_CODE -> Component.translatable("classic4j_library.viafabricplus.error.logincode").getString();
        };
    }

}
