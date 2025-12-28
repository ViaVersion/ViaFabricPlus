/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.features.entity.r1_8_boat;

import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

/**
 * Model for boats in 1.8 and lower.
 */
public final class BoatModel1_8 extends EntityModel<BoatRenderState> {

    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath("viafabricplus", "boat1_8"), "main");

    public BoatModel1_8(ModelPart root) {
        super(root);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();
        final float width = 24;
        final float wallHeight = 6;
        final float baseWidth = 20;
        final float pivotY = 4;
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 8).addBox(-width / 2, -baseWidth / 2 + 2, -3, width, baseWidth - 4, 4), PartPose.offsetAndRotation(0, pivotY, 0, (float) Math.PI / 2, 0, 0));
        root.addOrReplaceChild("back", CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), PartPose.offsetAndRotation(-width / 2 + 1, pivotY, 0, 0, (float) Math.PI * 1.5f, 0));
        root.addOrReplaceChild("front", CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), PartPose.offsetAndRotation(width / 2 - 1, pivotY, 0, 0, (float) Math.PI / 2, 0));
        root.addOrReplaceChild("right", CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), PartPose.offsetAndRotation(0, pivotY, -baseWidth / 2 + 1, 0, (float) Math.PI, 0));
        root.addOrReplaceChild("left", CubeListBuilder.create().texOffs(0, 0).addBox(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), PartPose.offset(0, pivotY, baseWidth / 2 - 1));
        return LayerDefinition.create(modelData, 64, 32);
    }

    @Override
    public void setupAnim(BoatRenderState state) {
    }

}
