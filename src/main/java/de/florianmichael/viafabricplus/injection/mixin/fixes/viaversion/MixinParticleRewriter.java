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

import com.viaversion.viaversion.api.minecraft.Particle;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import de.florianmichael.viafabricplus.fixes.particle.FootStepParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ParticleRewriter.class, remap = false)
public abstract class MixinParticleRewriter {

    @Unique
    private static int viaFabricPlus$particleIndex = 0;

    @ModifyArg(method = "add(I)V", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/protocols/protocol1_13to1_12_2/data/ParticleRewriter$NewParticle;<init>(ILcom/viaversion/viaversion/protocols/protocol1_13to1_12_2/data/ParticleRewriter$ParticleDataHandler;)V"))
    private static int replaceIds(int id) {
        viaFabricPlus$particleIndex++;
        final int oldId = viaFabricPlus$particleIndex - 1;

        if (oldId == 8) { // minecraft:depthsuspend -> minecraft:mycelium
            return 32;
        } else if (oldId == 28) { // minecraft:footstep -> viafabricplus:footstep
            return FootStepParticle.ID;
        } else {
            return id;
        }
    }

    @Inject(method = "rewriteParticle", at = @At("HEAD"), cancellable = true)
    private static void updateFootStepId(int particleId, Integer[] data, CallbackInfoReturnable<Particle> cir) {
        // Don't allow the server to send footstep particles directly as this would allow the server
        // to crash ViaFabricPlus clients without annoying vanilla clients
        if (particleId == FootStepParticle.ID) {
            cir.setReturnValue(null);
        }
    }

}
