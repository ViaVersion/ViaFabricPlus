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

package com.viaversion.viafabricplus.features.footstep_particle;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;

public final class FootStepParticle1_12_2 extends BillboardParticle {
    public static final Identifier ID = Identifier.of("viafabricplus", "footstep");
    public static int RAW_ID;

    static {
        final SimpleParticleType footStepType = FabricParticleTypes.simple(true);

        Registry.register(Registries.PARTICLE_TYPE, ID, footStepType);
        ParticleFactoryRegistry.getInstance().register(footStepType, FootStepParticle1_12_2.Factory::new);

        RAW_ID = Registries.PARTICLE_TYPE.getRawId(footStepType);
    }

    private FootStepParticle1_12_2(ClientWorld world, double x, double y, double z, Sprite sprite) {
        super(world, x, y, z, sprite);
        this.scale = 0.125F;
        this.setMaxAge(200);
    }

    @Override
    public void render(final BillboardParticleSubmittable submittable, final Camera camera, final Quaternionf rotation, final float tickProgress) {
        final float strength = ((float) this.age + tickProgress) / (float) this.maxAge;
        this.alpha = 2.0F - (strength * strength) * 2.0F;
        if (this.alpha > 1.0F) {
            this.alpha = 0.2F;
        } else {
            this.alpha *= 0.2F;
        }

        final Vec3d cameraPos = camera.getPos();
        final float x = (float) (MathHelper.lerp(tickProgress, this.lastX, this.x) - cameraPos.getX());
        final float y = (float) (MathHelper.lerp(tickProgress, this.lastY, this.y) - cameraPos.getY());
        final float z = (float) (MathHelper.lerp(tickProgress, this.lastZ, this.z) - cameraPos.getZ());
        this.renderVertex(submittable, new Quaternionf().rotateX(-1.5708F), x, y, z, tickProgress);
    }

    public static void init() {
        // Calls the static block
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(final SimpleParticleType parameters, final ClientWorld world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, final Random random) {
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
                throw new UnsupportedOperationException("FootStepParticle is not supported on versions newer than 1.12.2");
            } else {
                final Sprite sprite = spriteProvider.getSprite(random);
                final FootStepParticle1_12_2 particle = new FootStepParticle1_12_2(world, x, y, z, sprite);
                particle.setSprite(sprite);
                return particle;
            }
        }
    }
}
