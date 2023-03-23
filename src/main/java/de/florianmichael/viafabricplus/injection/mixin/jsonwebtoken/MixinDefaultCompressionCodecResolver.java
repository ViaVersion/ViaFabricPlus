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
package de.florianmichael.viafabricplus.injection.mixin.jsonwebtoken;

import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import io.jsonwebtoken.impl.compression.DeflateCompressionCodec;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;

@Mixin(value = DefaultCompressionCodecResolver.class, remap = false)
public class MixinDefaultCompressionCodecResolver {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lio/jsonwebtoken/impl/lang/Services;loadAll(Ljava/lang/Class;)Ljava/util/List;"))
    public List<Object> fixServicesSupport(Class<Object> implementations) {
        return Arrays.asList(new GzipCompressionCodec(), new DeflateCompressionCodec());
    }
}
