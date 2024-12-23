/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

import com.viaversion.viaversion.api.data.FullMappingsBase;
import com.viaversion.viaversion.api.data.Mappings;
import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viafabricplus.features.footstep_particle.FootStepParticle1_12_2;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ParticleMappings.class)
public abstract class MixinParticleMappings extends FullMappingsBase {

    public MixinParticleMappings(List<String> unmappedIdentifiers, List<String> mappedIdentifiers, Mappings mappings) {
        super(unmappedIdentifiers, mappedIdentifiers, mappings);
    }

    @Override
    public int getNewId(int id) {
        if (id == FootStepParticle1_12_2.RAW_ID) {
            return id;
        } else {
            return super.getNewId(id);
        }
    }

    @Override
    public String mappedIdentifier(int mappedId) {
        if (mappedId == FootStepParticle1_12_2.RAW_ID) {
            return "";
        } else {
            return super.mappedIdentifier(mappedId);
        }
    }
}
