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

package com.viaversion.viafabricplus.features.v1_12_2;

import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.type.Types;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

public final class FootStepParticle1_12_2 extends SingleQuadParticle {

    private static final int PARTICLE_ID = 28;
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("viafabricplus", "footprint");

    private FootStepParticle1_12_2(final ClientLevel level, final double x, final double y, final double z) {
        super(level, x, y, z, Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.PARTICLES).getSprite(TEXTURE));

        this.quadSize = 0.125F;
        this.setLifetime(200);
    }

    public static PacketHandler handleLevelParticles(final PacketHandler parentHandler) {
        return wrapper -> {
            final int particleId = wrapper.passthrough(Types.INT);
            wrapper.rewindReader(1);
            if (particleId != PARTICLE_ID) {
                parentHandler.handle(wrapper);
                return;
            }

            wrapper.cancel();
            wrapper.read(Types.INT); // Particle ID
            wrapper.read(Types.BOOLEAN); // Long distance
            final float x = wrapper.read(Types.FLOAT);
            final float y = wrapper.read(Types.FLOAT);
            final float z = wrapper.read(Types.FLOAT);
            final float offsetX = wrapper.read(Types.FLOAT);
            final float offsetY = wrapper.read(Types.FLOAT);
            final float offsetZ = wrapper.read(Types.FLOAT);
            wrapper.read(Types.FLOAT); // Particle data
            final int count = wrapper.read(Types.INT);

            Minecraft.getInstance().execute(() -> spawn(x, y, z, offsetX, offsetY, offsetZ, count));
        };
    }

    private static void spawn(final double x, final double y, final double z, final double offsetX, final double offsetY, final double offsetZ, final int count) {
        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        if (count == 0) {
            Minecraft.getInstance().particleEngine.add(new FootStepParticle1_12_2(level, x, y, z));
            return;
        }

        final RandomSource random = level.getRandom();
        for (int i = 0; i < count; i++) {
            Minecraft.getInstance().particleEngine.add(new FootStepParticle1_12_2(level, x + random.nextGaussian() * offsetX, y + random.nextGaussian() * offsetY, z + random.nextGaussian() * offsetZ));
        }
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected void extractRotatedQuad(final QuadParticleRenderState submittable, final Camera camera, final Quaternionf rotation, final float tickProgress) {
        final float strength = ((float) this.age + tickProgress) / (float) this.lifetime;
        this.alpha = 2.0F - (strength * strength) * 2.0F;
        if (this.alpha > 1.0F) {
            this.alpha = 0.2F;
        } else {
            this.alpha *= 0.2F;
        }

        super.extractRotatedQuad(submittable, camera, new Quaternionf().rotateX(-Mth.HALF_PI), tickProgress);
    }

}
