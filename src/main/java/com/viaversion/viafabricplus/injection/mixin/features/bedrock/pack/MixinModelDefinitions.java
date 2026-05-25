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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.pack;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.injection.access.bedrock.pack.IModelDefinitions;
import net.raphimc.viabedrock.api.resourcepack.definition.ModelDefinitions;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Mixin(value = ModelDefinitions.class, remap = false)
public class MixinModelDefinitions implements IModelDefinitions {
    @Unique
    private final Map<String, BedrockGeometryModel> viaFabricPlus$blockModels = new HashMap<>();

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z", shift = At.Shift.BEFORE))
    public void readBlockModels(ResourcePackStorage resourcePackStorage, CallbackInfo ci, @Local String modelPath, @Local BedrockGeometryModel bedrockGeometry) {
        if (modelPath.startsWith("models/blocks/")) {
            this.viaFabricPlus$blockModels.put(bedrockGeometry.getIdentifier(), bedrockGeometry);
        }
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void addVanillaModels(ResourcePackStorage resourcePackStorage, CallbackInfo ci) throws IOException {
        this.viaFabricPlus$blockModels.put("minecraft:geometry.full_block", BedrockGeometryModel.fromJson(new String(ViaFabricPlus.class.getResourceAsStream("/assets/viafabricplus/bedrock/geometry.full_block.json").readAllBytes())).getFirst());
    }

    @Override
    public BedrockGeometryModel viaFabricPlus$getBlockModel(final String name) {
        return this.viaFabricPlus$blockModels.get(name);
    }
}
