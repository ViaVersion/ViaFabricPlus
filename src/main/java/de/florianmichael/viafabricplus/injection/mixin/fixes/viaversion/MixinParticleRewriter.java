/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import de.florianmichael.viafabricplus.fixes.versioned.visual.FootStepParticle1_12_2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ParticleRewriter.class, remap = false)
public abstract class MixinParticleRewriter {

    @Shadow
    @Final
    private static List<?> particles;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void checkFootStepIdOverlap(CallbackInfo ci) {
        if (FootStepParticle1_12_2.ID < particles.size()) {
            throw new IllegalStateException("ViaFabricPlus FootStepParticle ID overlaps with a vanilla 1.12.2 particle ID");
        }
    }

    @ModifyArg(method = "add(I)V", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_13to1_12_2/data/ParticleRewriter$NewParticle;<init>(ILcom/viaversion/viaversion/protocols/protocol1_13to1_12_2/data/ParticleRewriter$ParticleDataHandler;)V"))
    private static int replaceIds(int id) {
        if (particles.size() == 8) { // minecraft:depthsuspend -> minecraft:mycelium
            return 32;
        } else if (particles.size() == 28) { // minecraft:footstep -> viafabricplus:footstep
            return FootStepParticle1_12_2.ID;
        } else {
            return id;
        }
    }

}
