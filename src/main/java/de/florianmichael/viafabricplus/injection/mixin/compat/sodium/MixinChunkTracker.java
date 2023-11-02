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
package de.florianmichael.viafabricplus.injection.mixin.compat.sodium;

import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.ChunkTracker", remap = false)
public abstract class MixinChunkTracker {

    @Redirect(method = "recalculateChunks", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/Long2IntOpenHashMap;get(J)I"))
    private int modifyRenderCondition(Long2IntOpenHashMap instance, long k) {
        if (VisualSettings.INSTANCE.fixSodiumChunkRendering.isEnabled()) {
            return instance.getOrDefault(k, -1);
        }

        return instance.get(k);
    }
}
