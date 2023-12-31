/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

/**
 * Model for boats in 1.8 and lower.
 */
public class BoatModel1_8 extends CompositeEntityModel<BoatEntity> {

    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(new Identifier("viafabricplus", "boat1_8"), "main");
    private final ImmutableList<ModelPart> parts;

    public BoatModel1_8(ModelPart root) {
        this.parts = ImmutableList.of(root.getChild("bottom"), root.getChild("back"), root.getChild("front"), root.getChild("right"), root.getChild("left"));
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
    public Iterable<ModelPart> getParts() {
        return parts;
    }

    @Override
    public void setAngles(BoatEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

}
