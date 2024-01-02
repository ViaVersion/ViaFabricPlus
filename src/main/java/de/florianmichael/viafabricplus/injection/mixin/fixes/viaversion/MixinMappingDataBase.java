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

import com.viaversion.viaversion.api.data.MappingDataBase;
import de.florianmichael.viafabricplus.fixes.particle.FootStepParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MappingDataBase.class, remap = false)
public abstract class MixinMappingDataBase {

    @Shadow protected abstract int checkValidity(int id, int mappedId, String type);

    @Redirect(method = "getNewParticleId", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/data/MappingDataBase;checkValidity(IILjava/lang/String;)I"))
    private int passFoodStepParticle(MappingDataBase instance, int id, int mappedId, String type) {
        if (id == FootStepParticle.ID) {
            return id;
        } else {
            return checkValidity(id, mappedId, type);
        }
    }

}
