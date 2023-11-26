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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.type.types.misc.TagType;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TagType.class, remap = false)
public abstract class MixinTagType {

    @Redirect(method = "read(Lio/netty/buffer/ByteBuf;)Lcom/viaversion/viaversion/libs/opennbt/tag/builtin/Tag;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/libs/opennbt/tag/limiter/TagLimiter;create(II)Lcom/viaversion/viaversion/libs/opennbt/tag/limiter/TagLimiter;"))
    private TagLimiter removeNBTSizeLimit(int maxBytes, int maxLevels) {
        return TagLimiter.noop();
    }

}
