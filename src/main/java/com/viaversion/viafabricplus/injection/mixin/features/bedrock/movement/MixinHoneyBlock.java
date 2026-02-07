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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.movement;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBlock.class)
public abstract class MixinHoneyBlock extends Block {

    public MixinHoneyBlock(final Properties settings) {
        super(settings);
    }

    @Shadow
    protected abstract boolean isSlidingDown(BlockPos pos, Entity entity);

    @Shadow
    protected abstract void maybeDoSlideEffects(Level world, Entity entity);

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void applyBedrockHoneyCollision(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl, CallbackInfo ci) {
        if (!ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return;
        }

        ci.cancel();
        if (this.isSlidingDown(pos, entity)) {
            this.maybeDoSlideEffects(world, entity);
        }

        final Vec3 velocity = entity.getDeltaMovement();
        entity.setDeltaMovement(new Vec3(velocity.x * 0.4F, Math.max(-0.12F, velocity.y), velocity.z * 0.4F));
    }

    @Override
    public void stepOn(final Level world, final BlockPos pos, final BlockState state, final Entity entity) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            final double absoluteY = Math.abs(entity.getDeltaMovement().y);
            if (absoluteY < 0.1 && !entity.isSteppingCarefully()) {
                final double frictionFactor = 0.4 + absoluteY * 0.2;
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(frictionFactor, 1.0F, frictionFactor));
            }
        } else {
            super.stepOn(world, pos, state, entity);
        }
    }

    @Override
    public float getFriction() {
        return ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) ? 0.8F : super.getFriction();
    }

    @Override
    public float getSpeedFactor() {
        return ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) ? 1F : super.getSpeedFactor();
    }

    @Override
    public float getJumpFactor() {
        return ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) ? 0.6F : super.getJumpFactor();
    }

    @Inject(method = {"getOldDeltaY", "getNewDeltaY"}, at = @At("HEAD"), cancellable = true)
    private static void simplifyVelocityComparisons(double d, CallbackInfoReturnable<Double> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            cir.setReturnValue(d);
        }
    }

}
