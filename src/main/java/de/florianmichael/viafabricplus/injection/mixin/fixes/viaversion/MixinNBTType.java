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
package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.type.types.minecraft.NBTType;
import com.viaversion.viaversion.libs.opennbt.tag.limiter.TagLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NBTType.class, remap = false)
public class MixinNBTType {

    @Unique
    private static final TagLimiter viafabricplus_tag_limiter = new TagLimiter() {
        private final int maxBytes = 2097152;
        private int bytes;

        @Override
        public void countBytes(int i) {
            this.bytes += bytes;
            if (this.bytes >= this.maxBytes) {
                throw new IllegalArgumentException("NBT data larger than expected (capped at " + this.maxBytes + ")");
            }
        }

        @Override
        public void checkLevel(int i) {}

        @Override
        public int maxBytes() {
            return this.maxBytes;
        }

        @Override
        public int maxLevels() {
            return 512; // Not used anymore
        }

        @Override
        public int bytes() {
            return this.bytes;
        }
    };

    @Redirect(method = "read(Lio/netty/buffer/ByteBuf;Z)Lcom/viaversion/viaversion/libs/opennbt/tag/builtin/CompoundTag;", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/libs/opennbt/tag/limiter/TagLimiter;create(II)Lcom/viaversion/viaversion/libs/opennbt/tag/limiter/TagLimiter;"))
    private static TagLimiter replaceTagLimiter(int maxBytes, int maxLevels) {
        return viafabricplus_tag_limiter;
    }
}
