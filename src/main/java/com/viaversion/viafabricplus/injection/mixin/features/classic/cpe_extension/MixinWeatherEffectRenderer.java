/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.classic.cpe_extension;

import com.viaversion.viafabricplus.features.classic.cpe_extension.CPEAdditions;
import java.util.List;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatherEffectRenderer.class)
public abstract class MixinWeatherEffectRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean forceSnow(List<?> instance) {
        if (CPEAdditions.isSnowing()) {
            return false;
        } else {
            return instance.isEmpty();
        }
    }

    @Inject(method = "getPrecipitationAt", at = @At(value = "HEAD"), cancellable = true)
    private void forceSnow(Level world, BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (CPEAdditions.isSnowing()) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        }
    }

}
