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

package com.viaversion.viafabricplus.visuals.injection.mixin.strike_through_offset;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextRenderer.Drawer.class)
public abstract class MixinTextRenderer_Drawer {

    @Unique
    private static final float viaFabricPlusVisuals$offset = 0.5F; // Magical offset to revert the changes done in 1.13 pre6->1.13 pre7

    @ModifyArg(method = "accept(ILnet/minecraft/text/Style;Lnet/minecraft/client/font/BakedGlyph;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/EffectGlyph;create(FFFFFIIF)Lnet/minecraft/client/font/TextDrawable;"), index = 1)
    private float fixStrikethroughMinY(float value) {
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return value - viaFabricPlusVisuals$offset;
        } else {
            return value;
        }
    }

    @ModifyArg(method = "accept(ILnet/minecraft/text/Style;Lnet/minecraft/client/font/BakedGlyph;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/EffectGlyph;create(FFFFFIIF)Lnet/minecraft/client/font/TextDrawable;"), index = 3)
    private float fixStrikethroughMaxY(float value) {
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return value - viaFabricPlusVisuals$offset;
        } else {
            return value;
        }
    }

}
