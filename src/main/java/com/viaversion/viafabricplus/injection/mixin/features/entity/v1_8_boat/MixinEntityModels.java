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

package com.viaversion.viafabricplus.injection.mixin.features.entity.v1_8_boat;

import com.google.common.collect.ImmutableMap;
import com.viaversion.viafabricplus.features.entity.v1_8_boat.BoatModel1_8;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityModels.class)
public abstract class MixinEntityModels {

    @ModifyVariable(method = "getModels", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private static ImmutableMap.Builder<EntityModelLayer, TexturedModelData> addBoatModel(ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder) {
        return builder.put(BoatModel1_8.MODEL_LAYER, BoatModel1_8.getTexturedModelData());
    }

}
