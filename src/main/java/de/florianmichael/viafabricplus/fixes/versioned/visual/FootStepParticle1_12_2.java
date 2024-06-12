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

package de.florianmichael.viafabricplus.fixes.versioned.visual;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FootStepParticle1_12_2 extends SpriteBillboardParticle {

    public static final Identifier ID = Identifier.of("viafabricplus", "footstep");
    public static int RAW_ID;

    protected FootStepParticle1_12_2(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);

        this.scale = 0.125F;
        this.setMaxAge(200);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        final float strength = ((float) this.age + tickDelta) / (float) this.maxAge;
        this.alpha = 2.0F - (strength * strength) * 2.0F;
        if (this.alpha > 1.0F) {
            this.alpha = 0.2F;
        } else {
            this.alpha *= 0.2F;
        }

        final Vec3d cameraPos = camera.getPos();
        final float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        final float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        final float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());

        final float minU = this.getMinU();
        final float maxU = this.getMaxU();
        final float minV = this.getMinV();
        final float maxV = this.getMaxV();

        final int light = this.getBrightness(tickDelta); // This is missing in the original code, that's why the particles are broken
        vertexConsumer.vertex(x - scale, y, z + scale).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(x + scale, y, z + scale).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(x + scale, y, z - scale).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(light);
        vertexConsumer.vertex(x - scale, y, z - scale).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light);
    }

    public static void init() {
        final SimpleParticleType footStepType = FabricParticleTypes.simple(true);

        Registry.register(Registries.PARTICLE_TYPE, ID, footStepType);
        ParticleFactoryRegistry.getInstance().register(footStepType, FootStepParticle1_12_2.Factory::new);

        RAW_ID = Registries.PARTICLE_TYPE.getRawId(footStepType);
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
                throw new UnsupportedOperationException("FootStepParticle is not supported on versions newer than 1.12.2");
            }

            final FootStepParticle1_12_2 particle = new FootStepParticle1_12_2(world, x, y, z);
            particle.setSprite(this.spriteProvider);
            return particle;
        }

    }

}
