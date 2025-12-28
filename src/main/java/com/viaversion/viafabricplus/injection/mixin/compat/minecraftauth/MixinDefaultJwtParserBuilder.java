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

package com.viaversion.viafabricplus.injection.mixin.compat.minecraftauth;

import io.jsonwebtoken.gson.io.GsonDeserializer;
import io.jsonwebtoken.impl.DefaultJwtParserBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*
 * JsonWebToken is used by MinecraftAuth, and since it's using Java services, it's not working with the fabric loader,
 * So we have to change all services usages by using the normal Java API
 */
@Mixin(value = DefaultJwtParserBuilder.class, remap = false)
public abstract class MixinDefaultJwtParserBuilder {

    @Redirect(method = "build()Lio/jsonwebtoken/JwtParser;", at = @At(value = "INVOKE", target = "Lio/jsonwebtoken/impl/lang/Services;get(Ljava/lang/Class;)Ljava/lang/Object;"))
    public Object removeServicesSupport(Class<?> spi) {
        return new GsonDeserializer<>();
    }

}
