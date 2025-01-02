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

package com.viaversion.viafabricplus.injection.mixin.features.networking.limitation.nbt;

import com.viaversion.nbt.limiter.TagLimiter;
import com.viaversion.viaversion.api.type.types.misc.NamedCompoundTagType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NamedCompoundTagType.class, remap = false)
public abstract class MixinNamedCompoundTagType {

    @Redirect(method = "read(Lio/netty/buffer/ByteBuf;Z)Lcom/viaversion/nbt/tag/CompoundTag;", at = @At(value = "INVOKE", target = "Lcom/viaversion/nbt/limiter/TagLimiter;create(II)Lcom/viaversion/nbt/limiter/TagLimiter;"))
    private static TagLimiter removeNBTSizeLimit(int maxBytes, int maxLevels) {
        return TagLimiter.noop();
    }

}