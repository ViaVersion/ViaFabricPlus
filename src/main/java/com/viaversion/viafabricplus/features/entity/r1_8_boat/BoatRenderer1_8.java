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

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

/**
 * Renderer for boats in 1.8 and lower.
 */
public final class BoatRenderer1_8 extends AbstractBoatRenderer {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("viafabricplus", "textures/boat1_8.png");
    private final BoatModel1_8 model;

    public BoatRenderer1_8(EntityRendererProvider.Context ctx) {
        super(ctx);
        shadowRadius = 0.5F;
        model = new BoatModel1_8(ctx.bakeLayer(BoatModel1_8.MODEL_LAYER));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return this.model.renderType(TEXTURE);
    }

    @Override
    public void submit(final BoatRenderState state, final PoseStack matrices, final SubmitNodeCollector orderedRenderCommandQueue, final CameraRenderState cameraRenderState) {
        matrices.pushPose();
        matrices.translate(0, 0.25, 0);
        matrices.mulPose(Axis.YP.rotationDegrees(180 - state.yRot));

        if (state.hurtTime > 0) {
            matrices.mulPose(Axis.XP.rotationDegrees(Mth.sin(state.hurtTime) * state.hurtTime * state.damageTime / 10 * state.hurtDir));
        }

        matrices.scale(-1, -1, 1);
        model.setupAnim(state);
        orderedRenderCommandQueue.submitModel(model, state, matrices, this.renderType(), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        matrices.popPose();
    }

}
