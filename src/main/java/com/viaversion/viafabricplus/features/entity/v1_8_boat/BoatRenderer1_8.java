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

package com.viaversion.viafabricplus.features.entity.v1_8_boat;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * Renderer for boats in 1.8 and lower.
 */
public final class BoatRenderer1_8 extends AbstractBoatEntityRenderer {

    private static final Identifier TEXTURE = Identifier.of("viafabricplus", "textures/boat1_8.png");
    private final BoatModel1_8 model;

    public BoatRenderer1_8(EntityRendererFactory.Context ctx) {
        super(ctx);
        shadowRadius = 0.5F;
        model = new BoatModel1_8(ctx.getPart(BoatModel1_8.MODEL_LAYER));
    }

    @Override
    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.model;
    }

    @Override
    protected RenderLayer getRenderLayer() {
        return this.model.getLayer(TEXTURE);
    }

    @Override
    public void render(BoatEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0, 0.25, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - state.yaw));

        if (state.damageWobbleTicks > 0) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(state.damageWobbleTicks) * state.damageWobbleTicks * state.damageWobbleStrength / 10 * state.damageWobbleSide));
        }

        matrices.scale(-1, -1, 1);
        model.setAngles(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }

}
