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

package com.viaversion.viafabricplus.injection.mixin.features.networking.packet_handling;

import com.viaversion.viafabricplus.injection.access.networking.packet_handling.IGameTestBlockHighlightRenderer;
import java.util.Map;
import net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameTestBlockHighlightRenderer.class)
public abstract class MixinGameTestBlockHighlightRenderer implements IGameTestBlockHighlightRenderer {

    @Shadow
    @Final
    private Map<BlockPos, GameTestBlockHighlightRenderer.Marker> markers;

    @Override
    public void viaFabricPlus$addMarker(final BlockPos pos, final int color, final String message, final int duration) {
        this.markers.put(pos, new GameTestBlockHighlightRenderer.Marker(color, message, Util.getMillis() + duration));
    }

}
