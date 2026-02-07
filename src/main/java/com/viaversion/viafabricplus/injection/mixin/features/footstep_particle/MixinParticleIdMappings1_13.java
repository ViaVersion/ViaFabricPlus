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

package com.viaversion.viafabricplus.injection.mixin.features.footstep_particle;

import com.viaversion.viafabricplus.features.footstep_particle.FootStepParticle1_12_2;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.data.ParticleIdMappings1_13;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ParticleIdMappings1_13.class, remap = false)
public abstract class MixinParticleIdMappings1_13 {

    @Shadow
    @Final
    private static List<?> particles;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void checkFootStepIdOverlap(CallbackInfo ci) {
        if (FootStepParticle1_12_2.RAW_ID < particles.size()) {
            throw new IllegalStateException("ViaFabricPlus FootStepParticle ID overlaps with a vanilla 1.12.2 particle ID");
        }
    }

    @ModifyArg(method = "add(I)V", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/v1_12_2to1_13/data/ParticleIdMappings1_13$NewParticle;<init>(ILcom/viaversion/viaversion/protocols/v1_12_2to1_13/data/ParticleIdMappings1_13$ParticleDataHandler;)V"))
    private static int replaceFootStepId(int id) {
        if (particles.size() == 28) { // minecraft:footstep -> viafabricplus:footstep
            return FootStepParticle1_12_2.RAW_ID;
        } else {
            return id;
        }
    }

}
