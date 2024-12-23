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

package com.viaversion.viafabricplus.protocoltranslator.impl;

import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

import java.util.HashMap;
import java.util.Map;

public class ViaFabricPlusMappingDataLoader extends MappingDataLoader {

    public static final Map<String, Material> MATERIALS = new HashMap<>();
    public static final Map<String, Map<ProtocolVersion, String>> BLOCK_MATERIALS = new HashMap<>();

    public static final ViaFabricPlusMappingDataLoader INSTANCE = new ViaFabricPlusMappingDataLoader();

    private ViaFabricPlusMappingDataLoader() {
        super(ViaFabricPlusMappingDataLoader.class, "assets/viafabricplus/data/");

        final JsonObject materialsData = this.loadData("materials-1.19.4.json");
        for (Map.Entry<String, JsonElement> entry : materialsData.getAsJsonObject("materials").entrySet()) {
            final JsonObject materialData = entry.getValue().getAsJsonObject();
            MATERIALS.put(entry.getKey(), new Material(
                    materialData.get("blocksMovement").getAsBoolean(),
                    materialData.get("burnable").getAsBoolean(),
                    materialData.get("liquid").getAsBoolean(),
                    materialData.get("blocksLight").getAsBoolean(),
                    materialData.get("replaceable").getAsBoolean(),
                    materialData.get("solid").getAsBoolean()
            ));
        }
        for (Map.Entry<String, JsonElement> blockEntry : materialsData.getAsJsonObject("blocks").entrySet()) {
            final Map<ProtocolVersion, String> blockMaterials = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : blockEntry.getValue().getAsJsonObject().entrySet()) {
                blockMaterials.put(ProtocolVersion.getClosest(entry.getKey()), entry.getValue().getAsString());
            }
            BLOCK_MATERIALS.put(blockEntry.getKey(), blockMaterials);
        }
    }

    public static String getBlockMaterial(final Block block) {
        return getBlockMaterial(block, ProtocolTranslator.getTargetVersion());
    }

    public static String getBlockMaterial(final Block block, ProtocolVersion version) {
        if (version.newerThan(ProtocolVersion.v1_19_4)) {
            version = ProtocolVersion.v1_19_4;
        }

        final Map<ProtocolVersion, String> materials = BLOCK_MATERIALS.get(Registries.BLOCK.getId(block).toString());
        if (materials == null) {
            return null;
        }
        for (Map.Entry<ProtocolVersion, String> materialEntry : materials.entrySet()) {
            if (version.olderThanOrEqualTo(materialEntry.getKey())) {
                return materialEntry.getValue();
            }
        }
        return null;
    }

    public record Material(boolean blocksMovement, boolean burnable, boolean liquid, boolean blocksLight, boolean replaceable, boolean solid) {
    }

}
