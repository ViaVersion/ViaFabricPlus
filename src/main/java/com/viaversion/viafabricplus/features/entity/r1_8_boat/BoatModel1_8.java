/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Model for boats in 1.8 and lower.
 */
public final class BoatModel1_8 extends EntityModel<BoatEntityRenderState> {

    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(Identifier.of("viafabricplus", "boat1_8"), "main");

    public BoatModel1_8(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        final float width = 24;
        final float wallHeight = 6;
        final float baseWidth = 20;
        final float pivotY = 4;
        root.addChild("bottom", ModelPartBuilder.create().uv(0, 8).cuboid(-width / 2, -baseWidth / 2 + 2, -3, width, baseWidth - 4, 4), ModelTransform.of(0, pivotY, 0, (float) Math.PI / 2, 0, 0));
        root.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), ModelTransform.of(-width / 2 + 1, pivotY, 0, 0, (float) Math.PI * 1.5f, 0));
        root.addChild("front", ModelPartBuilder.create().uv(0, 0).cuboid(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), ModelTransform.of(width / 2 - 1, pivotY, 0, 0, (float) Math.PI / 2, 0));
        root.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), ModelTransform.of(0, pivotY, -baseWidth / 2 + 1, 0, (float) Math.PI, 0));
        root.addChild("left", ModelPartBuilder.create().uv(0, 0).cuboid(-width / 2 + 2, -wallHeight - 1, -1, width - 4, wallHeight, 2), ModelTransform.pivot(0, pivotY, baseWidth / 2 - 1));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(BoatEntityRenderState state) {
    }

}
