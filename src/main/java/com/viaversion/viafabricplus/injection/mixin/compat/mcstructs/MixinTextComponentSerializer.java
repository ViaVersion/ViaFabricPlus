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

package com.viaversion.viafabricplus.injection.mixin.compat.mcstructs;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.serializer.TextComponentSerializer;
import com.viaversion.viaversion.libs.mcstructs.text.utils.LegacyGson;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TextComponentSerializer.class, remap = false)
public abstract class MixinTextComponentSerializer {

    @Shadow
    @Final
    private boolean legacyGson;

    @Shadow
    public abstract Gson getGson();

    /**
     * @author FlorianMichael/EnZaXD
     * @reason Fix legacy text component deserialization
     */
    @Overwrite
    public TextComponent deserialize(String json) {
        if (this.legacyGson) {
            LegacyGson.checkStartingType(json, true);
            json = LegacyGson.fixInvalidEscapes(json);
        }
        return this.getGson().fromJson(json, TextComponent.class);
    }

}
